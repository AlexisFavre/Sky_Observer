package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
// TODO Rise between north edge without making tour
public class ViewAnimator extends AnimationTimer {

    private final static double HANDLES_PER_RISING = 40;
    private final static RightOpenInterval CINTER_0TO360 = RightOpenInterval.of(0, 360);
    private ViewingParametersBean vpb;
    private SimpleBooleanProperty running;
    private Double azDegDest = null;
    private Double altDegDest = null;
    private double azDegStep;
    private double altDegStep;

    /**
     * @param vpb the {@code ViewingParametersBean} that will be periodically updated
     */
    public ViewAnimator(ViewingParametersBean vpb) {
        this.vpb = vpb;
        running = new SimpleBooleanProperty(false);
    }

    public void setDestination(double az, double alt) {
        azDegDest = az;
        altDegDest = alt;
        // TODO Check border (360/0 pile)
        double azDist = azDegDest - vpb.getCenter().azDeg();
        if(azDist > 180)
            azDist = -360 + azDist;
        else if (azDist < -180)
            azDist = 360 - Math.abs(azDist);
        azDegStep = azDist / HANDLES_PER_RISING;
        altDegStep = (altDegDest - vpb.getCenter().altDeg()) / HANDLES_PER_RISING;
    }

    /**
     * Set the running to {@code true} and start the animation if the destination was set
     */
    @Override
    public void start() {
        if(azDegDest != null && altDegDest != null) {
            running.setValue(true);
            super.start();
        }
    }

    private boolean destinationIsReached() {
        return Math.abs(azDegDest - vpb.getCenter().azDeg()) < 1.0e-7
                && Math.abs(altDegDest - vpb.getCenter().altDeg()) < 1.0e-7;
    }

    /**
     * automatically called around 60 time by seconds to make progress the animation
     * @param now the number of nanoseconds passed since the beginning
     * of the animation
     */
    @Override
    public void handle(long now) {
        vpb.setCenter(HorizontalCoordinates.ofDeg(CINTER_0TO360.reduce(vpb.getCenter().azDeg() + azDegStep),
                vpb.getCenter().altDeg() + altDegStep)); //TODO normalizer a [-90,90]
        if(destinationIsReached())
            stop();
    }

    /**
     * Set the running to {@code false} and stop the {@code AnimationTimer}
     */
    @Override
    public void stop() {
        super.stop();
        running.setValue(false);
        azDegDest = null;
        altDegDest = null;
    }

    /**
     * @return running as a {@code ReadOnlyBooleanProperty}
     * to ban modifications from outside the class
     */
    public ReadOnlyBooleanProperty runningProperty() {
        return running;
    }
}
