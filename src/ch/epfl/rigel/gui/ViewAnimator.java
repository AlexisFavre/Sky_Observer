package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.ZonedDateTime;
// TODO Rise between north edge without making tour
public class ViewAnimator extends AnimationTimer {

    private final static double HANDLES_PER_RISING = 40;
    private final static RightOpenInterval CINTER_M180TO180 = RightOpenInterval.of(-180, 180);
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
        azDegStep = CINTER_M180TO180.reduce(azDegDest - vpb.getCenter().azDeg()) / HANDLES_PER_RISING;
        altDegStep = CINTER_M180TO180.reduce(altDegDest - vpb.getCenter().altDeg()) / HANDLES_PER_RISING;
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
        vpb.setCenter(HorizontalCoordinates.ofDeg(vpb.getCenter().azDeg() + azDegStep,
                vpb.getCenter().altDeg() + altDegStep));
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
