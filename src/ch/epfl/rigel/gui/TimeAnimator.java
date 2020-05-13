package ch.epfl.rigel.gui;

import java.time.ZonedDateTime;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Used to update periodically (60 times/second) the given {@code DateTimeBean} at the creation when running
 * The updated time is computed by a {@code TimeAccelerator}
 *
 * @author Alexis FAVRE (310552)
 * @see DateTimeBean
 */
public final class TimeAnimator extends AnimationTimer {
    
    private DateTimeBean dtb;
    private ObjectProperty<TimeAccelerator> accelerator;
    
    private SimpleBooleanProperty running;
    private ZonedDateTime initial;
    private Long nanoOfBegin;
    private final static long VAL_WHEN_ACC_STOPED = -1L;

    /**
     * @param dtb the {@code DateTimeBean} that will be periodically updated
     */
    public TimeAnimator(DateTimeBean dtb) {
        this.dtb = dtb;
        accelerator = new SimpleObjectProperty<>(null);
        running = new SimpleBooleanProperty(false);
        nanoOfBegin = VAL_WHEN_ACC_STOPED;
    }

    /**
     * Set the running to {@code true} and start the {@code AnimationTimer}
     */
    @Override
    public void start() {
        running.setValue(true);
        super.start();
    }

    /**
     * automatically called around 60 time by seconds to make progress the animation
     * @param now the number of nanoseconds passed since the beginning
     * of the animation
     */
    @Override
    public void handle(long now) {
        if (nanoOfBegin == VAL_WHEN_ACC_STOPED) {
            nanoOfBegin = now;
            initial = dtb.getZonedDateTime();
        }
        dtb.setZonedDateTime(accelerator.getValue().adjust(initial, now - nanoOfBegin));
    }

    /**
     * Set the running to {@code false} and stop the {@code AnimationTimer}
     */
    @Override
    public void stop() {
        super.stop();
        running.setValue(false);

        // to stop the time optional
        initial = dtb.getZonedDateTime();
        nanoOfBegin = VAL_WHEN_ACC_STOPED;
    }
    
    /**
     * @return running as a {@code ReadOnlyBooleanProperty}
     * to ban modifications from outside the class
     */
    public ReadOnlyBooleanProperty runningProperty() {
        return running;
    }
    
    public ObjectProperty<TimeAccelerator> acceleratorProperty(){
        return accelerator;
    }
}
