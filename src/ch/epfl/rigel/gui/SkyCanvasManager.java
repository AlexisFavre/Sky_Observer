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

public class SkyCanvasManager {
    
    private final static ClosedInterval CINTER_5TO90   = ClosedInterval.of(5, 90);
    private final static ClosedInterval CINTER_30TO150 = ClosedInterval.of(30, 150);
    
    private Canvas canvas;
    private SkyCanvasPainter painter;
    private GeographicCoordinates observerCoordinates = GeographicCoordinates.ofDeg(6.57, 46.52);
    
    private ObjectProperty<CartesianCoordinates> mousePosition = new SimpleObjectProperty<>(CartesianCoordinates.of(0, 0));
    
    public DoubleBinding mouseAzDeg;
    public DoubleBinding mouseAltDeg;
    public ObjectBinding<CelestialObject> objectUnderMouse; //TODO pourquoi en private

    public SkyCanvasManager(StarCatalogue catalog, DateTimeBean dtb, ObserverLocationBean olb, ViewingParametersBean vpb) {
        canvas = new Canvas(800, 600);
        painter = new SkyCanvasPainter(canvas);

        //LINKS =====================================================================================
        

        ObjectBinding<Transform> planeToCanvas = Bindings.createObjectBinding( 
                () -> Transform.affine(400/Math.tan(Angle.ofDeg(vpb.getFieldOfViewDeg())/4),
                        0, 0, -400/Math.tan(Angle.ofDeg(vpb.getFieldOfViewDeg())/4), 400, 300), vpb.fieldOfViewDegProperty());

        ObjectBinding<StereographicProjection> projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(vpb.getCenter()), vpb.centerProperty());

        ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition = Bindings.createObjectBinding(
                () -> {
                    HorizontalCoordinates h = null;
                    try {
                        Point2D mp = planeToCanvas.get().createInverse().transform(mousePosition.get().x(), mousePosition.get().y());
                        CartesianCoordinates mousePlanePosition = CartesianCoordinates.of(mp.getX(), mp.getY());
                        h = projection.get().inverseApply(mousePlanePosition);
                    } catch (NonInvertibleTransformException e) {
                        System.out.println("un-computable horizontal coordinates; cause: non invertible transformation");
                    }
                    return h;
                }, mousePosition);

        ObjectBinding<ObservedSky> sky = Bindings.createObjectBinding(
                () -> new ObservedSky(dtb.getZonedDateTime(), observerCoordinates, vpb.getCenter(), catalog), planeToCanvas, vpb.centerProperty());

        mouseAzDeg  = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().azDeg(), mouseHorizontalPosition);
        mouseAltDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().altDeg(), mouseHorizontalPosition);

        objectUnderMouse = Bindings.createObjectBinding(
                () -> {
                    CelestialObject object = null;
                    try {
                        Point2D mp = planeToCanvas.get().createInverse().transform(mousePosition.get().x(), mousePosition.get().y());
                        CartesianCoordinates mousePlanePosition = CartesianCoordinates.of(mp.getX(), mp.getY());
                        object = sky.get().objectClosestTo(mousePlanePosition, 0.05);
                    } catch (NonInvertibleTransformException e) {
                        System.out.println("un-computable horizontal coordinates; cause: non invertible transformation");
                    }
                    return object;
                }, mousePosition);

        objectUnderMouse.addListener((p, o, n) -> System.out.println(objectUnderMouse.get()));

        //RE_DRAW SKY VIA LISTENER ==================================================================
        sky.addListener((p, o, n)-> {
            painter.clear();
            painter.drawSky(n, planeToCanvas.get());
        });

        //KEY LISTENER ==============================================================================
        canvas.setOnKeyPressed(event -> {
            double az = vpb.getCenter().azDeg();
            double alt = vpb.getCenter().altDeg();
            switch (event.getCode()) {
                case UP:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(az, CINTER_5TO90.clip( alt + 5)));
                    break;
                case DOWN:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(az, CINTER_5TO90.clip(alt - 5)));
                    break;
                case RIGHT:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(RightOpenInterval.of(0, 360).reduce(az + 10), alt));
                    break;
                case LEFT: // TODO Reduce not working
                    vpb.setCenter(HorizontalCoordinates.ofDeg(RightOpenInterval.of(0, 360).reduce(az - 10), alt));
                    break;
                default:
                    break;
            }
            event.consume();
        });

        //MOUSE MOVE LISTENER =======================================================================
        canvas.setOnMouseMoved((event -> {

            mousePosition.setValue(CartesianCoordinates.of(event.getX(), event.getY()));
            event.consume();
        }));

        //SCROLL LISTENER ===========================================================================
        canvas.setOnScroll((event -> {
            double delta = Math.abs(event.getDeltaX()) > Math.abs(event.getDeltaY()) ? event.getDeltaX() : event.getDeltaY();
            vpb.setFieldOfViewDeg(CINTER_30TO150.clip(vpb.getFieldOfViewDeg() + delta));
            event.consume();
        }));

        painter.clear();
        painter.drawSky(sky.get(), planeToCanvas.get());
    }

    /**
     * @return the canvas managed by {@code this}
     */
    public Canvas canvas() {
        return canvas;
    }
}
