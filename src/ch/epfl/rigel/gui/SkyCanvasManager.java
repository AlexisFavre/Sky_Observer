package ch.epfl.rigel.gui;

import static ch.epfl.rigel.math.RightOpenInterval.ROInter_0To360;

import java.util.Optional;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
 * @author Alexis FAVRE (310552)
 */
public final class SkyCanvasManager {
    
    private final static ClosedInterval RANGE_OBSERVABLE_ALTITUDES   = ClosedInterval.of(5, 90);
    private final static ClosedInterval RANGE_FIELD_OF_VIEW_DEG = ClosedInterval.of(30, 150);
    private final static RightOpenInterval CINTER_0TO360 = RightOpenInterval.of(0, 360);
    private final static int MAX_DISTANCE_FOR_CLOSEST_OBJECT_TO = 10;
    private final static int CHANGE_OF_AZIMUT_WHEN_KEY_PRESSED = 10;
    private final static int CHANGE_OF_ALTITUDE_WHEN_KEY_PRESSED = 5;
    private final static CartesianCoordinates INITIAL_POS_MOUSE = CartesianCoordinates.of(0, 0);

    private final Canvas canvas;
    private final SkyCanvasPainter painter;
    private final ObserverLocationBean olb;
    private final DateTimeBean dtb;
    private final ViewingParametersBean vpb;
    
    private final BooleanProperty drawWithStars;
    private final BooleanProperty drawWithHorizon;
    private final BooleanProperty drawWithPlanets;
    private final BooleanProperty drawWithSun;
    private final BooleanProperty drawWithMoon;
    
    private final DoubleBinding mouseAzDeg;
    private final DoubleBinding mouseAltDeg;
    private final ObjectProperty<CartesianCoordinates> mousePosition;
    private final ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition;
    private final ObjectBinding<Optional<CelestialObject>> objectUnderMouse;

    private final DoubleBinding scaleOfView;
    private final ObjectBinding<ObservedSky> sky;
    private final ObjectBinding<StereographicProjection> projection;
    private final ObjectBinding<Transform> planeToCanvas;

    private ViewAnimator centerAnimator;
    
    /**
     *
     * @param catalog the catalog of stars that will be painted on the canvas
     * @param dtb the {@code DateTimeBean} corresponding to the observation time
     * @param olb the {@code ObserverLocationBean} corresponding to the observer location on the earth
     * @param vpb the {@code ViewingParametersBean} corresponding to the observer view (zoom and look orientation)
     */
    public SkyCanvasManager(StarCatalogue catalog, DateTimeBean dtb, ObserverLocationBean olb, ViewingParametersBean vpb) {

        centerAnimator = new ViewAnimator(vpb);
        canvas = new Canvas();
        painter = new SkyCanvasPainter(canvas);
        this.olb = olb;
        this.dtb = dtb;
        this.vpb = vpb;
        
        mousePosition = new SimpleObjectProperty<>(INITIAL_POS_MOUSE);
        
        drawWithStars   = new SimpleBooleanProperty();
        drawWithPlanets = new SimpleBooleanProperty();
        drawWithSun     = new SimpleBooleanProperty();
        drawWithMoon    = new SimpleBooleanProperty();
        drawWithHorizon = new SimpleBooleanProperty();

        //BINDINGS =====================================================================================
        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(vpb.getCenter()), vpb.centerProperty());
        
        scaleOfView = Bindings.createDoubleBinding(() ->
                        Math.max(canvas.getWidth(), canvas.getHeight())
                        / projection.get().applyToAngle(Angle.ofDeg(vpb.getFieldOfViewDeg())),
                                canvas.widthProperty(), canvas.heightProperty(), 
                                vpb.fieldOfViewDegProperty(), projection);

        planeToCanvas = Bindings.createObjectBinding(
                () -> Transform.affine(scaleOfView.get(), 0, 0, -scaleOfView.get(),
                            canvas.getWidth()/2, canvas.getHeight()/2),
                            scaleOfView, canvas.widthProperty(), canvas.heightProperty());

        mouseHorizontalPosition = Bindings.createObjectBinding(
                () -> projection.get().inverseApply(mousePosition.get()),
                            mousePosition, projection);

        sky = Bindings.createObjectBinding(
                () -> new ObservedSky(dtb.getZonedDateTime(), olb.getCoordinates(), projection.get(), catalog),
                            projection, olb.coordinatesProperty(), dtb.timeProperty(),
                            dtb.dateProperty(), dtb.zoneProperty());

