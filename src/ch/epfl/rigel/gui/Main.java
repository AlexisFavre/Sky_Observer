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
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

public class Main extends Application {

    private final StarCatalogue CATALOG = initCatalog();
    private final Font fontAwesome = loadFontAwesome();
    private SkyCanvasManager manager;
    private TimeAnimator animator;

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
        ViewingParametersBean view = new ViewingParametersBean(HorizontalCoordinates.ofDeg(180 + 1.e-7, 22), 68.4);
        animator = new TimeAnimator(observationTime);

        manager = new SkyCanvasManager(CATALOG, observationTime, epfl, view);
        BorderPane skyPane = new BorderPane(manager.canvas());
        manager.canvas().widthProperty().bind(skyPane.widthProperty());
        manager.canvas().heightProperty().bind(skyPane.heightProperty());
        
        BorderPane root = new BorderPane();
        root.setCenter(skyPane);
        root.setBottom(informationPane());
        root.setTop(controlBar(observerPosition(), observationInstant(), timePassing()));
        
        System.out.println("test where exception appaer");
        
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        manager.canvas().requestFocus();
    }

    private HBox controlBar(HBox observerPosition, HBox observationInstant, HBox timePassing) {
        HBox controlBar = new HBox();
        Separator vertSeparator1 = new Separator(Orientation.VERTICAL);
        Separator vertSeparator2 = new Separator(Orientation.VERTICAL);
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
        boolean formatterForLon = typeCoordinate.equalsIgnoreCase("Longitude");
        boolean formatterForLat = typeCoordinate.equalsIgnoreCase("Latitude");
        if(!formatterForLat && !formatterForLon)
            throw new UnsupportedOperationException("Invalid Coordinate Type : " + typeCoordinate);
        
        NumberStringConverter stringConverter = new NumberStringConverter("#0.00");
        
        UnaryOperator<TextFormatter.Change> filter = (change -> {
            try {
                String newText = change.getControlNewText();
                double newCoordinateInDeg = stringConverter.fromString(newText).doubleValue();
                if(formatterForLon)
                    return GeographicCoordinates.isValidLonDeg(newCoordinateInDeg)
                            ? change
                            : null;
                if(formatterForLat)
                    return GeographicCoordinates.isValidLatDeg(newCoordinateInDeg)
                            ? change
                            : null;
                return null;
            } catch (Exception e) {
                return null;
              }
        });
        TextFormatter<Number> coordinateDisplay =  new TextFormatter<>(stringConverter, 0, filter);
        if(formatterForLon)
            manager.observerLocationBean().lonDegProperty().bindBidirectional(coordinateDisplay.valueProperty());
        if(formatterForLat)
            manager.observerLocationBean().latDegProperty().bindBidirectional(coordinateDisplay.valueProperty());
        return coordinateDisplay;
    }
    
    // observation instant ==============================================================
    private HBox observationInstant() {
        HBox observationInstant = new HBox();
        observationInstant.setStyle("-fx-spacing: inherit;\n" //TODO remove or should use \n 
                                  + "-fx-alignment: baseline-left;");
        Label date = new Label("Date :");
        DatePicker datePicker = new DatePicker();
        datePicker.valueProperty().bindBidirectional(manager.dateTimeBean().dateProperty());
        datePicker.setStyle("-fx-pref-width: 120;");
        datePicker.disableProperty().bind(animator.runningProperty());
        
        Label hour = new Label("Heure :");
        TextField hourField = new TextField();
        hourField.setStyle("-fx-pref-width: 75;\n"
                         + "-fx-alignment: baseline-right;");
        hourField.setTextFormatter(dateTimeFormatter());
        //hourField.disableProperty().bind(animator.runningProperty());
        
        List<String> notObservableListZoneId = new ArrayList<>(ZoneId.getAvailableZoneIds());
        ComboBox<String> zoneIdList = new ComboBox<>();
        //manager.dateTimeBean().zoneProperty().bindBidirectional(zoneIdList.valueProperty()); //TODO how bind
        zoneIdList.setItems(FXCollections.observableList(notObservableListZoneId).sorted());
        zoneIdList.setStyle("-fx-pref-width: 180;");
        zoneIdList.getSelectionModel().select("UTC");
        //TODO zoneIdList.disableProperty().bind(animator.runningProperty()); 
        
        observationInstant.getChildren().addAll(date, datePicker, hour, hourField, zoneIdList);
        return observationInstant;
    }
    
    private TextFormatter<LocalTime> dateTimeFormatter(){
        DateTimeFormatter hmsFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
      LocalTimeStringConverter stringConverter = new LocalTimeStringConverter(hmsFormatter, hmsFormatter);
        TextFormatter<LocalTime> timeDisplay =  new TextFormatter<>(stringConverter);
        timeDisplay.valueProperty().bindBidirectional(manager.dateTimeBean().timeProperty());
        return timeDisplay;
    }
    
    // timePassing=======================================================================
    private HBox timePassing() {
        HBox timePassing = new HBox();
        timePassing.setStyle("-fx-spacing: inherit;");
        
        ChoiceBox<NamedTimeAccelerator> accelerators = new ChoiceBox<>();// devrait mettre name ?
        accelerators.setItems(FXCollections.observableArrayList(NamedTimeAccelerator.values()));
        //animator.acceleratorProperty().bind(Bindings.select(accelerators));
        accelerators.getSelectionModel().selectFirst(); // put accelerator x1 by default
        
        Button resetButton = new Button("\uf0e2");
        resetButton.setFont(fontAwesome);
        
        Button playButton = new Button("\uf04b");
        playButton.setFont(fontAwesome);
        playButton.setOnAction(event ->{
            if(animator.runningProperty().get() == false) {
                playButton.setText("\uf04c");
                animator.start();
            }
            else {                                  //TODO good way ?
                playButton.setText("\uf04b");
                animator.stop();
            }
        });
        timePassing.getChildren().addAll(accelerators, resetButton, playButton);
        return timePassing;
    }
    
    // Information Pane===================================================================
    private BorderPane informationPane() {
        BorderPane infoPane = new BorderPane();
        infoPane.setStyle("-fx-padding: 4;\n" + 
                "-fx-background-color: white;");
        Text fielOfViewText = new Text();
        fielOfViewText.textProperty().bind(Bindings.format("Champ de vue : %.1f°", manager.scaleOfView())); 

        Text closestObjectText = new Text();
        closestObjectText.textProperty().bind(Bindings.format("%s", manager.objectUnderMouse));
        
        Text observerLookText = new Text();
        observerLookText.textProperty().bind(Bindings.format("Azimut : %.1f°, hauteur : %.1f°",
                manager.mouseAzDeg, manager.mouseAltDeg));
        
        infoPane.setLeft(fielOfViewText);
        infoPane.setCenter(closestObjectText);
        infoPane.setRight(observerLookText);
        return infoPane;
    }

    // additional methods=================================================================
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
    
    private Font loadFontAwesome() {
        try(InputStream fontStream = getClass().getResourceAsStream("/Font Awesome 5 Free-Solid-900.otf");){
            return Font.loadFont(fontStream, 15);
        } catch (IOException e) {
            return null;
        }
        
    }
}
