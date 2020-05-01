package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import javax.script.SimpleBindings;
import java.time.LocalDateTime;

/**
 * Use to create a canvas and control the sky painting on it
 * It defines a relation between canvas events and sky painting
 * (ex: scroll is zooming, arrows are orienting look)
 * And treats the events asked by the user to actualize sky painting
 *
 * @author Augustin ALLARD (299918)
 */
public class SkyCanvasManager {

    private ViewAnimator centerAnimator;
    
    private final static ClosedInterval CINTER_5TO90   = ClosedInterval.of(5, 90);
    private final static ClosedInterval CINTER_30TO150 = ClosedInterval.of(30, 150);
    private final static RightOpenInterval CINTER_0TO360 = RightOpenInterval.of(0, 360);

    // TODO Replace
    private BorderPane pane;
    private SkyCanvasPainter painter;
    // coordinates of the mouse on the projection plane // TODO Be sure it is the good solution
    // TODO should be null
    private ObjectProperty<CartesianCoordinates> mousePosition = new SimpleObjectProperty<>(CartesianCoordinates.of(0, 0));
    private ObjectBinding<StereographicProjection> projection;
    private ObjectBinding<ObservedSky> sky;
    
    public DoubleBinding mouseAzDeg;
    public DoubleBinding mouseAltDeg;
    public ObjectBinding<CelestialObject> objectUnderMouse; //TODO pourquoi en private?
    
    /**
     *
     * @param catalog the catalog of stars that will be painted on the canvas
     * @param dtb the {@code DateTimeBean} corresponding to the observation time
     * @param olb the {@code ObserverLocationBean} corresponding to the observer location on the earth
     * @param vpb the {@code ViewingParametersBean} corresponding to the observer view (zoom and look orientation)
     */
    public SkyCanvasManager(StarCatalogue catalog, DateTimeBean dtb, ObserverLocationBean olb, ViewingParametersBean vpb) {

        centerAnimator = new ViewAnimator(vpb);

        Canvas canvas = new Canvas();
        pane = new BorderPane(canvas);
        pane.setPrefSize(800, 600);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        painter = new SkyCanvasPainter(canvas);
        //Node test;

        //LINKS =====================================================================================
        // TODO Why projection depends of plane to canvas
        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(vpb.getCenter()), vpb.centerProperty());
        
        DoubleBinding scaleOfView = Bindings.createDoubleBinding(() ->
                        Math.max(canvas.getWidth(), canvas.getHeight())/ projection.get().applyToAngle(Angle.ofDeg(vpb.getFieldOfViewDeg())),
                canvas.widthProperty(), canvas.heightProperty(), vpb.fieldOfViewDegProperty(), projection);

        ObjectBinding<Transform> planeToCanvas = Bindings.createObjectBinding(
                () -> Transform.affine(scaleOfView.get(), 0, 0, -scaleOfView.get(),
                            canvas.getWidth()/2, canvas.getHeight()/2)
                , scaleOfView);

        ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition = Bindings.createObjectBinding(
                () -> projection.get().inverseApply(mousePosition.get()), mousePosition, projection, planeToCanvas);

        sky = Bindings.createObjectBinding(
                () -> new ObservedSky(dtb.getZonedDateTime(), olb.getCoordinates(), vpb.getCenter(), catalog),
                vpb.centerProperty(), olb.coordinatesProperty(), dtb.timeProperty(), dtb.dateProperty(), dtb.zoneProperty());

