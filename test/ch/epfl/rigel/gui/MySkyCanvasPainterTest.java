package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.MyStarCatalogueTest;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.Transform;
import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;

class MySkyCanvasPainterTest {

    @Test
    void draw() {
        ZonedDateTime observationTime = ZonedDateTime.parse("2020-02-17T20:15:00+01:00");
        GeographicCoordinates observerCoord = GeographicCoordinates.ofDeg(6.57, 46.52);
        HorizontalCoordinates projCenter = HorizontalCoordinates.ofDeg(180, 45);
        StereographicProjection projection = new StereographicProjection(projCenter);
        ObservedSky sky = new ObservedSky(observationTime, observerCoord, projection, MyStarCatalogueTest.CATALOG);

        Canvas canvas = new Canvas(800, 600);
        Transform planeToCanvas = Transform.affine(1300, 0, 0, -1300, 400, 300);
        SkyCanvasPainter skyPainter = new SkyCanvasPainter(canvas);
        skyPainter.clear();
        skyPainter.drawStars(sky, projection, planeToCanvas);
    }
}