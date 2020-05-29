package ch.epfl.rigel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.ZonedDateTime;

import javax.imageio.ImageIO;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.gui.SkyCanvasPainter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

public final class ExtractImage extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ZonedDateTime observationTime = ZonedDateTime.parse("2020-02-17T20:15:00+01:00");
        GeographicCoordinates observerCoordinates = GeographicCoordinates.ofDeg(6.57, 46.52);
        // Coordinates for planet & moon OBS
        //GeographicCoordinates observerCoord = GeographicCoordinates.ofDeg(-150, 20); //
        // Coordinates for Sun
        //GeographicCoordinates observerCoord = GeographicCoordinates.ofDeg(-100, 35);
        HorizontalCoordinates observerLook = HorizontalCoordinates.ofDeg(180, 45);
        
        //test frama : 
        // Sun : HorizontalCoordinates projCenter = HorizontalCoordinates.ofDeg(277, -23); 
        // Moon: HorizontalCoordinates projCenter = HorizontalCoordinates.ofDeg(3.7, -65); 
        // Moon2: HorizontalCoordinates projCenter = HorizontalCoordinates.ofDeg(0, 23);
        
        ObservedSky sky = new ObservedSky(observationTime, observerCoordinates, new StereographicProjection(observerLook), initCatalog());

        Canvas canvas = new Canvas(800, 600);
        SkyCanvasPainter skyPainter = new SkyCanvasPainter(canvas);

        Transform planeToCanvas = Transform.affine(1300, 0, 0, -1300, 400, 300);
        skyPainter.drawSky(sky, planeToCanvas);

        primaryStage.setScene(new Scene(new BorderPane(canvas)));
        primaryStage.show();
        WritableImage fxImage =
                canvas.snapshot(null, null);
                  BufferedImage swingImage =
                SwingFXUtils.fromFXImage(fxImage, null);
                  try {
                    ImageIO.write(swingImage, "png", new File("sky.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                Platform.exit();
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
