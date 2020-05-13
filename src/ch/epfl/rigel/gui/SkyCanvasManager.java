package ch.epfl.rigel.gui;

import java.util.Optional;

import static ch.epfl.rigel.math.RightOpenInterval.ROInter_0To360;
import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

/**
 * Use to create a canvas and control the sky painting on it
 * It defines a relation between canvas events and sky painting
 * (ex: scroll is zooming, arrows are orienting look)
 * And treats the events asked by the user to actualize sky painting
 *
 * @author Augustin ALLARD (299918)
 */
public final class SkyCanvasManager {
    
    private final static ClosedInterval CINTER_5TO90   = ClosedInterval.of(5, 90);
    private final static ClosedInterval CINTER_30TO150 = ClosedInterval.of(30, 150);
    private final static int MAX_DISTANCE_FOR_CLOSEST_OBJECT_TO = 10;
    private final static int CHANGE_OF_AZIMUT_WHEN_KEY_PRESSED = 10;
    private final static int CHANGE_OF_ALTITUDE_WHEN_KEY_PRESSED = 5;

    // TODO Replace
    private Canvas canvas;
    private SkyCanvasPainter painter;
    private ObserverLocationBean olb;
    private DateTimeBean dtb;
    private ViewingParametersBean vpb;
    
    private DoubleBinding mouseAzDeg;
    private DoubleBinding mouseAltDeg;
    private ObjectProperty<CartesianCoordinates> mousePosition;
    private ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition;
    private ObjectBinding<Optional<CelestialObject>> objectUnderMouse; //TODO pourquoi en private?

    private DoubleBinding scaleOfView;
    private ObjectBinding<ObservedSky> sky;
    private ObjectBinding<StereographicProjection> projection;
    private ObjectBinding<Transform> planeToCanvas;
    
    /**
     *
     * @param catalog the catalog of stars that will be painted on the canvas
     * @param dtb the {@code DateTimeBean} corresponding to the observation time
     * @param olb the {@code ObserverLocationBean} corresponding to the observer location on the earth
     * @param vpb the {@code ViewingParametersBean} corresponding to the observer view (zoom and look orientation)
     */
    public SkyCanvasManager(StarCatalogue catalog, DateTimeBean dtb, ObserverLocationBean olb, ViewingParametersBean vpb) {

        canvas = new Canvas();
        painter = new SkyCanvasPainter(canvas);
        this.olb = olb;
        this.dtb = dtb;
        this.vpb = vpb;
        
        mousePosition = new SimpleObjectProperty<>(CartesianCoordinates.of(0, 0)); // to initialize it

        //BLINDS =====================================================================================
        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(vpb.getCenter()), vpb.centerProperty());
        
        scaleOfView = Bindings.createDoubleBinding(() ->
                        Math.max(canvas.getWidth(), canvas.getHeight())
                        / projection.get().applyToAngle(Angle.ofDeg(vpb.getFieldOfViewDeg())),
                canvas.widthProperty(), canvas.heightProperty(), vpb.fieldOfViewDegProperty(), projection);

        planeToCanvas = Bindings.createObjectBinding(
                () -> Transform.affine(scaleOfView.get(), 0, 0, -scaleOfView.get(),
                            canvas.getWidth()/2, canvas.getHeight()/2), scaleOfView);

        mouseHorizontalPosition = Bindings.createObjectBinding(
                () -> projection.get().inverseApply(mousePosition.get()),
                        mousePosition, projection, planeToCanvas);

        sky = Bindings.createObjectBinding(
                () -> new ObservedSky(dtb.getZonedDateTime(), olb.getCoordinates(), vpb.getCenter(), catalog),
                        vpb.centerProperty(), olb.coordinatesProperty(), dtb.timeProperty(),
                        dtb.dateProperty(), dtb.zoneProperty());

        mouseAzDeg  = Bindings.createDoubleBinding(() ->
            mouseHorizontalPosition.get().azDeg(), mouseHorizontalPosition);
        
        mouseAltDeg = Bindings.createDoubleBinding(() ->
            mouseHorizontalPosition.get().altDeg(), mouseHorizontalPosition);

        objectUnderMouse = Bindings.createObjectBinding(
                () -> sky.get().
                objectClosestTo(mousePosition.get(), MAX_DISTANCE_FOR_CLOSEST_OBJECT_TO/scaleOfView.get()),
                mousePosition, planeToCanvas, sky);


        //RE_DRAW SKY VIA LISTENER ==================================================================
        sky.addListener(e-> painter.actualize(sky.get(), planeToCanvas.get()));
        planeToCanvas.addListener(e -> painter.actualize(sky.get(), planeToCanvas.get()));

        //KEYBOARD LISTENER ==============================================================================
        canvas.setOnKeyPressed(e -> {
            double az = vpb.getCenter().azDeg();
            double alt = vpb.getCenter().altDeg();
            switch (e.getCode()) {
                case UP:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(az, CINTER_5TO90.clip( alt + CHANGE_OF_ALTITUDE_WHEN_KEY_PRESSED)));
                    break;
                case DOWN:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(az, CINTER_5TO90.clip( alt - CHANGE_OF_ALTITUDE_WHEN_KEY_PRESSED)));
                    break;
                case RIGHT:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(ROInter_0To360.reduce( az + CHANGE_OF_AZIMUT_WHEN_KEY_PRESSED), alt));
                    break;
                case LEFT:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(ROInter_0To360.reduce( az - CHANGE_OF_AZIMUT_WHEN_KEY_PRESSED), alt));
                    break;
                default:
            }
            e.consume();
        });

        //MOUSE MOVE LISTENER =======================================================================
        canvas.setOnMouseMoved((e -> {
            try {
                Point2D mp = planeToCanvas.get().createInverse().transform(e.getX(), e.getY());
                mousePosition.setValue(CartesianCoordinates.of(mp.getX(), mp.getY()));
                
            } catch (NonInvertibleTransformException error) {
                System.out.println("un-computable mouse coordinates on plane; cause: non invertible transformation");
            }
            e.consume();
        }));

        //SCROLL LISTENER ===========================================================================
        canvas.setOnScroll(e -> {
            double delta = (Math.abs(e.getDeltaX()) > Math.abs(e.getDeltaY())) ? e.getDeltaX() : e.getDeltaY();
            vpb.setFieldOfViewDeg(CINTER_30TO150.clip(vpb.getFieldOfViewDeg() + delta));
            e.consume();
        });
        
        // MOUSE CLICKED LISTENER====================================================================
        canvas.setOnMouseClicked(e -> canvas.requestFocus());
        
    } //End Constructor

    // getters ======================================================================================
    
    /**
     * @return the canvas where the sky is drawn
     */
    public Canvas canvas() {
        return canvas;
    }
    
    /**
     * @return the ObserverLocationBean
     */
    public ObserverLocationBean observerLocationBean() {
        return olb;
    }
    
    /**
     * 
     * @return the DateTimeBean
     */
    public DateTimeBean dateTimeBean() {
        return dtb;
    }
    
    /**
     * @return the ViewingParametersBean
     */
    public ViewingParametersBean viewingParameterBean() {
        return vpb;
    }

    /**
     * @return the mouseAzDeg
     */
    public DoubleBinding mouseAzDeg() {
        return mouseAzDeg;
    }

    /**
     * @return the mouseAltDeg
     */
    public DoubleBinding mouseAltDeg() {
        return mouseAltDeg;
    }

    /**
     * @return the objectUnderMouse
     */
    public ObjectBinding<Optional<CelestialObject>> objectUnderMouse() {
        return objectUnderMouse;
    }
}
