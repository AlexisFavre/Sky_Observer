package ch.epfl.rigel.gui;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.ZonedDateTime;

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
    private Long nanoOfBegin;
    private ZonedDateTime initial;

    /**
     *
     * @param dtb the {@code DateTimeBean} that will be periodically updated
     */
    public TimeAnimator(DateTimeBean dtb) {
        this.dtb = dtb;
        accelerator = new SimpleObjectProperty<>(null);
        running = new SimpleBooleanProperty(false);
        nanoOfBegin = null;
    }

    /**
     *
     * @param accelerator the accelerator to set
     */
    public void setAccelerator(TimeAccelerator accelerator) {
        this.accelerator.setValue(accelerator);
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
     * automatically called 60 time by seconds to make progress the animation
     * @param now the number of nanoseconds passed since the beginning
     * of the animation
     */
    @Override
    public void handle(long now) {
        System.out.println("handle");
        if (nanoOfBegin == null) {
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
        nanoOfBegin = null;
    }
    
    /**
     *
     * @return running as a {@code ReadOnlyBooleanProperty}
     * to ban modifications from outside the class
     */
    public ReadOnlyBooleanProperty runningProperty() {
        return running;
    }
}