        mouseAzDeg  = Bindings.createDoubleBinding( () ->
            mouseHorizontalPosition.get().azDeg(),
                mouseHorizontalPosition);
        
        mouseAltDeg = Bindings.createDoubleBinding( () ->
            mouseHorizontalPosition.get().altDeg(),
                mouseHorizontalPosition);

        objectUnderMouse = Bindings.createObjectBinding(
                () -> sky.get().
                objectClosestTo(mousePosition.get(), MAX_DISTANCE_FOR_CLOSEST_OBJECT_TO/scaleOfView.get()),
                    mousePosition, planeToCanvas, sky);


        //RE_DRAW SKY VIA LISTENER ==================================================================
        sky.addListener(e -> painter.actualize(sky.get(), planeToCanvas.get(), dtb.getTime(),
                drawWithStars.get(), drawWithPlanets.get(), drawWithSun.get(),
                drawWithMoon.get(), drawWithHorizon.get()));
        
        planeToCanvas.addListener(e -> painter.actualize(sky.get(), planeToCanvas.get(), dtb.getTime(),
                drawWithStars.get(), drawWithPlanets.get(), drawWithSun.get(),
                drawWithMoon.get(), drawWithHorizon.get()));

        //KEYBOARD LISTENER ==============================================================================
        canvas.setOnKeyPressed(e -> {
            double az  = vpb.getCenter().azDeg();
            double alt = vpb.getCenter().altDeg();
            switch (e.getCode()) {
                case UP:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(az, RANGE_OBSERVABLE_ALTITUDES.clip( alt + CHANGE_OF_ALTITUDE_WHEN_KEY_PRESSED)));
                    break;
                case DOWN:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(az, RANGE_OBSERVABLE_ALTITUDES.clip( alt - CHANGE_OF_ALTITUDE_WHEN_KEY_PRESSED)));
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

        //MOUSE CLICKED LISTENER ======================================================================
        canvas.setOnMousePressed((e -> {
            if(e.isPrimaryButtonDown()) {
                if(objectUnderMouse.get().isPresent() && canvas.isFocused()) {
                    HorizontalCoordinates mh = mouseHorizontalPosition.get();
                    centerAnimator.setDestination(CINTER_0TO360.reduce(mh.azDeg()), RANGE_OBSERVABLE_ALTITUDES.clip(mh.altDeg()));
                    centerAnimator.start();
                } else {
                    canvas.requestFocus();
                }
            }
            e.consume();
        }));

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
            double delta = (Math.abs(e.getDeltaX()) > Math.abs(e.getDeltaY())) 
                            ? e.getDeltaX() 
                            : e.getDeltaY();
            vpb.setFieldOfViewDeg(RANGE_FIELD_OF_VIEW_DEG.clip(vpb.getFieldOfViewDeg() + delta));
            e.consume();
        });
        
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

    public ViewAnimator centerAnimator() {
        return centerAnimator;
    }

    protected void goToDestinationWithName(String destination) {
        try {
            CartesianCoordinates destinationOnPlane = sky.get().pointForObjectWithName(destination);
            if (destinationOnPlane != null) {
                HorizontalCoordinates coordinates = projection.get().inverseApply(destinationOnPlane);
                centerAnimator.setDestination(coordinates.azDeg(), coordinates.altDeg());
                centerAnimator.start();
            } else
                System.out.println("Votre position sur la terre ne vous permet pas de voir cet astre");
        } catch (IllegalArgumentException e) {
            System.out.println("Cet astre n'est pas référencé");
        }
    }

    /**
     * @return the objectUnderMouse
     */
    public ObjectBinding<Optional<CelestialObject>> objectUnderMouse() {
        return objectUnderMouse;
    }


    /**
     * @return the drawWithStars property
     */
    public BooleanProperty drawWithStars() {
        return drawWithStars;
    }


    /**
     * @return the drawWithPlanets property
     */
    public BooleanProperty drawWithPlanets() {
        return drawWithPlanets;
    }


    /**
     * @return the drawWithSun property
     */
    public BooleanProperty drawWithSun() {
        return drawWithSun;
    }


    /**
     * @return the drawWithMoon property
     */
    public BooleanProperty drawWithMoon() {
        return drawWithMoon;
    }
    
    /**
     * @return the drawWithHorizon property
     */
    public BooleanProperty drawWithHorizon() {
        return drawWithHorizon;
    }
}
