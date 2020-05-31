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
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

/**
 * Main Class of the application
 * manage the user interface
 * @author Alexis FAVRE (310552)
 */
public final class Main extends Application {

    private final static String PATTERN_LONG_AND_LAT = "#0.00";
    private final static String PATTERN_TIME = "HH:mm:ss";
    private final static String UNICODE_FOR_RESET_BUT = "\uf0e2";
    private final static String UNICODE_FOR_PLAY_BUT = "\uf04b";
    private final static String UNICODE_FOR_PAUSE_BUT = "\uf04c";
    private final static String NAME_FILE_OF_ASTERISMS = "/asterisms.txt";
    private final static String NAME_FILE_OF_STARS = "/hygdata_v3.csv";
    
    // constants for initialization
    private final static int MINIMAL_WIDTH_STAGE = 800;
    private final static int MINIMAL_HEIGHT_STAGE = 600;
    private final static int INDEX_ACCELERATOR_X300 = 2;
    private final static int FONT_SIZE = 15;
    
    private final static double EPFL_LON_DEG = 6.57;
    private final static double EPFL_LAT_DEG = 46.52;
    private final static double INITIAL_FIEL_OF_VIEW_DEG = 68.4;
    private final static HorizontalCoordinates INITIAl_CENTER_OF_PROJECTION = HorizontalCoordinates.ofDeg(180 + 1.e-12, 22);
    
    private final static Font FONT_AWESOME = loadFontAwesome();
    private final static StarCatalogue CATALOG = initCatalog();
    
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
        manager = new SkyCanvasManager(CATALOG, observationTime, epfl, view);
        
        
        // Pane containing the canvas where the sky is drawn
        Pane skyPane = new Pane(manager.canvas());
        manager.canvas().widthProperty().bind(skyPane.widthProperty());
        manager.canvas().heightProperty().bind(skyPane.heightProperty());
        
        // Initiate user interface
        BorderPane root = new BorderPane();
        root.setCenter(skyPane);
        root.setBottom(informationPane());
        root.setTop(controlBar(observerPosition(), observationInstant(), timePassing(), starSearch()));
        
