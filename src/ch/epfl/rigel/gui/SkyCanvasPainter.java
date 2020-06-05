package ch.epfl.rigel.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.Planet;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;

/**
 * Used to paint the sky on a given {@code Canvas}
 * Different elements of the sky can be painted separately
 * The sky is given at each draw action
 *
 * @author Augustin ALLARD (299918)
 */
public final class SkyCanvasPainter {

    private final static int DEG_360    = 360;
    private final static int OCTANTS_NB = 8;
    private final static double ALT_OF_OCT_INDICATORS      = -1.5;
    private final static HorizontalCoordinates CENTER_OF_HORIZON_CIRCLE = HorizontalCoordinates.ofDeg(0, 0);
    private final static ClosedInterval RANGE_OF_MAGNITUDE = ClosedInterval.of(-2, 5);
    
    private final static double DIAMETER_FACTOR     = Math.tan(Angle.ofDeg(0.5)/4.0);
    private final static double INFLUENCE_FACT_OF_MAG_ON_SIZE  = 17d / 140d;
    private final static double SIZE_FACTOR_FOR_ZERO_MAGNITUDE = 99d / 140d;
    private final static double SUN_HALO_SCALE_FACT = 2.2;
    private final static double BASIC_SCALE_FACT    = 1;
    private final static double SUN_PLUS_SCALE_ADD  = 2;
    private final static double BASIC_SCALE_ADD     = 0;
    private final static double SUN_HALO_OPACITY    = 0.25;
    private final static double BASIC_OPACITY       = 1;

    private final static int ASTERISM_LINE_WIDTH    = 1;
    private final static int HORIZON_LINE_WIDTH     = 2;
    private final static Color FONT_COLOR           = Color.BLACK;
    private final static Color PLANET_COLOR         = Color.LIGHTGRAY;
    private final static Color ASTERISM_LINE_COLOR  = Color.BLUE;
    private final static Color HORIZON_COLOR        = Color.FIREBRICK;
    private final static String SUN_MAIN_COLOR      = "yellow";
    private final static String BASIC_COLOR         = "white";

    
    private final Canvas canvas;
    private final GraphicsContext graph2D;
    
    private final List<Double> xPos = new ArrayList<>();
    private final List<Double> yPos = new ArrayList<>();
    private HorizontalCoordinates pointOfAlt0;
    private CartesianCoordinates pointOfAlt0inCartesian;
    private Point2D pointOfAlt0inCanvasRef;

