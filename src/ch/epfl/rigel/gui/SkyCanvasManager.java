package ch.epfl.rigel.gui;

import static ch.epfl.rigel.math.RightOpenInterval.ROInter_0To360;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
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

    private final static double MOON_DIST_DIVIDER = 1000;
    private final static ClosedInterval ALTITUDE_RANGE  = ClosedInterval.of(5, 90);
    private final static ClosedInterval ZOOM_RANGE= ClosedInterval.of(30, 150);
    private final static RightOpenInterval CINTER_0TO360 = RightOpenInterval.of(0, 360);
    private final static int TOLERANCE_FOR_OBJ_DETECTION = 10;
    private final static int AZIMUT_MOVE_STEP = 2;
    private final static int ALTITUDE_MOVE_STEP = 1;
    private final static CartesianCoordinates ZERO_POSITION = CartesianCoordinates.of(0, 0);
    //--------------------------------------------------------------------------------------------
    private final static double INFO_BOX_WIDTH = 100;
    private final static double INFO_BOX_HEIGTH = 70;
    private final static double INFO_BOX_DOWN_SHIFT = 5;
    private final static double POINTER_SIZE = 10;
    private final static double DEG_BORDER_MARGIN = 0.3;
    private final static double CONSTANT_SHIFT_AT_DOWN_BORDER = 4.0/68.4;

    private final Canvas canvas;
    private final SkyCanvasPainter painter;
    
    private final BooleanProperty drawWithStars;
    private final BooleanProperty drawWithHorizon;
    private final BooleanProperty drawWithPlanets;
    private final BooleanProperty drawWithAsterisms;
    private final BooleanProperty drawWithSun;
    private final BooleanProperty drawWithMoon;

    private final ObserverLocationBean olb;
    private final DateTimeBean dtb;
    private final ViewingParametersBean vpb;
    private final DoubleBinding mouseAzDeg;
    private final DoubleBinding mouseAltDeg;
    private final ObjectProperty<CartesianCoordinates> mousePosition;
    private final ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition;
    private final ObjectBinding<Optional<CelestialObject>> objectUnderMouse;

    private final ObjectBinding<ObservedSky> sky;
    private final ObjectBinding<StereographicProjection> projection;
    private final DoubleBinding scaleOfView;
    private final ObjectBinding<Transform> planeToCanvas;
    //--------------------------------------------------------------------------------------------
    private final ViewAnimator centerAnimator;

    private final Pane skyPane;
    private final ObjectProperty<ArrayList<CelestialObject>> selectedObjects;
    private final ObjectProperty<Optional<CelestialObject>> waitingRepositioningObject;
    private final IntegerProperty selectionsNumber;
    private final StringProperty errorMessage;
    private boolean overlappingInfos;
    
    /**
     *
     * @param catalog the catalog of stars that will be painted on the canvas
     * @param dtb the {@code DateTimeBean} corresponding to the observation time
     * @param olb the {@code ObserverLocationBean} corresponding to the observer location on the earth
     * @param vpb the {@code ViewingParametersBean} corresponding to the observer view (zoom and look orientation)
     */
    public SkyCanvasManager(StarCatalogue catalog, DateTimeBean dtb, ObserverLocationBean olb, ViewingParametersBean vpb) {

        canvas = new Canvas();
        painter = new SkyCanvasPainter(canvas);

        drawWithStars   = new SimpleBooleanProperty();
        drawWithPlanets = new SimpleBooleanProperty();
        drawWithAsterisms = new SimpleBooleanProperty();
        drawWithSun     = new SimpleBooleanProperty();
        drawWithMoon    = new SimpleBooleanProperty();
        drawWithHorizon = new SimpleBooleanProperty();

        this.olb = olb;
        this.dtb = dtb;
        this.vpb = vpb;
        mousePosition = new SimpleObjectProperty<>(ZERO_POSITION);

        //--------------------------------------------------------------------------------------------
        centerAnimator = new ViewAnimator(vpb);
        skyPane = new AnchorPane(canvas);
        canvas.widthProperty().bind(skyPane.widthProperty());
        canvas.heightProperty().bind(skyPane.heightProperty());
        selectedObjects = new SimpleObjectProperty<>(new ArrayList<>());
        waitingRepositioningObject = new SimpleObjectProperty<>(Optional.empty());
        selectionsNumber = new SimpleIntegerProperty(0);
        errorMessage = new SimpleStringProperty("");

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
                objectClosestTo(mousePosition.get(), TOLERANCE_FOR_OBJ_DETECTION/scaleOfView.get()),
                    mousePosition, planeToCanvas, sky);

        //RE_DRAW VIA LISTENER ======================================================================
        sky.addListener(e -> actualizeAstronomy());
        
        planeToCanvas.addListener(e -> actualizeAstronomy());

        //--------------------------------------------------------------------------------------------
        selectionsNumber.addListener((p,o,n) -> {
            if(o.intValue() < n.intValue()) {
                showInfoBoxFor(selectedObjects.get().get(n.intValue() - 1));
                if(!overlappingInfos) {
                    popBackBoxWithShift(1);
                }
            } else if(n.intValue() == 0) {
                removePanes();
            } else {
                popBackBoxWithShift(0);
            }
        });

        centerAnimator.runningProperty().addListener(e -> {
            if(!centerAnimator.runningProperty().get() && waitingRepositioningObject.get().isPresent()) {
                addSelection(waitingRepositioningObject.get().get());
                waitingRepositioningObject.setValue(Optional.empty());
                cleanErrors();
            }
        });

        //KEYBOARD PRESS LISTENER ====================================================================
        canvas.setOnKeyPressed(e -> {
            double az  = vpb.getCenter().azDeg();
            double alt = vpb.getCenter().altDeg();
            switch (e.getCode()) {
                case UP:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(az, ALTITUDE_RANGE.clip( alt + ALTITUDE_MOVE_STEP)));
                    break;
                case DOWN:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(az, ALTITUDE_RANGE.clip( alt - ALTITUDE_MOVE_STEP)));
                    break;
                case RIGHT:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(ROInter_0To360.reduce( az + AZIMUT_MOVE_STEP), alt));
                    break;
                case LEFT:
                    vpb.setCenter(HorizontalCoordinates.ofDeg(ROInter_0To360.reduce( az - AZIMUT_MOVE_STEP), alt));
                    break;
                case CONTROL:
                case COMMAND:
                    overlappingInfos = true;
                    break;
                case BACK_SPACE:
                    removeSelection();
                default:
            }
            e.consume();
        });

        //KEYBOARD RELEASE LISTENER ==================================================================
        canvas.setOnKeyReleased(e -> {
            if(e.getCode() == KeyCode.COMMAND) {
                overlappingInfos = false;
            }
            e.consume();
        });

        //MOUSE CLICKED LISTENER ======================================================================
        canvas.setOnMousePressed((e -> {
            if(e.isPrimaryButtonDown()) {
                if(canvas.isFocused()) {
                    cleanErrors();
                    HorizontalCoordinates mh = mouseHorizontalPosition.get();
                    Optional<CelestialObject> objectClicked = sky.get().objectClosestTo(mousePosition.get(),
                            TOLERANCE_FOR_OBJ_DETECTION/scaleOfView.get());
                    if(objectClicked.isEmpty() || !sky.get().isVisible(mousePosition.get())) {
                        clearSelections();
                    } else {
                        List<CelestialObject> selected = selectedObjects.get();
                        boolean newSelection = selected.isEmpty()
                                || objectClicked.get() != selected.get(selected.size() - 1);
                        if (newSelection) {
                            if(isInCanvasLimits(screenPointFor(mh))) {
                                addSelection(objectClicked.get());
                            } else {
                                double deltaAz = 0;
                                double deltaAlt = 0;
                                CartesianCoordinates sp = screenPointFor(mh);
                                if (sp.x() <= INFO_BOX_WIDTH / 2) {
                                    deltaAz -= (INFO_BOX_WIDTH / 2 - sp.x()) * vpb.getFieldOfViewDeg() / canvas.getWidth()
                                            + DEG_BORDER_MARGIN;
                                } else if (sp.x() >= canvas.getWidth() - INFO_BOX_WIDTH / 2) {
                                    deltaAz += (INFO_BOX_WIDTH / 2 + sp.x() - canvas.getWidth())
                                            * vpb.getFieldOfViewDeg() / canvas.getWidth() + DEG_BORDER_MARGIN;
                                }
                                if(sp.y() >= canvas.getHeight() - INFO_BOX_HEIGTH)
                                    deltaAlt -= CONSTANT_SHIFT_AT_DOWN_BORDER*vpb.getFieldOfViewDeg();
                                centerAnimator.setDestination(
                                        CINTER_0TO360.reduce(vpb.getCenter().azDeg() + deltaAz),
                                        ALTITUDE_RANGE.clip(vpb.getCenter().altDeg() + deltaAlt));
                                centerAnimator.start();
                                waitingRepositioningObject.setValue(objectClicked);
                                errorMessage.setValue("limite atteinte - bordure visuel"); //TODO move instead
                            }
                        } else {
                            centerAnimator.setDestination(CINTER_0TO360.reduce(mh.azDeg()),
                                    ALTITUDE_RANGE.clip(mh.altDeg()));
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
            vpb.setFieldOfViewDeg(ZOOM_RANGE.clip(vpb.getFieldOfViewDeg() + delta));
            e.consume();
        });
    }

    //====================================================================================================
    //=================================== IMPLEMENTATION STUFF ===========================================
    //====================================================================================================

    private void addSelection(CelestialObject object) {
        if(!overlappingInfos)
            clearSelections();
        selectedObjects.get().add(object);
        selectionsNumber.setValue(selectedObjects.get().size());
    }

    private void removeSelection() {
        selectedObjects.get().remove(selectionsNumber.get() - 1);
        selectionsNumber.setValue(selectedObjects.get().size());
    }

    private void clearSelections() {
        selectedObjects.get().clear();
        selectionsNumber.setValue(0);
    }
    //--------------------------------------------------------------------------------------------
    private void popBackBoxWithShift(int shift) {
        int size = skyPane.getChildren().size();
        if (size - 1 > shift) {
            skyPane.getChildren().remove(size - 1 - shift);
        }
    }

    private void removePanes() {
        while(skyPane.getChildren().size() > 1) {
            popBackBoxWithShift(0);
        }
    }

    private void actualizeAstronomy() {
        painter.actualize(sky.get(), planeToCanvas.get(),
                drawWithStars.get(), drawWithPlanets.get(), drawWithAsterisms.get(), drawWithSun.get(),
                drawWithMoon.get(), drawWithHorizon.get());
        removePanes();
        for(CelestialObject o : selectedObjects.get()) {
            if(sky.get().horizontalPointOf(o) != null
                    && isInCanvasLimits(screenPointFor(sky.get().horizontalPointOf(o))))
                showInfoBoxFor(o);
        }
    }
    //--------------------------------------------------------------------------------------------
    private boolean isInCanvasLimits(CartesianCoordinates screenPoint) {
        return screenPoint.x() > INFO_BOX_WIDTH/2 && screenPoint.x() < canvas.getWidth() - INFO_BOX_WIDTH/2
                && screenPoint.y() < canvas.getHeight() - INFO_BOX_HEIGTH;
    }

    //--------------------------------------------------------------------------------------------
    private CartesianCoordinates screenPointFor(HorizontalCoordinates hp) {
        CartesianCoordinates planePoint = projection.get().apply(hp);
        Point2D screenPoint = planeToCanvas.get().transform(planePoint.x(), planePoint.y());
        return CartesianCoordinates.of(screenPoint.getX(), screenPoint.getY());
    }

    private void showInfoBoxFor(CelestialObject object) {
        // init ---------------------------------------------------------
        Polygon triangle = new Polygon();
        VBox textBox = new VBox();
        textBox.setAlignment(Pos.TOP_CENTER);
        textBox.setMinWidth(INFO_BOX_WIDTH);
        textBox.setMinHeight(INFO_BOX_HEIGTH - POINTER_SIZE);
        VBox infoBox = new VBox();
        infoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0);" + "-fx-padding: 0;");
        infoBox.setAlignment(Pos.TOP_CENTER);
        infoBox.setMaxWidth(INFO_BOX_WIDTH);
        infoBox.setMaxHeight(INFO_BOX_HEIGTH);

        // set labels ---------------------------------------------------
        Label name = new Label(object.name());
        name.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        Label unity = new Label("distance en UA");
        Label ids = new Label("Mini  Maxi  Moy");
        Label values = new Label();
        unity.setFont(Font.font("Verdana", FontWeight.LIGHT, 11));
        ids.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        values.setFont(Font.font("Verdana", FontWeight.BOLD, 10));

        // fill text ----------------------------------------------------
        textBox.getChildren().addAll(name, unity);
        DecimalFormat twoDecimals = new DecimalFormat("#.##");
        if(object == sky.get().sun()) {
            values.setText(twoDecimals.format(object.distances()[0]));
        } else if(object == sky.get().moon()) {
            name.setText(object.info());
            unity.setText("dist. millier KM");
            double[] dist = object.distances();
            values.setText(String.valueOf((int)dist[0]/MOON_DIST_DIVIDER).substring(0, 3) + "   "
                    + String.valueOf(dist[1] / MOON_DIST_DIVIDER).substring(0, 3) + "   "
                    + String.valueOf(dist[2] / MOON_DIST_DIVIDER).substring(0, 3));
            textBox.getChildren().add(ids);
        } else if(object.angularSize() != 0) { //TODO find better
            double[] dist = object.distances();
            values.setText(twoDecimals.format(dist[0]) + "  " + twoDecimals.format(dist[1]) + "  "
                    + twoDecimals.format(dist[2]));
            textBox.getChildren().add(ids);
        } else {
            unity.setText("distance en AL");
            values.setText(twoDecimals.format(object.distances()[0]));
        }
        textBox.getChildren().add(values);

        // form of box --------------------------------------------------
        triangle.getPoints().addAll(0.0, 0.0, POINTER_SIZE, 0.0,
                POINTER_SIZE/2, -POINTER_SIZE/2);
        triangle.setFill(Color.LIGHTGRAY);
        textBox.setStyle("-fx-background-color: lightgray;" + "-fx-padding: 5;"
                + "-fx-background-radius: 2;");

        // info box (transparent) containing the text box and the pointer
        CartesianCoordinates screenPoint = screenPointFor(sky.get().horizontalPointOf(object));
        infoBox.relocate(screenPoint.x() - INFO_BOX_WIDTH/2,
                screenPoint.y() + INFO_BOX_DOWN_SHIFT);
        infoBox.getChildren().addAll(triangle, textBox);

        skyPane.getChildren().addAll(infoBox);
    }

    private void cleanErrors() {
        errorMessage.setValue("");
    }

    //====================================================================================================
    //=================================== PUBLIC INTERFACE ===============================================
    //====================================================================================================

    /**
     * Make a look (projection center) travelling to the object corresponding to the destination name
     * if it exists and is visible in the sky
     *
     * @param name the object to look (with travelling)
     */
    public void goToDestinationWithName(String name) {
        try {
            cleanErrors();
            HorizontalCoordinates destination = sky.get().availableDestinationForObjectNamed(name);
            if (destination != null) {
                centerAnimator.setDestination(destination.azDeg(), destination.altDeg());
                addSelection(sky.get().objectClosestTo(projection.get().apply(destination),
                                TOLERANCE_FOR_OBJ_DETECTION/scaleOfView.get()).get());
                centerAnimator.start();
                cleanErrors();
            } else
                errorMessage.setValue("position géographique invalide pour visualiser cet astre");
        } catch (IllegalArgumentException e) {
            errorMessage.setValue("astre non réferencé");
        }
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
