package ch.epfl.rigel.gui;

import java.util.Iterator;

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
public final class SkyCanvasPainter { // TODO Check if ok with removed projections

    private final static double ALTITUDE_OF_OCTANTS = -1.5;
    private final static int ANGLE_IN_DEGREES_BETWEEN_OCTANTS = 45;
    private final static ClosedInterval RANGE_OF_MAGNITUDE = ClosedInterval.of(-2, 5);
    private final static HorizontalCoordinates CENTER_OF_HORIZON_CIRCLE = HorizontalCoordinates.ofDeg(0, 0);
    
    private Canvas canvas;
    private GraphicsContext graph2D;

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
    public void actualize(ObservedSky sky, Transform planeToCanvas) {
        clear();
        drawSky(sky, planeToCanvas);
    }

    /**
     * Clear what has been drawn on the {@code Canvas} and reset it as a black board
     */
    public void clear() {
        graph2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graph2D.setFill(Color.BLACK);
        graph2D.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Draw the sky on the canvas with all its elements apparent
     *
     * @param sky that is drawn
     * @param planeToCanvas transformation used for matching projection(observed sky) to the computer screen
     *                      (scaling, translation)
     */
    public void drawSky(ObservedSky sky, Transform planeToCanvas) {
        drawSky(sky, planeToCanvas, true, true, true, true, true);
    }

    /**
     * Draw the sky on the canvas with only the elements that have been selected
     *
     * @param sky that is drawn
     * @param planeToCanvas transformation used for matching projection(observed sky) to the computer screen
     *                      (scaling, translation)
     */
    public void drawSky(ObservedSky sky, Transform planeToCanvas,
                        boolean withStars, boolean withPlanets, boolean withSun, boolean withMoon, boolean withHorizon) {
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

    private void drawStars(ObservedSky sky, Transform planeToCanvas) {
        int length = sky.starPointsRefs().length;
        double[] screenPoints = new double[length];
        planeToCanvas.transform2DPoints(sky.starPointsRefs(), 0, screenPoints, 0, length/2);

        graph2D.setLineWidth(1);
        graph2D.beginPath();
        graph2D.setStroke(Color.BLUE);
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

        int i = 0;
        for(Star s: sky.stars()) {
            drawEllipseOf(BlackBodyColor.colorForTemperature(s.colorTemperature()),
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
            drawEllipseOf(Color.LIGHTGRAY, screenPoints[i], screenPoints[i + 1], p.magnitude(), planeToCanvas);
            i += 2;
        }
    }

    private void drawSun(ObservedSky sky, Transform planeToCanvas) { //should do like dans l'enoncé
        drawEllipseOf("yellow", sky.sunPoint(), sky.sun().angularSize(), planeToCanvas,
                2.2, 0, 0.25);
        drawEllipseOf("yellow", sky.sunPoint(), sky.sun().angularSize(), planeToCanvas,
                1, 2, 1);
        drawEllipseOf(sky.sunPoint(), sky.sun().angularSize(), planeToCanvas);
    }

    private void drawMoon(ObservedSky sky, Transform planeToCanvas) {
        drawEllipseOf(sky.moonPoint(), sky.moon().angularSize(), planeToCanvas);
    }

    private void drawHorizon(StereographicProjection projection, Transform planeToCanvas) {
        CartesianCoordinates center = projection.circleCenterForParallel(CENTER_OF_HORIZON_CIRCLE);
        Point2D screenPointForCenter = planeToCanvas.transform(center.x(), center.y());
        double r = projection.circleRadiusForParallel(CENTER_OF_HORIZON_CIRCLE)*planeToCanvas.getMxx();
        graph2D.setStroke(Color.RED);
        graph2D.setLineWidth(2);
        graph2D.strokeOval(screenPointForCenter.getX() - r, screenPointForCenter.getY() - r, 2*r, 2*r);

        double az = 0;
        for(int i = 0; i < 8; ++i) {
            HorizontalCoordinates c = HorizontalCoordinates.ofDeg(az, ALTITUDE_OF_OCTANTS);
            drawCardinal(c, c.azOctantName(), projection, planeToCanvas);
            az += ANGLE_IN_DEGREES_BETWEEN_OCTANTS;
        }
    }

    private void drawEllipseOf(String color, CartesianCoordinates planePoint, double angularSize, Transform planeToCanvas,
                               double scaleFact, double scaleAdd, double opacity) {
        Point2D screenPoint = planeToCanvas.transform(planePoint.x(), planePoint.y());
        double d = 2*Math.tan(angularSize/4)*planeToCanvas.getMxx()*scaleFact + scaleAdd;
        graph2D.setFill(Color.web(color, opacity));
        graph2D.fillOval(screenPoint.getX() - d/2, screenPoint.getY() - d/2, d, d);
    }

    private void drawEllipseOf(CartesianCoordinates planePoint, double angularSize, Transform planeToCanvas) {
        drawEllipseOf("white", planePoint, angularSize, planeToCanvas, 1, 0, 1);
    }

    private void drawEllipseOf(Color color, double planeX, double planeY, double magnitude, Transform planeToCanvas) {
        double m = RANGE_OF_MAGNITUDE.clip(magnitude);
        double f = (99 - 17*m)/140;
        double d = f*2*Math.tan(Angle.ofDeg(0.5)/4.0)*planeToCanvas.getMxx();
        graph2D.setFill(color);
        graph2D.fillOval(planeX - d/2, planeY - d/2, d, d);
    }

    private void drawCardinal(HorizontalCoordinates c, String text,
                              StereographicProjection projection, Transform planeToCanvas) {
        CartesianCoordinates s = projection.apply(c);
        Point2D screenSud = planeToCanvas.transform(s.x(), s.y());
        graph2D.strokeText(text, screenSud.getX(), screenSud.getY());
    }
}