    /**
     *
     * @param canvas on which the sky is painted
     */
    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        graph2D = canvas.getGraphicsContext2D();
    }

    /**
     * Clear the previous sky and draw the new one actualize
     *
     * @param sky the new actual sky to draw
     * @param planeToCanvas the new actual transformation to use
     */
    public void actualize(ObservedSky sky, Transform planeToCanvas, 
            boolean withStars, boolean withPlanets, boolean withAsterisms, boolean withSun, boolean withMoon, boolean withHorizon) {
        clear();
        coordinatesHorizonPointsConstruct(sky, planeToCanvas);
        drawSky(sky, planeToCanvas, withStars, withPlanets,withAsterisms, withSun, withMoon, withHorizon);
    }

    /**
     * Draw the sky on the canvas with only the elements that have been selected
     *
     * @param sky that is drawn
     * @param planeToCanvas transformation used for matching projection(observed sky) to the computer screen
     *                      (scaling, translation)
     */
    public void drawSky(ObservedSky sky, Transform planeToCanvas,
                        boolean withStars, boolean withPlanets, boolean withAsterisms,
                        boolean withSun, boolean withMoon, boolean withHorizon) {
        if(withAsterisms)
            drawAsterisms(sky, planeToCanvas);
        if(withStars)
            drawStars(sky, planeToCanvas);
        if(withPlanets)
            drawPlanets(sky, planeToCanvas);
        if(withSun)
            drawSun(sky, planeToCanvas);
        if(withMoon)
            drawMoon(sky, planeToCanvas);
        if(withHorizon)
            drawHorizon(sky.projection(), planeToCanvas);
    }

    /* *************************************************************************
     *                                                                         *
     *                    Internal implementation stuff                        *
     *                                                                         *
     **************************************************************************/
    
    //Clear what has been drawn on the {@code Canvas} and reset it as a black board
    private void clear() {
        graph2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graph2D.setFill(FONT_COLOR);
        graph2D.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    private void coordinatesHorizonPointsConstruct(ObservedSky sky, Transform planeToCanvas) {
        xPos.clear();
        yPos.clear();
        for (int k = 0; k < 64; k++) {
            pointOfAlt0 = HorizontalCoordinates.ofDeg(k*360/64, 0);
            pointOfAlt0inCartesian = sky.projection().apply(pointOfAlt0);
            pointOfAlt0inCanvasRef = 
                    planeToCanvas.transform(
                            pointOfAlt0inCartesian.x(),
                            pointOfAlt0inCartesian.y());
            
            xPos.add(pointOfAlt0inCanvasRef.getX());
            yPos.add(pointOfAlt0inCanvasRef.getY());
        }
    }

    private void drawAsterisms(ObservedSky sky, Transform planeToCanvas) {
        int length = sky.starPointsRefs().length;
        double[] screenPoints = new double[length];
        planeToCanvas.transform2DPoints(sky.starPointsRefs(), 0, screenPoints, 0, length/2);

        graph2D.setLineWidth(ASTERISM_LINE_WIDTH);
        graph2D.beginPath();
        graph2D.setStroke(ASTERISM_LINE_COLOR);

        for(Asterism a: sky.asterisms()) {

            Iterator<Integer> iteratorOverID = sky.asterismIndices(a).iterator();
            int idOfStarFrom = iteratorOverID.next();

            while(iteratorOverID.hasNext()) {

                int idOfStarTo = iteratorOverID.next();
                double xFr = screenPoints[2*idOfStarFrom];
                double yFr = screenPoints[2*idOfStarFrom+1];
                double xTo = screenPoints[2*idOfStarTo];
                double yTo = screenPoints[2*idOfStarTo+1];

                if(canvas.getBoundsInLocal().contains(xFr, yFr) || canvas.getBoundsInLocal().contains(xTo, yTo)) {
                    graph2D.moveTo(xFr, yFr);
                    graph2D.lineTo(xTo, yTo);
                }
                idOfStarFrom = idOfStarTo;
            }
        }
        graph2D.stroke();
        graph2D.closePath();
    }

    private void drawStars(ObservedSky sky, Transform planeToCanvas) {
        
        int length = sky.starPointsRefs().length;
        double[] screenPoints = new double[length];
        planeToCanvas.transform2DPoints(sky.starPointsRefs(), 0, screenPoints, 0, length/2);

        int i = 0;
        for(Star s: sky.stars()) {
            drawEllipseOf(BlackBodyColor.colorForTemperature(s.colorTemperature(), screenPoints[i+1] < altOfHorizonInCanvasCoodinates(sky, planeToCanvas, screenPoints[i])),
                    screenPoints[i], screenPoints[i + 1], s.magnitude(), planeToCanvas);
            i += 2;
        }
    }

    private void drawPlanets(ObservedSky sky, Transform planeToCanvas) {
        int l = sky.planetPointsRefs().length;
        double[] screenPoints = new double[l];
        planeToCanvas.transform2DPoints(sky.planetPointsRefs(), 0, screenPoints, 0, l/2);

        int i = 0;
        for(Planet p: sky.planets()) {
            drawEllipseOf(PLANET_COLOR, screenPoints[i], screenPoints[i + 1], p.magnitude(), planeToCanvas);
            i += 2;
        }
    }

    private void drawSun(ObservedSky sky, Transform planeToCanvas) {
        double sunAngSize = sky.sun().angularSize();
        CartesianCoordinates sunPoint = sky.sunPoint();
        
        drawEllipseOf(SUN_MAIN_COLOR, sunPoint, sunAngSize, planeToCanvas,
                SUN_HALO_SCALE_FACT, BASIC_SCALE_ADD, SUN_HALO_OPACITY);
        
        drawEllipseOf(SUN_MAIN_COLOR, sunPoint, sunAngSize, planeToCanvas,
                BASIC_SCALE_FACT, SUN_PLUS_SCALE_ADD, BASIC_OPACITY);
        
        drawEllipseOf(sunPoint, sunAngSize, planeToCanvas);
    }

    private void drawMoon(ObservedSky sky, Transform planeToCanvas) {
        drawEllipseOf(sky.moonPoint(), sky.moon().angularSize(), planeToCanvas);
    }

    private void drawHorizon(StereographicProjection projection, Transform planeToCanvas) {
        CartesianCoordinates center  = projection.circleCenterForParallel(CENTER_OF_HORIZON_CIRCLE);
        Point2D screenPointForCenter = planeToCanvas.transform(center.x(), center.y());
        
        double radius = projection.circleRadiusForParallel(CENTER_OF_HORIZON_CIRCLE) * planeToCanvas.getMxx();
        double diameter = 2*radius;
        
        graph2D.setStroke(HORIZON_COLOR);
        graph2D.setLineWidth(HORIZON_LINE_WIDTH);
        graph2D.strokeOval(screenPointForCenter.getX() - radius, screenPointForCenter.getY() - radius, diameter, diameter);

        double az = 0; // to draw the 8 octants 
        for(int i = 0; i < OCTANTS_NB; ++i) {
            HorizontalCoordinates cardinalCoord = HorizontalCoordinates.ofDeg(az, ALT_OF_OCT_INDICATORS);
            drawCardinal(cardinalCoord, cardinalCoord.azOctantName(), projection, planeToCanvas);
            az += DEG_360/OCTANTS_NB;
        }
    }
    
    private void drawCardinal(HorizontalCoordinates c, String text,
            StereographicProjection projection, Transform planeToCanvas) {
        CartesianCoordinates s = projection.apply(c);
        Point2D screenPoint = planeToCanvas.transform(s.x(), s.y());
        System.out.println(screenPoint.getX());
        graph2D.strokeText(text, screenPoint.getX(), screenPoint.getY());
    }

    private void drawEllipseOf(String color, CartesianCoordinates planePoint, double angularSize, Transform planeToCanvas,
                               double scaleFact, double scaleAdd, double opacity) {
        Point2D screenPoint = planeToCanvas.transform(planePoint.x(), planePoint.y());
        double radius = Math.tan(angularSize/4)*planeToCanvas.getMxx()*scaleFact + scaleAdd;
        double diameter = 2*radius;
        
        graph2D.setFill(Color.web(color, opacity));
        graph2D.fillOval(screenPoint.getX() - radius, screenPoint.getY() - radius, diameter, diameter);
    }

    private void drawEllipseOf(CartesianCoordinates planePoint, double angularSize, Transform planeToCanvas) {
        drawEllipseOf(BASIC_COLOR, planePoint, angularSize, planeToCanvas, BASIC_SCALE_FACT, BASIC_SCALE_ADD, BASIC_OPACITY);
    }

    private void drawEllipseOf(Color color, double planeX, double planeY, double magnitude, Transform planeToCanvas) {
        double clipedMagnitude = RANGE_OF_MAGNITUDE.clip(magnitude);
        double sizeFactor = SIZE_FACTOR_FOR_ZERO_MAGNITUDE - INFLUENCE_FACT_OF_MAG_ON_SIZE*clipedMagnitude;
        
        double radius = sizeFactor * DIAMETER_FACTOR * planeToCanvas.getMxx();
        double diameter = 2*radius;
        
        graph2D.setFill(color);
        graph2D.fillOval(planeX - radius, planeY - radius, diameter, diameter);
    }
    
    private double altOfHorizonInCanvasCoodinates(ObservedSky sky, Transform planeToCanvas, double xPosStar) {
              
        Optional<Double> o = xPos.
                             stream().
                             map(x -> Math.abs(x - xPosStar)).
                             min(Comparator.naturalOrder());
       double possibleVal1 = -o.get() + xPosStar;
       double possibleVal2 =  o.get() + xPosStar;
       int index = -2;
       if(xPos.contains(possibleVal1))
           index = xPos.lastIndexOf(possibleVal1);
       else
           index = xPos.lastIndexOf(possibleVal2);
       return yPos.get(index);
    }
}
