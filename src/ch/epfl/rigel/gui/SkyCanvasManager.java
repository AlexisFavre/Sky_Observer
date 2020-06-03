package ch.epfl.rigel.gui;

import static ch.epfl.rigel.math.RightOpenInterval.ROInter_0To360;

import java.util.Optional;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.Interval;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

/**
 * Use to create a canvas and control the sky painting on it
 * It defines a relation between canvas events and sky painting
 * (ex: scroll is zooming, arrows are orienting look)
 * And treats the events asked by the user to actualize sky painting
 *
 * @author Augustin ALLARD (299918)
 * @author Alexis FAVRE (310552)
 */
public final class SkyCanvasManager {
    
    private final static ClosedInterval RANGE_OBSERVABLE_ALTITUDES   = ClosedInterval.of(5, 90);
    private final static ClosedInterval RANGE_FIELD_OF_VIEW_DEG = ClosedInterval.of(30, 150);
    private final static RightOpenInterval CINTER_0TO360 = RightOpenInterval.of(0, 360);
    private final static int MAX_DISTANCE_FOR_CLOSEST_OBJECT_TO = 10;
    private final static int CHANGE_OF_AZIMUT_WHEN_KEY_PRESSED = 10;
    private final static int CHANGE_OF_ALTITUDE_WHEN_KEY_PRESSED = 5;
    private final static CartesianCoordinates INITIAL_POS_MOUSE = CartesianCoordinates.of(0, 0);

    private final static double INFO_BOX_WIDTH = 90;
    private final static double INFO_BOX_HEIGTH = 60;
    private final static double INFO_BOX_DOWN_SHIFT = 10;

    private final Canvas canvas;
    private final SkyCanvasPainter painter;
    private final Pane skyPane;
    private final ObserverLocationBean olb;
    private final DateTimeBean dtb;
    private final ViewingParametersBean vpb;
    
    private final BooleanProperty drawWithStars;
    private final BooleanProperty drawWithHorizon;
    private final BooleanProperty drawWithPlanets;
    private final BooleanProperty drawWithAsterisms;
    private final BooleanProperty drawWithSun;
    private final BooleanProperty drawWithMoon;
    
    private final DoubleBinding mouseAzDeg;
    private final DoubleBinding mouseAltDeg;
    private final ObjectProperty<CartesianCoordinates> mousePosition;
    private final ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition;
    private final ObjectBinding<Optional<CelestialObject>> objectUnderMouse;
    private final ObjectProperty<HorizontalCoordinates> selectedObjectPoint;
    private final ObjectBinding<Optional<CartesianCoordinates>> selectedScreenPoint;
    private final StringProperty errorMessage;
    private String selectedObjectName;
    private double[] selectedDistances;

    private final DoubleBinding scaleOfView;
    private final ObjectBinding<ObservedSky> sky;
    private final ObjectBinding<StereographicProjection> projection;
    private final ObjectBinding<Transform> planeToCanvas;

    private final ViewAnimator centerAnimator;

    
    /**
     *
     * @param catalog the catalog of stars that will be painted on the canvas
     * @param dtb the {@code DateTimeBean} corresponding to the observation time
     * @param olb the {@code ObserverLocationBean} corresponding to the observer location on the earth
     * @param vpb the {@code ViewingParametersBean} corresponding to the observer view (zoom and look orientation)
     */
    public SkyCanvasManager(StarCatalogue catalog, DateTimeBean dtb, ObserverLocationBean olb, ViewingParametersBean vpb) {

        centerAnimator = new ViewAnimator(vpb);

        canvas = new Canvas();
        painter = new SkyCanvasPainter(canvas);
        skyPane = new AnchorPane(canvas);
        canvas.widthProperty().bind(skyPane.widthProperty());
        canvas.heightProperty().bind(skyPane.heightProperty());
        this.olb = olb;
        this.dtb = dtb;
        this.vpb = vpb;

        mousePosition = new SimpleObjectProperty<>(INITIAL_POS_MOUSE);
        selectedObjectPoint = new SimpleObjectProperty<>(null);
        errorMessage = new SimpleStringProperty("");
        selectedObjectName = "";
        
        drawWithStars   = new SimpleBooleanProperty();
        drawWithPlanets = new SimpleBooleanProperty();
        drawWithAsterisms = new SimpleBooleanProperty();
        drawWithSun     = new SimpleBooleanProperty();
        drawWithMoon    = new SimpleBooleanProperty();
        drawWithHorizon = new SimpleBooleanProperty();

        //BINDINGS =====================================================================================
        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(vpb.getCenter()), vpb.centerProperty());
        
