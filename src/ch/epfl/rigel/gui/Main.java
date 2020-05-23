package ch.epfl.rigel.gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
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
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

/**
 * Main Class of the application
 * manage the user interface
 * @author Alexis FAVRE (310552)
 */
public class Main extends Application {

    private final static String PATTERN_LONG_AND_LAT = "#0.00";
    private final static String PATTERN_TIME = "HH:mm:ss";
    private final static String UNICODE_FOR_RESET_BUT = "\uf0e2";
    private final static String UNICODE_FOR_PLAY_BUT = "\uf04b";
    private final static String UNICODE_FOR_PAUSE_BUT = "\uf04c";
    
    // constants for initialization
    private final static int MINIMAL_WIDTH_STAGE = 800;
    private final static int MINIMAL_HEIGHT_STAGE = 600;
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
        primaryStage.setMinWidth(MINIMAL_WIDTH_STAGE);
        primaryStage.setMinHeight(MINIMAL_HEIGHT_STAGE);
        
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
        Pane skyPane = new Pane(manager.canvas());
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
        
        
        Label lon = new Label("Longitude (°) :");
        Label lat = new Label("Latitude  (°) :");
        
        TextField lonField = new TextField();
        TextField latField = new TextField();
        
        String styleForField = "-fx-pref-width: 60;"
                             + "-fx-alignment: baseline-right;";
        
        lonField.setStyle(styleForField);
        latField.setStyle(styleForField);
        
        lonField.setTextFormatter(positionFormatter(
                GeographicCoordinates :: isValidLonDeg, manager.observerLocationBean().lonDegProperty()));
        latField.setTextFormatter(positionFormatter(
                GeographicCoordinates :: isValidLatDeg, manager.observerLocationBean().latDegProperty()));
        
        observerPosition.getChildren().addAll(lon, lonField, lat, latField);
        return observerPosition;
    }
    
    // to make TextFormatter for lonField and LatField
    private TextFormatter<Number> positionFormatter(Predicate<Double> isValidCoordinate, DoubleProperty coordinateProperty){
        NumberStringConverter stringConverter = new NumberStringConverter(PATTERN_LONG_AND_LAT);
        
        UnaryOperator<TextFormatter.Change> filter = (change -> {
            try {
                String newText = change.getControlNewText();
                double newCoordinateInDeg = stringConverter.fromString(newText).doubleValue();
                return isValidCoordinate.test(newCoordinateInDeg) ? change : null;
                
            } catch (Exception e) { //ParseException if cannot convert the input string into a double
                return null; // or NullPointerException if user try to entirely clear the textField
              }
        });
        TextFormatter<Number> coordinateDisplay =  new TextFormatter<>(stringConverter, 0, filter);
        coordinateDisplay.valueProperty().bindBidirectional(coordinateProperty);
        
        return coordinateDisplay;
    }
    
    // observation instant ==============================================================
    private HBox observationInstant() {
        
        HBox observationInstant = new HBox();
        observationInstant.setStyle("-fx-spacing: inherit;\n" //TODO remove or should use \n ??
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
        hourField.disableProperty().bind(animator.runningProperty());
        
        List<ZoneId> notObservableListZoneId =  
                ZoneId.getAvailableZoneIds().
                stream().
                sorted().
                map(ZoneId :: of).
                collect(Collectors.toList());
        ComboBox<ZoneId> zoneIdList = new ComboBox<>();
        zoneIdList.valueProperty().bindBidirectional(manager.dateTimeBean().zoneProperty());
        zoneIdList.setItems(FXCollections.observableList(notObservableListZoneId));
        zoneIdList.setStyle("-fx-pref-width: 180;");
        zoneIdList.disableProperty().bind(animator.runningProperty()); 
        
        observationInstant.getChildren().addAll(date, datePicker, hour, hourField, zoneIdList);
        return observationInstant;
    }
    
    private TextFormatter<LocalTime> dateTimeFormatter(){
        
        DateTimeFormatter hmsFormatter = DateTimeFormatter.ofPattern(PATTERN_TIME);
        LocalTimeStringConverter stringConverter = new LocalTimeStringConverter(hmsFormatter, hmsFormatter);
        TextFormatter<LocalTime> timeDisplay =  new TextFormatter<>(stringConverter);
        timeDisplay.valueProperty().bindBidirectional(manager.dateTimeBean().timeProperty());
        
        return timeDisplay;
    }
    
    // timePassing=======================================================================
    private HBox timePassing() {
        
        HBox timePassing = new HBox();
        timePassing.setStyle("-fx-spacing: inherit;");
        
        ChoiceBox<NamedTimeAccelerator> accelerators = new ChoiceBox<>();
        accelerators.setItems(FXCollections.observableArrayList(NamedTimeAccelerator.values()));
        accelerators.getSelectionModel().select(INDEX_ACCELERATOR_X300);
        animator.acceleratorProperty().bind(Bindings.select(accelerators.valueProperty(), "accelerator"));
        
        // to reset to the current instant
        Button resetButton = new Button(UNICODE_FOR_RESET_BUT);
        resetButton.setFont(fontAwesome);
        resetButton.setOnAction(event -> {
            currentInstant = ZonedDateTime.now(manager.dateTimeBean().getZone());
            if(animator.runningProperty().get() == true) {
                animator.stop();  //can't modify dateTimeBean when animation is running
                manager.dateTimeBean().setZonedDateTime(currentInstant);
                animator.start();
          } else {
                manager.dateTimeBean().setZonedDateTime(currentInstant);
            }

        });
        
        // to active or stop the time evolution
        Button playButton = new Button(UNICODE_FOR_PLAY_BUT);
        playButton.setFont(fontAwesome);
        playButton.setOnAction(event -> {
            
            if(animator.runningProperty().get() == false) {
                playButton.setText(UNICODE_FOR_PAUSE_BUT);
                animator.start();
            
            } else {                                  //TODO good way ?,
                //should use Bindings.when(boolean).then.....?
                playButton.setText(UNICODE_FOR_PLAY_BUT);
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
        closestObjectText.textProperty().bind(Bindings.createStringBinding(
                () -> {
                        if (manager.objectUnderMouse().get().isPresent())  
                            return manager.objectUnderMouse().get().get().info();
                        return "";
                     }, 
                manager.objectUnderMouse()));
                    
        
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
            throw new UncheckedIOException(e);
        }
        
    }
}
