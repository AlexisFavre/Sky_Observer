package ch.epfl.rigel;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.gui.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.ZonedDateTime;

public class GraphismTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        DateTimeBean observationTime = new DateTimeBean(ZonedDateTime.parse("2020-02-17T20:15:00+01:00"));
        TimeAnimator timeAnimator = new TimeAnimator(observationTime);
        timeAnimator.setAccelerator(NamedTimeAccelerator.SIDEREAL_DAY.getAccelerator());
        GeographicCoordinates observerCoordinates = GeographicCoordinates.ofDeg(6.57, 46.52);
        HorizontalCoordinates observerLook = HorizontalCoordinates.ofDeg(80, 22);

        Canvas canvas = new Canvas(800, 600);
        SkyCanvasPainter skyPainter = new SkyCanvasPainter(canvas);
        Transform planeToCanvas = Transform.affine(1300, 0, 0, -1300, 400, 300);

        Scene scene = new Scene(new BorderPane(canvas));
        primaryStage.setScene(scene);
        primaryStage.show();
        timeAnimator.start();

        observationTime.timeProperty().addListener((p, o, n) -> {
            ObservedSky sky = new ObservedSky(observationTime.getZonedDateTime(), observerCoordinates, observerLook, initCatalog());
            skyPainter.clear();
            skyPainter.drawSky(sky, planeToCanvas);
        });

        observationTime.dateProperty().addListener((p, o, n) -> {
            ObservedSky sky = new ObservedSky(observationTime.getZonedDateTime(), observerCoordinates, observerLook, initCatalog());
            skyPainter.clear();
            skyPainter.drawSky(sky, planeToCanvas);
        });

        scene.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
                switch(event.getCode()){
                    case SPACE:
                        if (timeAnimator.runningProperty().getValue())
                            timeAnimator.stop();
                        else
                            timeAnimator.start();
                        break;
                    case UP:
                        //observerLook = new HorizontalCoordinates(observerLook)
                        break;
                }
            }
        );

        // Coordinates for planet & moon OBS
        //GeographicCoordinates observerCoord = GeographicCoordinates.ofDeg(-150, 20); // TODO Verify moon size
        // Coordinates for Sun
        //GeographicCoordinates observerCoord = GeographicCoordinates.ofDeg(-100, 35);
    }

    private StarCatalogue initCatalog() {
        try (InputStream hygStream = getClass().getResourceAsStream("/hygdata_v3.csv");
             InputStream aStream = getClass().getResourceAsStream("/asterisms.txt")) {
            return new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .loadFrom(aStream, AsterismLoader.INSTANCE).build();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}