        primaryStage.setScene(welcomeScene(primaryStage, new Scene(root)));
        primaryStage.show();
    }
    
    /* *************************************************************************
     *                                                                         *
     *                    Internal implementation stuff                        *
     *                                                                         *
     **************************************************************************/
    
    //welcome scene that the user see when he loads the application
    private Scene welcomeScene(Stage stage, Scene nextscene) {
        StackPane root = new StackPane();
        Scene scene    = new Scene(root);
        ImageView imgV = new ImageView(welcomeImage()); //TODO find a way to bind size
        
        VBox selectionBox = new VBox(40);
        selectionBox.setAlignment(Pos.CENTER_RIGHT);
        Text drawingTxt = new Text("Voulez vous observez le ciel");
        drawingTxt.setFill(Color.GHOSTWHITE);
//        drawingTxt.textProperty()
        
        selectionBox.getChildren().addAll(
                drawingTxt,
                butToDrawCelestailObjects("avec les étoiles  ",  manager.drawWithStars()),
                butToDrawCelestailObjects("avec les planètes", manager.drawWithPlanets()),
                butToDrawCelestailObjects("avec le Soleil    ",    manager.drawWithSun()),
                butToDrawCelestailObjects("avec la Lune      ",      manager.drawWithMoon()),
                butToDrawCelestailObjects("avec l'horizon    ",    manager.drawWithHorizon()));
        
        VBox mainBox = new VBox(40);
        mainBox.setAlignment(Pos.CENTER);                  //TODO when end control bar, must put the same size
            
        //presentation texts
        Text wlcTxt  = new Text("Bienvenue");
        wlcTxt.setFill(Color.GHOSTWHITE);
        wlcTxt.setFont(Font.font(90));
        
        Text readyTxt = new Text("Prêt à découvrir de nouveaux asterisms, étoiles et planètes ? :) ");
        readyTxt.setWrappingWidth(700);
        readyTxt.setTextAlignment(TextAlignment.CENTER);
        readyTxt.setFill(Color.GHOSTWHITE);
        readyTxt.setFont(Font.font(40));
        
        
        // transitions between the welcome sceen to the main scene
        FadeTransition quitWlcScene = new FadeTransition(Duration.millis(400));
        quitWlcScene.setNode(root);
        quitWlcScene.setFromValue(1);
        quitWlcScene.setToValue(0);
        
        FadeTransition joinMainScene = new FadeTransition(Duration.millis(1000));
        joinMainScene.setNode(nextscene.getRoot());
        joinMainScene.setFromValue(0);
        joinMainScene.setToValue(1);
        
        quitWlcScene.setOnFinished(e -> {
            stage.setScene(nextscene);
            joinMainScene.play();
            manager.canvas().requestFocus();
        });
        
        
        //button to switch to main scene
        Button but = new Button("Commencer l'observation");
        but.minWidth(150);
        but.setOnAction(e -> quitWlcScene.play());
        
        mainBox.getChildren().addAll(wlcTxt, readyTxt, but);
        mainBox.setAlignment(Pos.CENTER);
        
        root.getChildren().addAll(imgV, mainBox, selectionBox);
        
        return scene;
    }
    
    private RadioButton butToDrawCelestailObjects(String name, BooleanProperty propertyToBind) {// TODO find better names
        RadioButton but = new RadioButton(name);
        propertyToBind.bind(but.selectedProperty());
        but.setAlignment(Pos.TOP_LEFT);
        but.setSelected(true);
        but.setTextFill(Color.GHOSTWHITE);
        return but;
    }
    
    // top module, contain observer position, observation instant and time passing modules
    private HBox controlBar(HBox observerPosition, HBox observationInstant, HBox timePassing, HBox searchBar) {
        
        HBox controlBar = new HBox();
        Separator vertSeparator1 = new Separator(Orientation.VERTICAL);
        Separator vertSeparator2 = new Separator(Orientation.VERTICAL);
        Separator vertSeparator3 = new Separator(Orientation.VERTICAL);
        controlBar.getChildren().addAll(observerPosition,
                                        vertSeparator1,
                                        observationInstant,
                                        vertSeparator2,
                                        timePassing,
                                        vertSeparator3,
                                        searchBar);
        controlBar.setStyle("-fx-spacing: 4; "
                          + "-fx-padding: 4;");
        return controlBar;
    }

    private HBox starSearch() {
        HBox starSearch = new HBox();
        starSearch.setStyle("-fx-spacing: inherit;"
                + "-fx-alignment: baseline-left;");

        TextField searchBar = new TextField();
        searchBar.setMinWidth(92);
        searchBar.setPromptText("search a star");
        searchBar.setOnAction(event -> {
            String destination = searchBar.getText();
            searchBar.deleteText(0, searchBar.getLength());
            manager.goToDestinationWithName(destination);
        });

        starSearch.getChildren().addAll(searchBar);
        return starSearch;
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
                return isValidCoordinate
                        .test(newCoordinateInDeg) 
                        ? change 
                        : null;
                
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
        observationInstant.setStyle("-fx-spacing: inherit;"
                                  + "-fx-alignment: baseline-left;");
        
        Label date = new Label("Date :");
        DatePicker datePicker = new DatePicker();
        datePicker.valueProperty().bindBidirectional(manager.dateTimeBean().dateProperty());
        datePicker.setStyle("-fx-pref-width: 120;");
        datePicker.disableProperty().bind(animator.runningProperty());
        
        Label hour = new Label("Heure :");
        TextField hourField = new TextField();
        hourField.setStyle("-fx-pref-width: 75;"
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
        resetButton.setFont(FONT_AWESOME);
        resetButton.setOnAction(event -> {
            currentInstant = ZonedDateTime.now();
            if(animator.runningProperty().get()) {
                animator.stop();  //can't modify dateTimeBean when animation is running
                manager.dateTimeBean().setZonedDateTime(currentInstant);
                animator.start();
          } else {
                manager.dateTimeBean().setZonedDateTime(currentInstant);
            }

        });
        
        // to active or stop the time evolution
        Button playButton = new Button(UNICODE_FOR_PLAY_BUT);
        playButton.setFont(FONT_AWESOME);
        playButton.setOnAction(event -> {
            
            if( !animator.runningProperty().get()) {
                playButton.setText(UNICODE_FOR_PAUSE_BUT);
                animator.start();
            
            } else {                                 
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
        infoPane.setStyle("-fx-padding: 4;" + 
                "-fx-background-color: white;");
        
        Text fieldOfViewText = new Text();
        fieldOfViewText.textProperty().bind(
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
        
        infoPane.setLeft(fieldOfViewText);
        infoPane.setCenter(closestObjectText);
        infoPane.setRight(observerLookText);
        
        return infoPane;
    }

    // additional methods=================================================================
    private static StarCatalogue initCatalog() {
        
        try (InputStream hygStream = Main.class.getResourceAsStream(NAME_FILE_OF_STARS);
             InputStream aStream   = Main.class.getResourceAsStream(NAME_FILE_OF_ASTERISMS)) {
            return new StarCatalogue
                    .Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .loadFrom(aStream,   AsterismLoader.INSTANCE)
                    .build();
            
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    private static Font loadFontAwesome() {
        try(InputStream fontStream = Main.class.getResourceAsStream("/Font Awesome 5 Free-Solid-900.otf")){
            return Font.loadFont(fontStream, FONT_SIZE);
            
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    private static Image welcomeImage() {
        try(InputStream imgStream = Main.class.getResourceAsStream("/Sky Image.jpg")) {
            return new Image(imgStream);
            
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
