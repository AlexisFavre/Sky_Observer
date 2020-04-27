package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.Transform;

public class SkyCanvasManager {

    Canvas canvas;
    SkyCanvasPainter painter;
    ObjectProperty<ObservedSky> sky = new SimpleObjectProperty<>();

    public SkyCanvasManager(StarCatalogue catalog, DateTimeBean dtb, ViewingParametersBean vpb) {
        canvas = new Canvas(800, 600);
        painter = new SkyCanvasPainter(canvas);

       /* GeographicCoordinates observerCoordinates = GeographicCoordinates.ofDeg(6.57, 46.52);
        ObservableObjectValue<> = Bindings.createObjectBinding(
                () -> new ObservedSky(dtb.getZonedDateTime(), observerCoordinates, vpb.getCenter(), catalog),
                dtb.getTime(), dtb.getDate(), dtb.getZone(), vpb.getCenter(), vpb.getField());*/

        //KEY LISTENER ==============================================================================
        canvas.setOnKeyPressed((event) -> {
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
                case LEFT:
                    // TODO Verify reduce for neg value
                    vpb.setCenter(HorizontalCoordinates.ofDeg(RightOpenInterval.of(0, 360).reduce(az - 10), alt));
                    break;
            }
            GeographicCoordinates observerCoordinates = GeographicCoordinates.ofDeg(6.57, 46.52);
            Transform planeToCanvas = Transform.affine(400/Math.tan(Angle.ofDeg(vpb.getField())/4),
                    0, 0, -400/Math.tan(Angle.ofDeg(vpb.getField())/4), 400, 300);
            ObservedSky sky = new ObservedSky(dtb.getZonedDateTime(), observerCoordinates, vpb.getCenter(), catalog);
            painter.clear();
            painter.drawSky(sky, planeToCanvas);
            System.out.println("event");
            event.consume();
        });

        // TODO Verify loop is normal

        //SCROLL LISTENER ===========================================================================
        canvas.setOnScroll((event -> {
            double delta;
            if(Math.abs(event.getDeltaX()) > Math.abs(event.getDeltaY())) {
                delta = event.getDeltaX();
            } else {
                delta = event.getDeltaY();
            }
            vpb.setField(vpb.getField() + delta);
            GeographicCoordinates observerCoordinates = GeographicCoordinates.ofDeg(6.57, 46.52);
            Transform planeToCanvas = Transform.affine(400/Math.tan(Angle.ofDeg(vpb.getField())/4),
                    0, 0, -400/Math.tan(Angle.ofDeg(vpb.getField())/4), 400, 300);
            ObservedSky sky = new ObservedSky(dtb.getZonedDateTime(), observerCoordinates, vpb.getCenter(), catalog);
            painter.clear();
            painter.drawSky(sky, planeToCanvas);
            System.out.println("event");
            event.consume();
        }));

    }

    public Canvas canvas() {
        return canvas;
    }
}
