package ch.epfl.rigel.gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

public class Main extends Application {

    private final StarCatalogue CATALOG = initCatalog();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        
        primaryStage.setTitle("Rigel");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        DateTimeBean observationTime = new DateTimeBean(ZonedDateTime.parse("2020-02-17T20:15:00+01:00"));
        ObserverLocationBean epfl = new ObserverLocationBean();
        epfl.setLonDeg(6.57);
        epfl.setLatDeg(46.52);
        ViewingParametersBean view = new ViewingParametersBean(HorizontalCoordinates.ofDeg(180 + 1.e-7, 22),
                68.4);

        SkyCanvasManager manager = new SkyCanvasManager(CATALOG, observationTime, epfl, view);
        BorderPane root = new BorderPane();
        root.getChildren().addAll(controlBar(observerPosition(), observationInstant(), timePassing()));
        
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        
    }

    private HBox controlBar(HBox observerPosition, HBox observationInstant, HBox timePassing) {
        HBox controlBar = new HBox();
        Separator vertSeparator1 = new Separator();
        Separator vertSeparator2 = new Separator();
        controlBar.getChildren().addAll(observerPosition,
                                        vertSeparator1,
                                        observationInstant,
                                        vertSeparator2,
                                        timePassing);
        controlBar.setStyle("-fx-spacing: 4; "
                          + "-fx-padding: 4;");
        return controlBar;
    }
    
    // observer Position ==================================================================================
    private HBox observerPosition() {
        HBox observerPosition = new HBox();
        observerPosition.setStyle("-fx-spacing: inherit;"
                                + "-fx-alignment: baseline-left;");
        Label lon = new Label("Longitude (°) :");
        Label lat = new Label("Latitude (°) :");
        
        TextField lonField = new TextField();
        TextField latField = new TextField();
        
        String styleForField = "-fx-pref-width: 60;"
                             + "-fx-alignment: baseline-right;";
        
        lonField.setStyle(styleForField);
        latField.setStyle(styleForField);
        
        lonField.setTextFormatter(positionFormatter("Longitude"));
        latField.setTextFormatter(positionFormatter("Latitude"));
        
        observerPosition.getChildren().addAll(lon, lonField, lat, latField);
        return observerPosition;
    }
    
    // to format the TextFields of longitude and latitude
    // must be used only with the Strings : Longitude or Latitude
    private TextFormatter<Number> positionFormatter(String typeCoordinate){
        NumberStringConverter stringConverter = new NumberStringConverter("#0.00");
        
        UnaryOperator<TextFormatter.Change> filter = (change -> {
            try {
                String newText = change.getControlNewText();
                double newCoordinateInDeg = stringConverter.fromString(newText).doubleValue();
                if(typeCoordinate.equals("Longitude"))
                    return GeographicCoordinates.isValidLonDeg(newCoordinateInDeg)
                            ? change
                            : null;
                if(typeCoordinate.equals("Latitude"))
                    return GeographicCoordinates.isValidLatDeg(newCoordinateInDeg)
                            ? change
                            : null;
                return null;
            } catch (Exception e) {
                return null;
              }
        });
        // TODO bind TextFormatter to observerLocationBean
        return new TextFormatter<Number>(stringConverter, 0, filter);
    }
    
    // observation instant ==============================================================
    private HBox observationInstant() {
        HBox observationInstant = new HBox();
        observationInstant.setStyle("-fx-spacing: inherit;\n" //TODO remove or should use \n 
                                  + "-fx-alignment: baseline-left;");
        Label date = new Label("Date :");
        DatePicker datePicker = new DatePicker();
        datePicker.setStyle("-fx-pref-width: 120;");
        
        Label hour = new Label("Heure :");
        TextField hourField = new TextField();
        hourField.setStyle("-fx-pref-width: 75;\n"
                         + "-fx-alignment: baseline-right;");
        hourField.setTextFormatter(dateTimeFormatter());
        
        List<String> notObservableListZoneId = new ArrayList<>(ZoneId.getAvailableZoneIds());
        ComboBox<String> zoneIdList = new ComboBox<>(); //TODO bind
        zoneIdList.setItems(FXCollections.observableList(notObservableListZoneId).sorted());
        zoneIdList.setStyle("-fx-pref-width: 180;");
        //zoneIdList.disabledProperty() //TODO bind to running of timeAnimator
        
        observationInstant.getChildren().addAll(date, datePicker, hour, hourField, zoneIdList);
        return observationInstant;
    }
    
    private TextFormatter<LocalTime> dateTimeFormatter(){
        DateTimeFormatter hmsFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
      LocalTimeStringConverter stringConverter = new LocalTimeStringConverter(hmsFormatter, hmsFormatter);
        return new TextFormatter<LocalTime>(stringConverter); //TODO  bind with bean
    }
    
    // timePassing=======================================================================
    private HBox timePassing() {
        HBox timePassing = new HBox();
        timePassing.setStyle("-fx-spacing: inherit;");
        
        ChoiceBox<NamedTimeAccelerator> accelerators = new ChoiceBox<>(); //TODO bind
        accelerators.setItems(FXCollections.observableArrayList(NamedTimeAccelerator.values()));
        
        Button resetButton = new Button("\uf0e2");
        // TO CONTINUE (import FONT AWESOME)
        
        return timePassing;
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
