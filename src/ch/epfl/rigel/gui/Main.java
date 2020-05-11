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
import java.util.stream.Collectors;

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

    private final static String PATTERN_LONG_AND_LAT = "#0.00";
    private final static String LONG = "Longitude";
    private final static String LAT = "Latitude";
    
    // constants for initialization
    private final static int INDEX_ACCELERATOR_X300 = 2;
    private final static double EPFL_LON_DEG = 6.57;
    private final static double EPFL_LAT_DEG = 46.52;
    private final static double INITIAL_FIEL_OF_VIEW_DEG = 68.4;
    private final static HorizontalCoordinates INITIAl_CENTER_OF_PROJECTION = HorizontalCoordinates.ofDeg(180 + 1.e-7, 22);
    
    private final Font fontAwesome = loadFontAwesome();
    private final StarCatalogue catalog = initCatalog();
    
    private SkyCanvasManager manager;
    private TimeAnimator animator;
    private ZonedDateTime currentInstant;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        
        // Initiate stage (window)
        primaryStage.setTitle("Rigel");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        currentInstant = ZonedDateTime.now();
        
        // Initiate sky
        DateTimeBean observationTime = new DateTimeBean(currentInstant);
        ObserverLocationBean epfl = new ObserverLocationBean();
        epfl.setLonDeg(EPFL_LON_DEG);
        epfl.setLatDeg(EPFL_LAT_DEG);
        ViewingParametersBean view = new ViewingParametersBean(INITIAl_CENTER_OF_PROJECTION, INITIAL_FIEL_OF_VIEW_DEG);
        animator = new TimeAnimator(observationTime);
        manager = new SkyCanvasManager(catalog, observationTime, epfl, view);
        
        
        // Pane containing the canvas where the sky is drawn
        BorderPane skyPane = new BorderPane(manager.canvas());
        manager.canvas().widthProperty().bind(skyPane.widthProperty());
        manager.canvas().heightProperty().bind(skyPane.heightProperty());
        
        // Initiate user interface
        BorderPane root = new BorderPane();
        root.setCenter(skyPane);
        root.setBottom(informationPane());
        root.setTop(controlBar(observerPosition(), observationInstant(), timePassing()));
        
        
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        manager.canvas().requestFocus();
    }
    // top module, contain observer position, observation instant and time passing modules
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
        
        
        Label lon = new Label(LONG + " (°) :");
        Label lat = new Label(LAT  + " (°) :");
        
        TextField lonField = new TextField();
        TextField latField = new TextField();
        
        String styleForField = "-fx-pref-width: 60;"
                             + "-fx-alignment: baseline-right;";
        
        lonField.setStyle(styleForField);
        latField.setStyle(styleForField);
        
        lonField.setTextFormatter(positionFormatter(LONG));
        latField.setTextFormatter(positionFormatter(LAT));
        
        observerPosition.getChildren().addAll(lon, lonField, lat, latField);
        return observerPosition;
    }
    
    // to format the TextFields of longitude and latitude
    // must be used only with the Strings : Longitude or Latitude
    private TextFormatter<Number> positionFormatter(String typeCoordinate){
        
        boolean formatterForLon = typeCoordinate.equalsIgnoreCase(LONG);
        boolean formatterForLat = typeCoordinate.equalsIgnoreCase(LAT);
        if(!formatterForLat && !formatterForLon)
            throw new UnsupportedOperationException("Invalid Coordinate Type : " + typeCoordinate);
        
        NumberStringConverter stringConverter = new NumberStringConverter(PATTERN_LONG_AND_LAT);
        
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
                
            } catch (Exception e) { //if cannot convert the input string into a double
                return null;
              }
        });
        TextFormatter<Number> coordinateDisplay =  new TextFormatter<>(stringConverter, 0, filter);
        
        if(formatterForLon)
            manager.observerLocationBean().lonDegProperty().
                bindBidirectional(coordinateDisplay.valueProperty());
        
        if(formatterForLat)
            manager.observerLocationBean().latDegProperty().
                bindBidirectional(coordinateDisplay.valueProperty());
        
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
        
        List<ZoneId> notObservableListZoneId =  
                ZoneId.getAvailableZoneIds().
                stream().
                sorted().
                map(ZoneId::of).
                collect(Collectors.toList());
        ComboBox<ZoneId> zoneIdList = new ComboBox<>();
        zoneIdList.valueProperty().bindBidirectional(manager.dateTimeBean().zoneProperty()); //TODO how bind
        zoneIdList.setItems(FXCollections.observableList(notObservableListZoneId));
        zoneIdList.setStyle("-fx-pref-width: 180;");
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
        accelerators.getSelectionModel().select(INDEX_ACCELERATOR_X300);
        
        // to reset to the current instant
        Button resetButton = new Button("\uf0e2"); //unicode for the image
        resetButton.setFont(fontAwesome);
        resetButton.setOnAction(event -> {
            currentInstant = ZonedDateTime.now(manager.dateTimeBean().getZone());
            manager.dateTimeBean().setZonedDateTime(currentInstant);
        });
        
        // to active or stop the time evolution
        Button playButton = new Button("\uf04b"); //unicode for the image
        playButton.setFont(fontAwesome);
        playButton.setOnAction(event -> {
            
            if(animator.runningProperty().get() == false) {
                playButton.setText("\uf04c"); //unicode for the image
                animator.start();
            
            } else {                                  //TODO good way ?
                playButton.setText("\uf04b"); //unicode for the image
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
        fielOfViewText.textProperty().bind(
                Bindings.format("Champ de vue : %.1f°", 
                        manager.viewingParameterBean().fieldOfViewDegProperty())); 

        Text closestObjectText = new Text();
        closestObjectText.textProperty().bind(
                Bindings.format("%s",
                        manager.objectUnderMouse())); //TODO when null should print nothing
        
        Text observerLookText = new Text();
        observerLookText.textProperty().bind(
                Bindings.format("Azimut : %.1f°, hauteur : %.1f°",
                manager.mouseAzDeg(), manager.mouseAltDeg()));
        
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