        mouseAzDeg  = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().azDeg(), mouseHorizontalPosition);
        mouseAltDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().altDeg(), mouseHorizontalPosition);

        objectUnderMouse = Bindings.createObjectBinding(
                () -> sky.get().objectClosestTo(mousePosition.get(), 10/scaleOfView.get()),
                mousePosition, planeToCanvas, sky);

        //PRINT CLOSEST OBJECT VIA LISTENER =========================================================
        // TODO Verify horizontal coordinates with zoom of planeToCanva ??
        objectUnderMouse.addListener((p, o, n) -> {
            if(objectUnderMouse.get() != null && n != o)
                System.out.println(objectUnderMouse.get());
        });

        //RE_DRAW SKY VIA LISTENER ==================================================================
        // TODO Plane redessine
        sky.addListener(e-> painter.actualize(sky.get(), planeToCanvas.get()));
        planeToCanvas.addListener(e -> painter.actualize(sky.get(), planeToCanvas.get()));

        //KEYBOARD LISTENER ==============================================================================
        canvas.setOnKeyPressed(event -> {
            double az = vpb.getCenter().azDeg();
            double alt = vpb.getCenter().altDeg();
            if(!centerAnimator.runningProperty().getValue()) {
                switch (event.getCode()) {
                    case UP:
                        vpb.setCenter(HorizontalCoordinates.ofDeg(az, CINTER_5TO90.clip(alt + 1)));
                        break;
                    case DOWN:
                        vpb.setCenter(HorizontalCoordinates.ofDeg(az, CINTER_5TO90.clip(alt - 1)));
                        break;
                    case RIGHT:
                        vpb.setCenter(HorizontalCoordinates.ofDeg(CINTER_0TO360.reduce(az + 2), alt));
                        break;
                    case LEFT: // TODO Reduce not working
                        vpb.setCenter(HorizontalCoordinates.ofDeg(CINTER_0TO360.reduce(az - 2), alt));
                        break;
                    default:
                        break;
                }
            }
            event.consume();
        });

        //MOUSE CLICK LISTENER ======================================================================
        canvas.setOnMouseClicked((event -> {
            if(objectUnderMouse.get() != null) {
                HorizontalCoordinates mh = mouseHorizontalPosition.get();
                // TODO Verify north passage
                centerAnimator.setDestination(CINTER_0TO360.reduce(mh.azDeg()), CINTER_5TO90.clip(mh.altDeg()));
                centerAnimator.start();
            }
            event.consume();
        }));

        //MOUSE MOVE LISTENER =======================================================================
        canvas.setOnMouseMoved((event -> {
            try {
                
                Point2D mp = planeToCanvas.get().createInverse().transform(event.getX(), event.getY());
                mousePosition.setValue(CartesianCoordinates.of(mp.getX(), mp.getY()));
            } catch (NonInvertibleTransformException e) {
                System.out.println("un-computable mouse coordinates on plane; cause: non invertible transformation");
            }
            event.consume();
        }));

        //SCROLL LISTENER ===========================================================================
        canvas.setOnScroll((event -> {
            double delta = Math.abs(event.getDeltaX()) > Math.abs(event.getDeltaY()) ? event.getDeltaX() : event.getDeltaY();
            vpb.setFieldOfViewDeg(CINTER_30TO150.clip(vpb.getFieldOfViewDeg() + delta));
            event.consume();
        }));
        
        //ADAPTATION OF SKY WHEN SIZE OF STAGE CHANGE
        canvas.widthProperty().addListener(e-> painter.actualize(sky.get(), planeToCanvas.get()));
        canvas.heightProperty().addListener(e-> painter.actualize(sky.get(), planeToCanvas.get()));
    }

    // TODO Border pane
    /**
     * @return the pane containing the managed canvas
     */
    public BorderPane pane() {
        return pane;
    }

    /**
     * Request the focus directly on the canvas and not on the pane
     * To use after scene integration of the pane
     */
    public void focusOnCanvas() {
        //pane.getChildren().get(0).isFocusTraversable();
        pane.getChildren().get(0).requestFocus();
    }

    public ViewAnimator centerAnimator() {
        return centerAnimator;
    }

    protected void goToDestinationWithName(String destination) {
        CartesianCoordinates destinationOnPlane = sky.get().pointForObjectWithName(destination);
        if(destinationOnPlane != null) {
            HorizontalCoordinates coordinates = projection.get().inverseApply(destinationOnPlane);
            centerAnimator.setDestination(coordinates.azDeg(), coordinates.altDeg());
            centerAnimator.start();
        }
        else
            System.out.println("Astre introuvable");
    }
}
