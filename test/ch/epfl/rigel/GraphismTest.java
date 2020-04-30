package ch.epfl.rigel;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.ZonedDateTime;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.gui.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

public class GraphismTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    final StarCatalogue CATALOG = initCatalog();

    @Override
    public void start(Stage primaryStage) {

        DateTimeBean observationTime = new DateTimeBean(ZonedDateTime.parse("2020-02-17T20:15:00+01:00"));
        ObserverLocationBean epfl = new ObserverLocationBean();
        epfl.setLonDeg(6.57);
        epfl.setLatDeg(46.52);
        ViewingParametersBean view = new ViewingParametersBean(HorizontalCoordinates.ofDeg(180 + 1.e-7, 22),
                68.4);

        SkyCanvasManager canvasManager = new SkyCanvasManager(initCatalog(), observationTime, epfl, view);
        MenuManager menuManager = new MenuManager(view, canvasManager);

        GridPane root = new GridPane();
        root.getChildren().addAll(new BorderPane(canvasManager.canvas()), menuManager.menuPane());
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
        canvasManager.canvas().setFocusTraversable(true);
        System.out.println(menuManager.menuPane().focusTraversableProperty());
        canvasManager.canvas().requestFocus();
        //root.requestFocus();
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