        scaleOfView = Bindings.createDoubleBinding(() ->
                        Math.max(canvas.getWidth(), canvas.getHeight())
                        / projection.get().applyToAngle(Angle.ofDeg(vpb.getFieldOfViewDeg())),
                                canvas.widthProperty(), canvas.heightProperty(), 
                                vpb.fieldOfViewDegProperty(), projection);

        planeToCanvas = Bindings.createObjectBinding(
                () -> Transform.affine(scaleOfView.get(), 0, 0, -scaleOfView.get(),
                            canvas.getWidth()/2, canvas.getHeight()/2),
                            scaleOfView, canvas.widthProperty(), canvas.heightProperty());

        mouseHorizontalPosition = Bindings.createObjectBinding(
                () -> projection.get().inverseApply(mousePosition.get()),
                            mousePosition, projection);

        sky = Bindings.createObjectBinding(
                () -> new ObservedSky(dtb.getZonedDateTime(), olb.getCoordinates(), projection.get(), catalog),
                            projection, olb.coordinatesProperty(), dtb.timeProperty(),
                            dtb.dateProperty(), dtb.zoneProperty());

        mouseAzDeg  = Bindings.createDoubleBinding( () ->
            mouseHorizontalPosition.get().azDeg(),
                mouseHorizontalPosition);
        
        mouseAltDeg = Bindings.createDoubleBinding( () ->
            mouseHorizontalPosition.get().altDeg(),
                mouseHorizontalPosition);

        objectUnderMouse = Bindings.createObjectBinding(
                () -> sky.get().
                objectClosestTo(mousePosition.get(), MAX_DISTANCE_FOR_CLOSEST_OBJECT_TO/scaleOfView.get()),
                    mousePosition, planeToCanvas, sky);

        selectedScreenPoint = Bindings.createObjectBinding(
                () -> {
                    if(screenPointFor(selectedObjectPoint.get()).isPresent()) {
                        CartesianCoordinates sp = screenPointFor(selectedObjectPoint.get()).get();
                        if(isInCanvasLimits(sp)) {
                            return screenPointFor(selectedObjectPoint.get());
                        }
                    }
                    popBackBoxAt(1);
                    return Optional.empty();
                }, planeToCanvas, sky, selectedObjectPoint);


        //RE_DRAW SKY VIA LISTENER ==================================================================
        sky.addListener(e -> painter.actualize(sky.get(), planeToCanvas.get(), 
                drawWithStars.get(), drawWithPlanets.get(), drawWithAsterisms.get(), drawWithSun.get(),
                drawWithMoon.get(), drawWithHorizon.get()));
        
        planeToCanvas.addListener(e -> painter.actualize(sky.get(), planeToCanvas.get(), 
                drawWithStars.get(), drawWithPlanets.get(), drawWithAsterisms.get(), drawWithSun.get(),
                drawWithMoon.get(), drawWithHorizon.get()));

        selectedScreenPoint.addListener(e -> showInfoBoxWith(selectedObjectName, 0));

