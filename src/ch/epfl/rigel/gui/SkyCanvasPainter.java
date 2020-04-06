package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.Planet;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.coordinates.StereographicProjection;
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

    public void clear() {
        graph2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graph2D.setFill(Color.BLACK);
        graph2D.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    // TODO Can't see anything
    public void drawMoon(ObservedSky sky, StereographicProjection projection, Transform planeToCanva) {
        Point2D screenPoint = planeToCanva.transform(sky.moonPoint().x(), sky.moonPoint().y());
        graph2D.setFill(Color.WHITE);
        graph2D.fillOval(screenPoint.getX(), screenPoint.getY(), 100, 100); // TODO Factor & shift
    }

    public void drawPlanets(ObservedSky sky, StereographicProjection projection, Transform planeToCanva) {
        int coordNumber = sky.planetPointsRefs().length;
        double[] screenPoints = new double[coordNumber];
        planeToCanva.transform2DPoints(sky.planetPointsRefs(), 0, screenPoints, 0, coordNumber/2);

        int i = 0;
        for(Planet p: sky.planets()) {
            graph2D.setFill(Color.LIGHTGRAY);
            // TODO Factor (angular size * distance of planet??) & shift
            graph2D.fillOval(screenPoints[i], screenPoints[i + 1], 10, 10);
            i += 2;
        }
    }

    public void drawStars(ObservedSky sky, StereographicProjection projection, Transform planeToCanva) {
        int coordNumber = sky.starPointsRefs().length;
        double[] screenPoints = new double[coordNumber];
        planeToCanva.transform2DPoints(sky.starPointsRefs(), 0, screenPoints, 0, coordNumber/2);

        int i = 0;
        for(Star s: sky.stars()) {
            graph2D.setFill(BlackBodyColor.colorForTemperature(s.colorTemperature()));
            graph2D.fillOval(screenPoints[i], screenPoints[i + 1], s.magnitude()/3, s.magnitude()/3); // TODO Factor & shift
            i += 2;
        }
    }
}
