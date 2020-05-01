package ch.epfl.rigel.gui;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Used to create discrete or continuous accelerator {@code TimeAccelerator} via static methods
 * that can compute from two different manners the simulation time used for simulation
 * (elapses faster than the real time) via the adjust method
 *
 * @author Augustin ALLARD (299918)
 */
@FunctionalInterface
public interface TimeAccelerator {
    
    final static double FREQUENCE_OF_REFRESHING_OF_JAVAFX = 1e-9; // all nanoseconds

    /**
     * Gives the simulation time (that elapses faster than the real time)
     * corresponding to the elapsed nanosecond from the beginning of the simulation
     *
     * @param initial the real time of the simulation beginning
     * @param elapsed the nanosecond elapsed since the beginning of the simulation
     * @return the computed time used for simulation
     */
    ZonedDateTime adjust(ZonedDateTime initial, long elapsed);

    /**
     * Give a new continuous accelerator with the given factor
     *
     * @param accelFactor the factor with which the simulation time will be accelerated
     *                    (1 correspond to the real time)
     * @return the created accelerator
     */
    static TimeAccelerator continuous(int accelFactor) {
        return (initial, elapsed) -> initial.plusNanos(accelFactor*elapsed);
    }

    /**
     * Give a new discrete accelerator with the given update frequency and step of time
     *
     * @param frequency the number of time that the simulation time will be updated in one second
     * @param step the time step that will be added to the simulation time at each update
     * @return the created accelerator
     */
    static TimeAccelerator discrete(int frequency, Duration step) {
        return (initial, elapsed) -> {
            long factor = (long)Math.floor(elapsed*frequency*FREQUENCE_OF_REFRESHING_OF_JAVAFX);
            return initial
                    .plusNanos(step.getNano()*factor)
                    .plusSeconds(step.getSeconds()*factor);
        };
    }
}
