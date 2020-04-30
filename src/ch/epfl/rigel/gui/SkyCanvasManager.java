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
import javafx.scene.canvas.Canvas;
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
    
    private final static ClosedInterval CINTER_5TO90   = ClosedInterval.of(5, 90);
    private final static ClosedInterval CINTER_30TO150 = ClosedInterval.of(30, 150);

    // TODO Replace
    private Canvas canvas;
    private SkyCanvasPainter painter;
    // coordinates of the mouse on the projection plane // TODO Be sure it is the good solution
    // TODO should be null
    private ObjectProperty<CartesianCoordinates> mousePosition = new SimpleObjectProperty<>(CartesianCoordinates.of(0, 0));
    
    public DoubleBinding mouseAzDeg;
    public DoubleBinding mouseAltDeg;
    public ObjectBinding<CelestialObject> objectUnderMouse; //TODO pourquoi en private?

    ObjectBinding<ObservedSky> sky;
    ObjectBinding<Transform> planeToCanvas;
    DoubleBinding scaleOfView;
    
    /**
     *
     * @param catalog the catalog of stars that will be painted on the canvas
     * @param dtb the {@code DateTimeBean} corresponding to the observation time
     * @param olb the {@code ObserverLocationBean} corresponding to the observer location on the earth
     * @param vpb the {@code ViewingParametersBean} corresponding to the observer view (zoom and look orientation)
     */
    public SkyCanvasManager(StarCatalogue catalog, DateTimeBean dtb, ObserverLocationBean olb, ViewingParametersBean vpb) {

        canvas = new Canvas(800, 600);
        painter = new SkyCanvasPainter(canvas);
        scaleOfView = Bindings.createDoubleBinding(() -> canvas.getWidth()/ (2*Math.tan(Angle.ofDeg(vpb.getFieldOfViewDeg())/4)),
                canvas.widthProperty(), vpb.fieldOfViewDegProperty());

        //LINKS =====================================================================================
        // TODO Introduce multiple canva forms ????
        planeToCanvas = Bindings.createObjectBinding(
                () -> {
                    double scaleOfView = canvas.getWidth()/ (2*Math.tan(Angle.ofDeg(vpb.getFieldOfViewDeg())/4));
                    return Transform.affine(scaleOfView, 0, 0, -scaleOfView,
                            canvas.getWidth()/2, canvas.getHeight()/2);
                }, vpb.fieldOfViewDegProperty(), canvas.heightProperty(), canvas.widthProperty());

        // TODO Why projection depends of plane to canvas
        ObjectBinding<StereographicProjection> projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(vpb.getCenter()), vpb.centerProperty());

        ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition = Bindings.createObjectBinding(
                () -> projection.get().inverseApply(mousePosition.get()), mousePosition, projection, planeToCanvas);

        sky = Bindings.createObjectBinding(
                () -> new ObservedSky(dtb.getZonedDateTime(), olb.getCoordinates(), vpb.getCenter(), catalog),
                // TODO work with center not with projection && how to do without plane to canva?
                planeToCanvas, vpb.centerProperty(), olb.coordinatesProperty(), dtb.timeProperty(), dtb.dateProperty(), dtb.zoneProperty());

        mouseAzDeg  = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().azDeg(), mouseHorizontalPosition);
        mouseAltDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().altDeg(), mouseHorizontalPosition);

        objectUnderMouse = Bindings.createObjectBinding(
                () ->  {
                    double scaleOfView = canvas.getWidth()/Math.tan(Angle.ofDeg(vpb.getFieldOfViewDeg())/4)/2; //TODO faire property pour le calc que 1fois
                    return sky.get().objectClosestTo(mousePosition.get(), 10/scaleOfView);
                }, mousePosition, planeToCanvas, sky);

        //PRINT CLOSEST OBJECT VIA LISTENER =========================================================
        // TODO Verify horizontal coordinates with zoom of planeToCanva ??
        //mouseAltDeg.addListener((p, o, n) -> System.out.println(mouseAzDeg.get() + "   " + mouseAltDeg.get()));
        objectUnderMouse.addListener((p, o, n) -> {if(objectUnderMouse.get() != null) 
            System.out.println(objectUnderMouse.get());});

        //RE_DRAW SKY VIA LISTENER ==================================================================
        // TODO Plane redessine
        sky.addListener((p, o, n)-> resetSky());

        //KEYBOARD LISTENER ==============================================================================
        canvas.setOnKeyPressed(event -> {
            double az = vpb.getCenter().azDeg();
            double alt = vpb.getCenter().altDeg();
            switch (event.getCode()) {
                case UP:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(az, CINTER_5TO90.clip( alt + 5)));
                    break;
                case DOWN:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(az, CINTER_5TO90.clip( alt - 5)));
                    break;
                case RIGHT:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(RightOpenInterval.of(0, 360).reduce( az + 10), alt));
                    break;
                case LEFT: // TODO Reduce not working
                    vpb.setCenter(HorizontalCoordinates.ofDeg(RightOpenInterval.of(0, 360).reduce( az - 10), alt));
                    break;
                default:
                    break;
            }
            event.consume();
        });

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
        canvas.widthProperty().addListener(e-> resetSky());
        canvas.heightProperty().addListener(e-> resetSky());
        canvas.requestFocus();
    }

    // TODO Border pane
    /**
     * @return the canvas managed by {@code this}
     */
    public Canvas canvas() {
        return canvas;
    }

    // TODO check
    public void resetSky() {
        painter.clear();
        painter.drawSky(sky.get(), planeToCanvas.get());
    }
}
