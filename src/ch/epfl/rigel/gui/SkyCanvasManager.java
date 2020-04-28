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

    private Canvas canvas;
    private SkyCanvasPainter painter;
    private ObjectProperty<CartesianCoordinates> mousePosition = new SimpleObjectProperty<>(CartesianCoordinates.of(0, 0));
    /*private DoubleProperty mouseX = new SimpleDoubleProperty();
    private DoubleProperty mouseY = new SimpleDoubleProperty();*/

    public DoubleProperty mouseAzDeg = new SimpleDoubleProperty();
    public DoubleProperty mouseAltDeg = new SimpleDoubleProperty();
    public ObjectProperty<CelestialObject> objectUnderMouse  = new SimpleObjectProperty<>(null); //TODO mettre en private et faire getteurs

    public SkyCanvasManager(StarCatalogue catalog, DateTimeBean dtb, ObserverLocationBean olb, ViewingParametersBean vpb) {
        canvas = new Canvas(800, 600);
        painter = new SkyCanvasPainter(canvas);

        //LINKS =====================================================================================

        //objectUnderMouse = Bindings.createObjectBinding(sky.get().objectClosestTo(mousePosition.get(), 10) , mousePosition);
//        ObjectProperty<StereographicProjection> str = new SimpleObjectProperty<StereographicProjection>(new StereographicProjection(vpb.getCenter()));
//        projection = Bindings.createObjectBinding( () -> str, vpb);

        GeographicCoordinates observerCoordinates = GeographicCoordinates.ofDeg(6.57, 46.52);

        ObjectBinding<Transform> planeToCanvas = Bindings.createObjectBinding(
                () -> Transform.affine(400/Math.tan(Angle.ofDeg(vpb.getFieldOfViewDeg())/4),
                        0, 0, -400/Math.tan(Angle.ofDeg(vpb.getFieldOfViewDeg())/4), 400, 300), vpb.fieldOfViewDegProperty());

        ObjectBinding<StereographicProjection> projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(vpb.getCenter()), vpb.centerProperty());

        /*ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition = Bindings.createObjectBinding(
                () -> {
                    HorizontalCoordinates h = null;
                    try {
                        Point2D mp = planeToCanvas.get().createInverse().transform(mousePosition.get().x(), mousePosition.get().y());
                        CartesianCoordinates mousePlanePosition = CartesianCoordinates.of(mp.getX(), mp.getY());
                        h = projection.get().inverseApply(mousePlanePosition);
                        //mouseAzDeg.setValue(h.azDeg());
                        mouseAltDeg.setValue(h.altDeg());
                    } catch (NonInvertibleTransformException e) {
                        System.out.println("un-computable horizontal coordinates; cause: non invertible transformation");
                    }
                    return h;
                }, mousePosition);*/

       // DoubleBinding mAz = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().altDeg(), mouseHorizontalPosition);

        //mouseHorizontalPosition.addListener((p, o, n)-> System.out.println("BINGO"));
        //mouseX.addListener((p, o, n)-> System.out.println("hello"));

        //mousePosition.addListener((p, o, n)-> System.out.println("move"));

        mouseAltDeg.addListener((p, o, n) -> System.out.println("\n\n\n\n\n" + mouseAltDeg.get() + "\n\n\n\n\n"));

        ObjectBinding<ObservedSky> sky = Bindings.createObjectBinding(
                () -> new ObservedSky(dtb.getZonedDateTime(), observerCoordinates, vpb.getCenter(), catalog), planeToCanvas, vpb.centerProperty());

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
                    vpb.setCenter(HorizontalCoordinates.ofDeg(az, ClosedInterval.of(5, 90).clip( alt + 5)));
                    break;
                case DOWN:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(az, ClosedInterval.of(5, 90).clip(alt - 5)));
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

            HorizontalCoordinates mouseHorizontal = null;
            try {
                Point2D mp = planeToCanvas.get().createInverse().transform(mousePosition.get().x(), mousePosition.get().y());
                CartesianCoordinates mousePlanePosition = CartesianCoordinates.of(mp.getX(), mp.getY());
                mouseHorizontal = projection.get().inverseApply(mousePlanePosition);
                //mouseAzDeg.setValue(h.azDeg());
                mouseAltDeg.setValue(mouseHorizontal.altDeg());
            } catch (NonInvertibleTransformException e) {
                System.out.println("un-computable horizontal coordinates; cause: non invertible transformation");
            }

            System.out.println(mouseHorizontal);

            /*mouseX.setValue(event.getX());
            mouseY.setValue(event.getY());

            System.out.println(mouseX.get());
            System.out.println(mouseY.get());
            System.out.print("\n");*/
            event.consume();
        }));

        //SCROLL LISTENER ===========================================================================
        canvas.setOnScroll((event -> {
            double delta = Math.abs(event.getDeltaX()) > Math.abs(event.getDeltaY()) ? event.getDeltaX() : event.getDeltaY();
            vpb.setFieldOfViewDeg(ClosedInterval.of(30, 150).clip(vpb.getFieldOfViewDeg() + delta));
            event.consume();
        }));

        painter.clear();
        painter.drawSky(sky.get(), planeToCanvas.get());
    }

    public Canvas canvas() {
        return canvas;
    }
}