        //KEYBOARD LISTENER ==============================================================================
        canvas.setOnKeyPressed(e -> {
            double az  = vpb.getCenter().azDeg();
            double alt = vpb.getCenter().altDeg();
            switch (e.getCode()) {
                case UP:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(az, RANGE_OBSERVABLE_ALTITUDES.clip( alt + CHANGE_OF_ALTITUDE_WHEN_KEY_PRESSED)));
                    break;
                case DOWN:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(az, RANGE_OBSERVABLE_ALTITUDES.clip( alt - CHANGE_OF_ALTITUDE_WHEN_KEY_PRESSED)));
                    break;
                case RIGHT:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(ROInter_0To360.reduce( az + CHANGE_OF_AZIMUT_WHEN_KEY_PRESSED), alt));
                    break;
                case LEFT:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(ROInter_0To360.reduce( az - CHANGE_OF_AZIMUT_WHEN_KEY_PRESSED), alt));
                    break;
                default:
            }
            e.consume();
        });

        //MOUSE CLICKED LISTENER ======================================================================
        canvas.setOnMousePressed((e -> {
            if(e.isPrimaryButtonDown()) {
                if(canvas.isFocused()) {
                    if(objectUnderMouse.get().isEmpty()) {
                        removeInfoPanes();
                    } else {
                        HorizontalCoordinates mh = mouseHorizontalPosition.get();
                        boolean newSelection = selectedObjectPoint.get() == null
                                || selectedObjectPoint.get().angularDistanceTo(mh)
                                > MAX_DISTANCE_FOR_CLOSEST_OBJECT_TO/scaleOfView.get();
                        if (newSelection) {
                            CartesianCoordinates mouseScreen = INITIAL_POS_MOUSE;
                            if(screenPointFor(mh).isPresent()) {
                                mouseScreen = screenPointFor(mh).get();
                            }
                            if(isInCanvasLimits(mouseScreen)) {
                                selectedObjectName = objectUnderMouse.get().get().name();
                                selectedDistances = objectUnderMouse.get().get().distances();
                                selectedObjectPoint.setValue(mh);
                            } else {
                                errorMessage.setValue("limite atteinte - bordure visuel");
                                selectedObjectPoint.setValue(null);
                                popBackBoxAt(1);
                            }
                        } else {
                            centerAnimator.setDestination(CINTER_0TO360.reduce(mh.azDeg()),
                                    RANGE_OBSERVABLE_ALTITUDES.clip(mh.altDeg()));
                            centerAnimator.start();
                        }
                    }
                } else {
                    canvas.requestFocus();
                }
            }
            e.consume();
        }));

        //MOUSE MOVE LISTENER =======================================================================
        canvas.setOnMouseMoved((e -> {
            try {
                Point2D mp = planeToCanvas.get().createInverse().transform(e.getX(), e.getY());
                mousePosition.setValue(CartesianCoordinates.of(mp.getX(), mp.getY()));

            } catch (NonInvertibleTransformException error) {
                errorMessage.setValue("projection souris sur plan impossible; cause: transformation non iversible");
            }
            e.consume();
        }));

        //SCROLL LISTENER ===========================================================================
        canvas.setOnScroll(e -> {
            double delta = (Math.abs(e.getDeltaX()) > Math.abs(e.getDeltaY())) 
                            ? e.getDeltaX() 
                            : e.getDeltaY();
            vpb.setFieldOfViewDeg(RANGE_FIELD_OF_VIEW_DEG.clip(vpb.getFieldOfViewDeg() + delta));
            e.consume();
        });
        
    } //End Constructor

    private boolean isInCanvasLimits(CartesianCoordinates screenPoint) {
       // System.out.println(screenPoint.y() < canvas.getHeight() - INFO_BOX_HEIGTH - INFO_BOX_SPACING);
        // TODO magic number and graphic values
        return screenPoint.x() > INFO_BOX_WIDTH/2 && screenPoint.x() < canvas.getWidth() - INFO_BOX_WIDTH/2
                && screenPoint.y() < canvas.getHeight() - INFO_BOX_HEIGTH && screenPoint.y() > 0;
    }

    private Optional<CartesianCoordinates> screenPointFor(HorizontalCoordinates hp) {
        if(hp == null){
            return Optional.empty();
        }
        CartesianCoordinates planePoint = projection.get().apply(hp);
        Point2D screenPoint = planeToCanvas.get().transform(planePoint.x(), planePoint.y());
        return Optional.of(CartesianCoordinates.of(screenPoint.getX(), screenPoint.getY()));
    }

    private void popBackBoxAt(int index) {
        int size = skyPane.getChildren().size();
        if (size > index) {
            skyPane.getChildren().remove(size - index);
        }
    }


    private void showInfoBoxWith(String objectName, double angularSize) {
        if(selectedScreenPoint.get().isPresent()) {
            Polygon triangle = new Polygon();
            triangle.getPoints().addAll(0.0, 0.0, 10.0, 0.0, 5.0, -5.0);
            triangle.setFill(Color.LIGHTGRAY);

            Label name = new Label(objectName);
            name.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
            Label dist = new Label("distance: ");
            dist.setFont(new Font(11));
            /*Label distValue = new Label(String.valueOf(angularSize));
            size.setFont(new Font(7));*/

            VBox textBox = new VBox();
            textBox.setAlignment(Pos.TOP_CENTER);
            textBox.setMinWidth(INFO_BOX_WIDTH - 10);
            textBox.setMinHeight(INFO_BOX_HEIGTH - 10); //TODO value with triangle
            textBox.setStyle("-fx-background-color: lightgray;" + "-fx-padding: 5;"
                    + "-fx-background-radius: 2;");
            textBox.getChildren().addAll(triangle, name, dist);

            VBox infoBox = new VBox();
            infoBox.relocate(selectedScreenPoint.get().get().x() - INFO_BOX_WIDTH/2 + 4, //TODO value
                    selectedScreenPoint.get().get().y() + 4);
            infoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0);" + "-fx-padding: 0;");
            infoBox.setAlignment(Pos.TOP_CENTER);
            infoBox.setMaxWidth(INFO_BOX_WIDTH);
            infoBox.setMaxHeight(INFO_BOX_HEIGTH);

            popBackBoxAt(1);

            infoBox.getChildren().addAll(triangle, textBox);
            skyPane.getChildren().addAll(infoBox);
        }
    }

    protected void goToDestinationWithName(String destination) {
        try {
            CartesianCoordinates destinationOnPlane = sky.get().pointForObjectWithName(destination);
            if (destinationOnPlane != null) {
                HorizontalCoordinates coordinates = projection.get().inverseApply(destinationOnPlane);
                centerAnimator.setDestination(coordinates.azDeg(), coordinates.altDeg());
                centerAnimator.start();
                errorMessage.setValue("");
            } else
                errorMessage.setValue("position géographique invalide pour visualiser cet astre");
        } catch (IllegalArgumentException e) {
            errorMessage.setValue("astre non réferencé");
        }
    }

    public void removeInfoPanes() {
        while(skyPane.getChildren().size() > 1) {
            popBackBoxAt(1);
        }
        selectedObjectPoint.setValue(null);
    }
    
    // getters ======================================================================================
    /**
     * @return the canvas the canvas where the sky is drawn
     */
    public Canvas canvas() {
        return canvas;
    }

    /**
     * @return the pane containing the canvas where the sky is drawn
     */
    public Pane skyPane() {
        return skyPane;
    }
    
    /**
     * @return the ObserverLocationBean
     */
    public ObserverLocationBean observerLocationBean() {
        return olb;
    }
    
    /**
     * 
     * @return the DateTimeBean
     */
    public DateTimeBean dateTimeBean() {
        return dtb;
    }
    
    /**
     * @return the ViewingParametersBean
     */
    public ViewingParametersBean viewingParameterBean() {
        return vpb;
    }

    /**
     * @return the errorMessage of the manager
     */
    public StringProperty errorMessage() {
        return errorMessage;
    }

    /**
     * @return the mouseAzDeg
     */
    public DoubleBinding mouseAzDeg() {
        return mouseAzDeg;
    }

    /**
     * @return the mouseAltDeg
     */
    public DoubleBinding mouseAltDeg() {
        return mouseAltDeg;
    }

    /**
     * @return the objectUnderMouse
     */
    public ObjectBinding<Optional<CelestialObject>> objectUnderMouse() {
        return objectUnderMouse;
    }

    /**
     * @return the drawWithStars property
     */
    public BooleanProperty drawWithStars() {
        return drawWithStars;
    }


    /**
     * @return the drawWithPlanets property
     */
    public BooleanProperty drawWithPlanets() {
        return drawWithPlanets;
    }

    /**
     * @return the drawWithPlanets property
     */
    public BooleanProperty drawWithAsterisms() {
        return drawWithAsterisms;
    }


    /**
     * @return the drawWithSun property
     */
    public BooleanProperty drawWithSun() {
        return drawWithSun;
    }


    /**
     * @return the drawWithMoon property
     */
    public BooleanProperty drawWithMoon() {
        return drawWithMoon;
    }
    
    /**
     * @return the drawWithHorizon property
     */
    public BooleanProperty drawWithHorizon() {
        return drawWithHorizon;
    }
}
