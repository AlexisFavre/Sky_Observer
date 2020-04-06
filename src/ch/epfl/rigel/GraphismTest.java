package ch.epfl.rigel;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.gui.SkyCanvasPainter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;

public class GraphismTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ZonedDateTime observationTime = ZonedDateTime.parse("2020-02-17T20:15:00+01:00");
        GeographicCoordinates observerCoord = GeographicCoordinates.ofDeg(6.57, 46.52);
        // Coordinates for planet & moon OBS
        //GeographicCoordinates observerCoord = GeographicCoordinates.ofDeg(-150, 20);
        HorizontalCoordinates projCenter = HorizontalCoordinates.ofDeg(180, 45);
        StereographicProjection projection = new StereographicProjection(projCenter);
        ObservedSky sky = new ObservedSky(observationTime, observerCoord, projection, initCatalog());

        Canvas canvas = new Canvas(800, 600);
        Transform planeToCanvas = Transform.affine(1300, 0, 0, -1300, 400, 300);
        SkyCanvasPainter skyPainter = new SkyCanvasPainter(canvas);
        skyPainter.drawStars(sky, projection, planeToCanvas);
        skyPainter.drawPlanets(sky, projection, planeToCanvas);
        skyPainter.drawMoon(sky, projection, planeToCanvas);

        primaryStage.setScene(new Scene(new BorderPane(canvas)));
        primaryStage.show();
    }

    private StarCatalogue initCatalog() {
        try (InputStream hygStream = getClass().getResourceAsStream("/hygdata_v3.csv");
             InputStream aStream = getClass().getResourceAsStream("/asterisms.txt")) {
            return new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .loadFrom(aStream, AsterismLoader.INSTANCE).build();
        } catch (IOException e) {
            return null;
        }
    }
}