package ch.epfl.rigel.gui;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * modify periodically the observation moment with an 
 * accelerator to animate the sky
 * @author Alexis FAVRE (310552)
 */
public final class TimeAnimator extends AnimationTimer {
    
    private DateTimeBean dtb;
    private ObjectProperty<TimeAccelerator> accelerator;
    private SimpleBooleanProperty running; //must be modifiable only from the inside of this

    /**
     * @param dtb
     */
    public TimeAnimator(DateTimeBean dtb) {
        this.dtb = dtb;
    }
    
    @Override
    public void start() {
        running.setValue(true);
        super.start();
    }

    /**
     * automatically called 60 time by seconds to make progress the animation
     * @param now the number of nanoseconds passed since the beginning
     * of the animation
     */
    @Override
    public void handle(long now) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void stop() {
        super.stop();
        running.setValue(false);
    }
    
    /**
     * @return running as a {@code ReadOnlyBooleanProperty} to ban modicications
     * of it from outside of {@code this}
     */
    public ReadOnlyBooleanProperty runningProperty() {
        return (ReadOnlyBooleanProperty) running;
    }

}
