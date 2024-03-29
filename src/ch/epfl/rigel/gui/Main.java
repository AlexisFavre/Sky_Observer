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
import ch.epfl.rigel.city.City;
import ch.epfl.rigel.city.CityCatalogue;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

    private final static String PATTERN_LONG_AND_LAT   = "#0.00";
    private final static String PATTERN_TIME           = "HH:mm:ss";
    private final static String UNICODE_FOR_RESET_BUT  = "\uf0e2";
    private final static String UNICODE_FOR_PLAY_BUT   = "\uf04b";
    private final static String UNICODE_FOR_PAUSE_BUT  = "\uf04c";
    private final static String NAME_FILE_OF_ASTERISMS = "/asterisms.txt";
    private final static String NAME_FILE_OF_STARS     = "/hygdata_v3.csv";
    
    // constants for initialization
    private final static int MINIMAL_WIDTH_STAGE    = 1350;
    private final static int MINIMAL_HEIGHT_STAGE   = 800;
    private final static int INDEX_ACCELERATOR_X300 = 2;
    private final static int FONT_SIZE   = 15;
    private final static int MAX_OPACITY = 1;
    private final static int MIN_OPACITY = 0;
    
    private final static double INITIAL_FIELD_OF_VIEW_DEG = 68.4;
    private final static HorizontalCoordinates INITIAl_CENTER_OF_PROJECTION = HorizontalCoordinates.ofDeg(180 + 1.e-12, 22);
    
    private final static Font FONT_AWESOME     = loadFontAwesome();
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
        ObserverLocationBean epfl    = new ObserverLocationBean(CityCatalogue.epfl().coordinates());
        ViewingParametersBean view   = new ViewingParametersBean(INITIAl_CENTER_OF_PROJECTION, INITIAL_FIELD_OF_VIEW_DEG);
        animator = new TimeAnimator(observationTime);
        manager  = new SkyCanvasManager(CATALOG, observationTime, epfl, view);
        
        // Initiate user interface
        BorderPane mainRoot = new BorderPane();
        mainRoot.setCenter(manager.skyPane());
        mainRoot.setBottom(informationPane());
        mainRoot.setTop(controlPane(observerPosition(), observationInstant(), timePassing(), starSearch(), cityBox()));
        
        primaryStage.setScene(welcomeSceneTo(new Scene(mainRoot), primaryStage));
        primaryStage.show();
    }
    
    /* *************************************************************************
     *                                                                         *
     *                    Internal implementation stuff                        *
     *                                                                         *
     **************************************************************************/
    
    
    //====================================================================================================
    //==================================== Welcome Scene =================================================
    //====================================================================================================
    
    //scene that the user see when he loads the application
    private Scene welcomeSceneTo(Scene mainScene, Stage stage) {
        StackPane welcomeRoot  = new StackPane();
        Scene scene            = new Scene(welcomeRoot);
        
        ImageView background   = new ImageView(loadWelcomeImage());
        background.fitWidthProperty().bind(welcomeRoot.widthProperty());
        background.fitHeightProperty().bind(welcomeRoot.heightProperty());
        
        BorderPane presentationPane = new BorderPane();
        
        //box used to select celestial objects to draw
        HBox selectionBox = new HBox(40);
        selectionBox.setAlignment(Pos.CENTER);
        selectionBox.setSpacing(50);
        
        RadioButton starSelector      = butToDrawCelestailObjects("étoiles",   manager.drawWithStars(), true);
        RadioButton planetSelector    = butToDrawCelestailObjects("planètes",  manager.drawWithPlanets(), false);
        RadioButton asterismsSelector = butToDrawCelestailObjects("asterimes", manager.drawWithAsterisms(), false);
        starSelector.selectedProperty().addListener(e -> {
            if(!planetSelector.selectedProperty().get()) {
                starSelector.selectedProperty().setValue(true);
            }
            if(!starSelector.selectedProperty().get()) {
                asterismsSelector.selectedProperty().setValue(false);
            }
        });
        planetSelector.selectedProperty().addListener(e -> {
            if(!starSelector.selectedProperty().get()) {
                planetSelector.selectedProperty().setValue(true);
            }
        });
        asterismsSelector.selectedProperty().addListener(e -> {
            if(!starSelector.selectedProperty().get()) {
                asterismsSelector.selectedProperty().setValue(false);
            }
        });

        selectionBox.getChildren().addAll(starSelector, planetSelector, asterismsSelector,
                butToDrawCelestailObjects("soleil",  manager.drawWithSun(), false),
                butToDrawCelestailObjects("lune",    manager.drawWithMoon(), false),
                butToDrawCelestailObjects("horizon & cardinaux", manager.drawWithHorizon(), true));
        
        Text drawingTxt   = new Text("Choisir les éléments à ajouter à l'observation");
        drawingTxt.setFill(Color.GHOSTWHITE);
        drawingTxt.setFont(Font.font(20));
        drawingTxt.setTextAlignment(TextAlignment.CENTER);
        
        
        //box used to present welcome text
        VBox welcomeBox  = new VBox(40);
        welcomeBox.setAlignment(Pos.CENTER);
            
        //presentation texts
        Text welcomeText = new Text("Bienvenue");
        welcomeText.setFill(Color.GHOSTWHITE);
        welcomeText.setFont(Font.font(90));
        
        Text readyText   = new Text("Prêt à découvrir les étoiles, planètes et asterismes ?");
        readyText.setWrappingWidth(700);
        readyText.setTextAlignment(TextAlignment.CENTER);
        readyText.setFill(Color.GHOSTWHITE);
        readyText.setFont(Font.font(40));
        
        
        // transitions between the welcome scene to the main scene
        FadeTransition quitWelcomeScene = new FadeTransition(Duration.millis(800));
        quitWelcomeScene.setNode(welcomeRoot);
        quitWelcomeScene.setFromValue(MAX_OPACITY);
        quitWelcomeScene.setToValue(MIN_OPACITY);
        
        FadeTransition joinMainScene = new FadeTransition(Duration.millis(1400));
        joinMainScene.setNode(mainScene.getRoot());
        joinMainScene.setFromValue(MIN_OPACITY);
        joinMainScene.setToValue(MAX_OPACITY);
        
        quitWelcomeScene.setOnFinished(e -> {
            joinMainScene.play();
            stage.setScene(mainScene);
            manager.canvas().requestFocus();
        });
        
        
        //button to switch to main scene
        Button switchBut = new Button("Commencer l'observation");
        switchBut.minWidth(150);
        switchBut.setOnAction(e -> quitWelcomeScene.play());
        
        
        welcomeBox.getChildren().addAll(welcomeText, readyText, drawingTxt, selectionBox, switchBut);
        welcomeBox.setAlignment(Pos.CENTER);
        
        presentationPane.setCenter(welcomeBox);
        
        welcomeRoot.getChildren().addAll(background, presentationPane);
        
        return scene;
    }
    
    // used to make selection buttons with enable to select what we want to draw in the sky
    private RadioButton butToDrawCelestailObjects(String name, BooleanProperty propertyToBind, boolean preSelect) {
        
        RadioButton but = new RadioButton(name);
        but.setAlignment(Pos.TOP_LEFT);
        but.setSelected(preSelect);
        but.setTextFill(Color.GHOSTWHITE);
        but.setFont(Font.font(15));
        
        propertyToBind.bind(but.selectedProperty());
        return but;
    }
    //====================================================================================================
    //====================================== Control Pane =================================================
    //====================================================================================================
    
    // top sub-pane of main scene, contain observer position, observation instant and time passing modules
    private HBox controlPane(HBox observerPosition, HBox observationInstant, HBox timePassing, HBox searchBar, HBox cities) {
        
        HBox controlBar = new HBox();

        controlBar.getChildren().addAll(observerPosition,
                                        new Separator(Orientation.VERTICAL),
                                        observationInstant,
                                        new Separator(Orientation.VERTICAL),
                                        timePassing,
                                        new Separator(Orientation.VERTICAL),
                                        searchBar,
                                        new Separator(Orientation.VERTICAL),
                                        cities);
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
        searchBar.setPromptText("rechercher un astre");
        searchBar.setOnAction(event -> {
            String destination = searchBar.getText();
            searchBar.deleteText(0, searchBar.getLength());
            manager.goToDestinationWithName(destination);
        });

        starSearch.getChildren().addAll(searchBar);
        return starSearch;
    }
    
    //====================================================================================================
    //================================ Observer Position Box =============================================
    //====================================================================================================
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
    
    //====================================================================================================
    //================================ Observation Instant Box ===========================================
    //====================================================================================================
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
    
    //====================================================================================================
    //==================================== Time Passing Box ==============================================
    //====================================================================================================
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
    
    //===================================================================================
    //=================================== Cities ========================================
    //===================================================================================
    private HBox cityBox() {
        
        HBox cityBox = new HBox();
        cityBox.setStyle("-fx-spacing: inherit;"
                       + "-fx-alignment: baseline-left;");
        
        Label cityLabel = new Label("Ville : ");
        ComboBox<City> citiesList = new ComboBox<>();
        citiesList.setItems(FXCollections.observableList(CityCatalogue.availableCities()));
        citiesList.setValue(CityCatalogue.epfl());
        citiesList.setPrefWidth(180);
        
        //if user has modified latitude or longitude of observation independently from the city,
        //when he will click on the comboBox, the observation coordinates
        //will be the coordinates of the current city
        citiesList.setOnMouseClicked( e -> manager.observerLocationBean().
                setCoordinates(citiesList.getValue().coordinates()));
        
        citiesList.valueProperty().addListener( (o, oV, nV) -> manager.observerLocationBean().
                setCoordinates(nV.coordinates()));
        
        //if coordinates of Observer Position don't correspond to those of the current city in the citiesList
        // then the color of the ComboBox will become tomato
        manager.observerLocationBean().lonDegProperty().
            addListener( (o, oV, nV) -> {
                if(! (Math.abs(nV.doubleValue() - citiesList.getValue().coordinates().lonDeg()) < 1e-2))
                    citiesList.setStyle("-fx-background-color : tomato");
                else
                    citiesList.setStyle("");  //take back default background color
            });
        
        manager.observerLocationBean().latDegProperty().
            addListener( (o, oV, nV) -> {
                if(! (Math.abs(nV.doubleValue() - citiesList.getValue().coordinates().latDeg()) < 1e-2))
                    citiesList.setStyle("-fx-background-color : tomato");
                else
                    citiesList.setStyle(""); //take back default background color
            });
        
        cityBox.getChildren().addAll(cityLabel, citiesList);
        return cityBox;
    }
    //===================================================================================
    //=========================== Information Pane ======================================
    //===================================================================================
    
    //bottom sub-pane of the main scene, display Field of View, Closest Object to the Mouse,
    // and horizontal coordinates of the mouse
    private BorderPane informationPane() {
        
        BorderPane infoPane = new BorderPane();
        infoPane.setStyle("-fx-padding: 4;" + 
                "-fx-background-color: white;");
        
        Text fieldOfViewText = new Text();
        fieldOfViewText.textProperty().bind(
                Bindings.format("Champ de vue : %.1f°", 
                        manager.viewingParameterBean().fieldOfViewDegProperty())); 

        Text errorLog = new Text();
        errorLog.textProperty().bind(manager.errorMessage());
        errorLog.setFill(Color.CRIMSON);
                    
        
        Text observerLookText = new Text();
        observerLookText.textProperty().bind(
                Bindings.format("Azimut : %.1f°, hauteur : %.1f°",
                manager.mouseAzDeg(), manager.mouseAltDeg()));
        
        infoPane.setLeft(fieldOfViewText);
        infoPane.setCenter(errorLog);
        infoPane.setRight(observerLookText);
        
        return infoPane;
    }

    //====================================================================================================
    //==================================== Additional Methods ============================================
    //====================================================================================================
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
    
    private static Image loadWelcomeImage() {
        try(InputStream imgStream = Main.class.getResourceAsStream("/Sky Image.jpg")) {
            return new Image(imgStream);
            
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
