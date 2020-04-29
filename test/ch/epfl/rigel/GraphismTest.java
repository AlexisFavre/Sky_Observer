package ch.epfl.rigel;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.ZonedDateTime;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.gui.DateTimeBean;
import ch.epfl.rigel.gui.ObserverLocationBean;
import ch.epfl.rigel.gui.SkyCanvasManager;
import ch.epfl.rigel.gui.ViewingParametersBean;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GraphismTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        DateTimeBean observationTime = new DateTimeBean(ZonedDateTime.parse("2020-02-17T20:15:00+01:00"));
        ObserverLocationBean epfl = new ObserverLocationBean();
        epfl.setLonDeg(6.57);
        epfl.setLatDeg(46.52);
        ViewingParametersBean view = new ViewingParametersBean(HorizontalCoordinates.ofDeg(180 + 1.e-7, 22),
                68.4);

        SkyCanvasManager manager = new SkyCanvasManager(initCatalog(), observationTime, epfl, view);

        BorderPane bp = new BorderPane(manager.canvas());
        bp.setMinSize(800, 600);
        manager.canvas().widthProperty().bind(bp.widthProperty());
        manager.canvas().heightProperty().bind(bp.heightProperty());
        Scene scene = new Scene(bp);
        primaryStage.setScene(scene);
        primaryStage.setY(100);
        primaryStage.show();
        manager.canvas().requestFocus();
        manager.resetSky();
        
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