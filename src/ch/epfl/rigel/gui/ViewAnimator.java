package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * to make animation of the sky
 * @author Augustin ALLARD (299918)
 */
public class ViewAnimator extends AnimationTimer {

    private final static double HANDLES_PER_RISING = 40;
    private final static double MIN_DISTANCE_TO_CONSIDER_DESTIANTION_REACHED = 1e-7;
    private final static RightOpenInterval CINTER_0TO360 = RightOpenInterval.of(0, 360);
    private final static int HALF_MAX_AZ = 180;
    private final static int MAX_AZ      = 360;

    
    private final ViewingParametersBean vpb;
    private final SimpleBooleanProperty running;
    
    private Double azDegDest;
    private Double altDegDest;
    private double azDegStep;
    private double altDegStep;

    /**
     * @param vpb the {@code ViewingParametersBean} that will be periodically updated
     */
    public ViewAnimator(ViewingParametersBean vpb) {
        this.vpb = vpb;
        running  = new SimpleBooleanProperty(false);
    }

    public void setDestination(double az, double alt) {
        azDegDest  = az;
        altDegDest = alt;
        double azDist = azDegDest - vpb.getCenter().azDeg();
        if(azDist > HALF_MAX_AZ)
            azDist = -MAX_AZ + azDist;
        else if (azDist < -HALF_MAX_AZ)
            azDist = MAX_AZ - Math.abs(azDist);
        azDegStep  = azDist / HANDLES_PER_RISING;
        altDegStep = (altDegDest - vpb.getCenter().altDeg()) / HANDLES_PER_RISING;
    }
    
    private boolean destinationIsReached() {
        return Math.abs(azDegDest  - vpb.getCenter().azDeg())  < MIN_DISTANCE_TO_CONSIDER_DESTIANTION_REACHED
            && Math.abs(altDegDest - vpb.getCenter().altDeg()) < MIN_DISTANCE_TO_CONSIDER_DESTIANTION_REACHED;
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

    /**
     * automatically called around 60 time by seconds to make progress the animation
     * @param now the number of nanoseconds passed since the beginning
     * of the animation
     */
    @Override
    public void handle(long now) {
        vpb.setCenter(HorizontalCoordinates.ofDeg(CINTER_0TO360.reduce(vpb.getCenter().azDeg() + azDegStep),
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
