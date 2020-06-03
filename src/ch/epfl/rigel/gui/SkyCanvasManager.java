package ch.epfl.rigel.gui;

import static ch.epfl.rigel.math.RightOpenInterval.ROInter_0To360;

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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
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

    private final static double INFO_BOX_WIDTH = 60;
    private final static double INFO_BOX_HEIGTH = 30;
    private final static double INFO_BOX_SPACING = 5;

    private final Canvas canvas;
    private final SkyCanvasPainter painter;
    private final Pane skyPane;
    private final ObserverLocationBean olb;
    private final DateTimeBean dtb;
    private final ViewingParametersBean vpb;
    
    private final BooleanProperty drawWithStars;
    private final BooleanProperty drawWithHorizon;
    private final BooleanProperty drawWithPlanets;
    private final BooleanProperty drawWithSun;
    private final BooleanProperty drawWithMoon;
    
    private final DoubleBinding mouseAzDeg;
    private final DoubleBinding mouseAltDeg;
    private final ObjectProperty<CartesianCoordinates> mousePosition;
    private final ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition;
    private final ObjectBinding<Optional<CelestialObject>> objectUnderMouse;
    private final ObjectProperty<HorizontalCoordinates> selectedObjectPoint;
    private final ObjectBinding<Optional<CartesianCoordinates>> selectedScreenPoint;
    private String selectedObjectName;

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
        selectedObjectName = "";
        
        drawWithStars   = new SimpleBooleanProperty();
        drawWithPlanets = new SimpleBooleanProperty();
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
                    return screenPointFor(selectedObjectPoint.get());
                }, planeToCanvas, sky, selectedObjectPoint);


        //RE_DRAW SKY VIA LISTENER ==================================================================
        sky.addListener(e -> painter.actualize(sky.get(), planeToCanvas.get(), 
                drawWithStars.get(), drawWithPlanets.get(), drawWithSun.get(),
                drawWithMoon.get(), drawWithHorizon.get()));
        
        planeToCanvas.addListener(e -> painter.actualize(sky.get(), planeToCanvas.get(), 
                drawWithStars.get(), drawWithPlanets.get(), drawWithSun.get(),
                drawWithMoon.get(), drawWithHorizon.get()));

        selectedScreenPoint.addListener(e -> {
            if(selectedScreenPoint.get().isPresent()) {
                CartesianCoordinates ip = selectedScreenPoint.get().get();
                if(ip.x() > INFO_BOX_WIDTH/2 && ip.x() < canvas.getWidth() - INFO_BOX_WIDTH/2
                        && ip.y() < canvas.getHeight() - INFO_BOX_HEIGTH) {
                    showInfoBoxWith(selectedObjectName);
                } else {
                    System.out.println("too close from the edge");
                }
            } });

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
                int size = skyPane.getChildren().size();
                if(canvas.isFocused()) {
                    if(objectUnderMouse.get().isEmpty()) {
                        if (size > 1) {
                            selectedObjectPoint.setValue(null);
                            skyPane.getChildren().remove(size - 1); // TODO duplicate
                        }
                    } else {
                        HorizontalCoordinates mh = mouseHorizontalPosition.get();
                        boolean newSelection = selectedObjectPoint.get() == null
                                || selectedObjectPoint.get().angularDistanceTo(mh) > MAX_DISTANCE_FOR_CLOSEST_OBJECT_TO/scaleOfView.get();//TODO empty check
                        if (newSelection) {
                            System.out.println("set");
                            selectedObjectName = objectUnderMouse.get().get().name();
                            selectedObjectPoint.setValue(mh);
                        } else {
                            centerAnimator.setDestination(CINTER_0TO360.reduce(mh.azDeg()), RANGE_OBSERVABLE_ALTITUDES.clip(mh.altDeg()));
                            centerAnimator.start();
                            /*if (size > 1) {
                                objectOfDisplayBox = null;
                                skyPane.getChildren().remove(size - 1); // TODO duplicate
                            }*/
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
                System.out.println("un-computable mouse coordinates on plane; cause: non invertible transformation");
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

    private Optional<CartesianCoordinates> screenPointFor(HorizontalCoordinates hp) {
        //CartesianCoordinates infoScreenPoint = null;
        //if(informedPoint != null) {
        if(hp == null){
            return Optional.empty();
        }
            HorizontalCoordinates infoPoint = HorizontalCoordinates.of(hp.az(), hp.alt());
            CartesianCoordinates planePoint = projection.get().apply(infoPoint);
            Point2D screenPoint = planeToCanvas.get().transform(planePoint.x(), planePoint.y());
            return Optional.of(CartesianCoordinates.of(screenPoint.getX(), screenPoint.getY()));
        //}
        //System.out.println(infoScreenPoint);
        //return Optional.ofNullable(infoScreenPoint);
    }

    protected void goToDestinationWithName(String destination) {
        try {
            CartesianCoordinates destinationOnPlane = sky.get().pointForObjectWithName(destination);
            if (destinationOnPlane != null) {
                HorizontalCoordinates coordinates = projection.get().inverseApply(destinationOnPlane);
                centerAnimator.setDestination(coordinates.azDeg(), coordinates.altDeg());
                centerAnimator.start();
            } else
                System.out.println("Votre position sur la terre ne vous permet pas de voir cet astre");
        } catch (IllegalArgumentException e) {
            System.out.println("Cet astre n'est pas référencé");
        }
    }

    private void showInfoBoxWith(String objectName) {
        if(selectedScreenPoint.get().isPresent()) {
            HBox infoBox = new HBox();
            /*Polygon triangle = new Polygon();
            triangle.getPoints().addAll(-20.0, 0.0, 15.0, 0.0, 7.5, -10.0);
            triangle.setFill(Color.RED);
            triangle.setStroke(Color.GRAY);
            triangle.relocate(60.0, 60.0);*/

            infoBox.relocate(selectedScreenPoint.get().get().x() - INFO_BOX_WIDTH/2 - INFO_BOX_SPACING,
                    selectedScreenPoint.get().get().y() + 10);
            infoBox.setSpacing(INFO_BOX_SPACING);
            infoBox.setPadding(new Insets(INFO_BOX_SPACING));
            infoBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY,
                    CornerRadii.EMPTY, Insets.EMPTY)));
            Label name = new Label(objectName);
            name.setMinWidth(INFO_BOX_WIDTH);
            name.setMinHeight(INFO_BOX_HEIGTH);
            name.setAlignment(Pos.CENTER);
            infoBox.getChildren().addAll(name);
            skyPane.getChildren().addAll(infoBox);
            int size = skyPane.getChildren().size();
            if (size > 2) {
                skyPane.getChildren().remove(size - 2);
            }
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
