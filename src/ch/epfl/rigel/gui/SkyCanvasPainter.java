package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.Planet;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import com.sun.javafx.geom.Rectangle;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Transform;

public class SkyCanvasPainter {

    Canvas canvas;
    GraphicsContext graph2D;

    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        graph2D = canvas.getGraphicsContext2D();
        clear();
    }

    private void drawEllipseOf(String color, CartesianCoordinates planePoint, double angularSize, Transform planeToCanva,
                               double scaleFact, double scaleAdd, double opacity) {
        Point2D screenPoint = planeToCanva.transform(planePoint.x(), planePoint.y());
        double d = 2*Math.tan(angularSize/4)*planeToCanva.getMxx()*scaleFact + scaleAdd;
        graph2D.setFill(Color.web(color, opacity));
        graph2D.fillOval(screenPoint.getX() - d/2, screenPoint.getY() - d/2, d, d);
    }

    private void drawEllipseOf(String color, CartesianCoordinates planePoint, double angularSize, Transform planeToCanva) {
        drawEllipseOf(color, planePoint, angularSize, planeToCanva, 1, 0, 1);
    }

    private void drawEllipseOf(Color color, double planeX, double planeY, double magnitude, Transform planeToCanva) {
        double m = ClosedInterval.of(-2, 5).clip(magnitude);
        double f = (99 - 17*m)/140;
        double d = f*2*Math.tan(Angle.ofDeg(0.5)/4.0)*planeToCanva.getMxx();
        graph2D.setFill(color);
        graph2D.fillOval(planeX - d/2, planeY - d/2, d, d);
    }

    public void clear() {
        graph2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graph2D.setFill(Color.BLACK);
        graph2D.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void drawSky(ObservedSky sky, StereographicProjection projection, Transform planeToCanva) {
        drawSky(sky, projection, planeToCanva, true, true, true, true, true);
    }

    public void drawSky(ObservedSky sky, StereographicProjection projection, Transform planeToCanva,
                        boolean withStars, boolean withPlanets, boolean withSun, boolean withMoon, boolean withHorizon) {
        if(withStars)
            drawStars(sky, projection, planeToCanva);
        if(withPlanets)
            drawPlanets(sky, projection, planeToCanva);
        if(withSun)
            drawSun(sky, projection, planeToCanva);
        if(withMoon)
            drawMoon(sky, projection, planeToCanva);
        if(withHorizon)
            drawHorizon(sky, projection, planeToCanva);
    }

    public void drawStars(ObservedSky sky, StereographicProjection projection, Transform planeToCanva) {
        int l = sky.starPointsRefs().length;
        double[] screenPoints = new double[l];
        planeToCanva.transform2DPoints(sky.starPointsRefs(), 0, screenPoints, 0, l/2);

        graph2D.setLineWidth(1);
        graph2D.beginPath();
        graph2D.setStroke(Color.BLUE);
        for(Asterism a: sky.asterisms()) {
            for(int j = 0; j < sky.asterismIndices(a).size() - 1; ++j) {
                int idOfStarFrom = sky.asterismIndices(a).get(j);
                int idOfStarTo = sky.asterismIndices(a).get(j + 1);
                double xFr = screenPoints[2*idOfStarFrom];
                double yFr = screenPoints[2*idOfStarFrom+1];
                double xTo = screenPoints[2*idOfStarTo];
                double yTo = screenPoints[2*idOfStarTo+1];
                if(canvas.getBoundsInLocal().contains(xFr, yFr) || canvas.getBoundsInLocal().contains(xTo, yTo)) {
                    graph2D.moveTo(xFr, yFr);
                    graph2D.lineTo(xTo, yTo);
                }
            }
        }
        graph2D.stroke();
        graph2D.closePath();

        int i = 0;
        for(Star s: sky.stars()) {
            drawEllipseOf(BlackBodyColor.colorForTemperature(s.colorTemperature()),
                    screenPoints[i], screenPoints[i + 1], s.magnitude(), planeToCanva);
            i += 2;
        }
    }

    public void drawPlanets(ObservedSky sky, StereographicProjection projection, Transform planeToCanva) {
        int l = sky.planetPointsRefs().length;
        double[] screenPoints = new double[l];
        planeToCanva.transform2DPoints(sky.planetPointsRefs(), 0, screenPoints, 0, l/2);

        int i = 0;
        for(Planet p: sky.planets()) {
            drawEllipseOf(Color.LIGHTGRAY, screenPoints[i], screenPoints[i + 1], p.magnitude(), planeToCanva);
            i += 2;
        }
    }

    public void drawSun(ObservedSky sky, StereographicProjection projection, Transform planeToCanva) {
        drawEllipseOf("yellow", sky.sunPoint(), sky.sun().angularSize(), planeToCanva,
                2.2, 0, 0.25);
        drawEllipseOf("yellow", sky.sunPoint(), sky.sun().angularSize(), planeToCanva,
                1, 2, 1);
        drawEllipseOf("white", sky.sunPoint(), sky.sun().angularSize(), planeToCanva);
    }

    public void drawMoon(ObservedSky sky, StereographicProjection projection, Transform planeToCanva) {
        drawEllipseOf("white", sky.moonPoint(), sky.moon().angularSize(), planeToCanva);
    }

    private void drawHorizon(ObservedSky sky, StereographicProjection projection, Transform planeToCanva) {
        CartesianCoordinates center = projection.circleCenterForParallel(HorizontalCoordinates.ofDeg(0, 0));
        Point2D screenPointForCenter = planeToCanva.transform(center.x(), center.y());
        double r = projection.circleRadiusForParallel(HorizontalCoordinates.ofDeg(0, 0))*planeToCanva.getMxx();
        graph2D.setStroke(Color.RED);
        graph2D.setLineWidth(2);
        graph2D.strokeOval(screenPointForCenter.getX() - r, screenPointForCenter.getY() - r, 2*r, 2*r);

        // TODO Search for 0.5 instead of 1.5
        CartesianCoordinates s = projection.apply(HorizontalCoordinates.ofDeg(180, -1.5));
        Point2D screenSud = planeToCanva.transform(s.x(), s.y());
        System.out.println(screenSud.getX());
        System.out.println(screenSud.getY());
        graph2D.strokeText("S", screenSud.getX(), screenSud.getY());
    }
}
