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
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.Transform;

public class SkyCanvasManager {

    private Canvas canvas;
    private SkyCanvasPainter painter;
    private ObjectBinding<StereographicProjection> projection;
    private ObjectBinding<Transform> planeToCanvas;
    private ObjectBinding<ObservedSky> sky;
    private ObjectProperty<CartesianCoordinates> mousePosition; //TODO must be in CartesianCoordinates ?
    private ObjectBinding<HorizontalCoordinates> mouseHorizonatlPosition;
    
    public DoubleProperty mouseAzDeg = new SimpleDoubleProperty();
    public DoubleProperty mouseAltDeg = new SimpleDoubleProperty();
    public ObjectProperty<CelestialObject> objectUnderMouse  = new SimpleObjectProperty<>(null);

    public SkyCanvasManager(StarCatalogue catalog, DateTimeBean dtb, ViewingParametersBean vpb, ObserverLocationBean olb) {
        canvas = new Canvas(800, 600);
        painter = new SkyCanvasPainter(canvas);
        //objectUnderMouse = Bindings.createObjectBinding(sky.get().objectClosestTo(mousePosition.get(), 10) , mousePosition);
//        ObjectProperty<StereographicProjection> str = new SimpleObjectProperty<StereographicProjection>(new StereographicProjection(vpb.getCenter()));
//        projection = Bindings.createObjectBinding( () -> str, vpb);

       /* GeographicCoordinates observerCoordinates = GeographicCoordinates.ofDeg(6.57, 46.52);
        ObservableObjectValue<> = Bindings.createObjectBinding(
                () -> new ObservedSky(dtb.getZonedDateTime(), observerCoordinates, vpb.getCenter(), catalog),
                dtb.getTime(), dtb.getDate(), dtb.getZone(), vpb.getCenter(), vpb.getField());*/

        //KEY LISTENER ==============================================================================
        canvas.setOnKeyPressed(e -> {
            double az = vpb.getCenter().azDeg();
            double alt = vpb.getCenter().altDeg();
            switch (e.getCode()) {
                case UP:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(az, ClosedInterval.of(5, 90).clip( alt + 5)));
                    break;
                case DOWN:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(az, ClosedInterval.of(5, 90).clip(alt - 5)));
                    break;
                case RIGHT:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(RightOpenInterval.of(0, 360).reduce(az + 10), alt));
                    break;
                case LEFT:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(RightOpenInterval.of(0, 360).reduce(az - 10), alt));
                    break;
                default:
                    break;
            }
            GeographicCoordinates observerCoordinates = GeographicCoordinates.ofDeg(6.57, 46.52);
            Transform planeToCanvas = Transform.affine(400/Math.tan(Angle.ofDeg(vpb.getField())/4),
                    0, 0, -400/Math.tan(Angle.ofDeg(vpb.getField())/4), 400, 300);
            ObservedSky sky = new ObservedSky(dtb.getZonedDateTime(), observerCoordinates, vpb.getCenter(), catalog);
            painter.clear();
            painter.drawSky(sky, planeToCanvas);
            System.out.println("event");
            e.consume();
        });

        // TODO Verify loop is normal

        //SCROLL LISTENER ===========================================================================
        canvas.setOnScroll((e -> {
            double delta = Math.abs(e.getDeltaX()) > Math.abs(e.getDeltaY()) ? e.getDeltaX() : e.getDeltaY();
            
            vpb.setField(vpb.getField() + delta);
            GeographicCoordinates observerCoordinates = GeographicCoordinates.ofDeg(6.57, 46.52);
            Transform planeToCanvas = Transform.affine(400/Math.tan(Angle.ofDeg(vpb.getField())/4),
                    0, 0, -400/Math.tan(Angle.ofDeg(vpb.getField())/4), 400, 300);
            ObservedSky sky = new ObservedSky(dtb.getZonedDateTime(), observerCoordinates, vpb.getCenter(), catalog);
            painter.clear();
            painter.drawSky(sky, planeToCanvas);
            System.out.println("event");
            e.consume();
        }));

    }

    public Canvas canvas() {
        return canvas;
    }
}
