package ch.epfl.rigel.gui;

import ch.epfl.rigel.math.Angle;

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

    /**
     * Gives the simulation time (that elapses faster than the real time)
     * corresponding to the elapsed nanosecond from the beginning of the simulation
     *
     * @param initial the real time of the simulation beginning
     * @param elapsed the nanosecond elapsed since the beginning of the simulation
     * @return the computed time used for simulation
     */
    ZonedDateTime adjust(ZonedDateTime initial, long elapsed);

    static TimeAccelerator continuous(int accelFactor) {
        return (initial, elapsed) -> initial.plusNanos(accelFactor*elapsed);
    }

   static TimeAccelerator discrete(int frequence, ZonedDateTime step) {
        return (initial, elapsed) -> {
            Long factor = (long) Math.floor(elapsed*frequence);
            ZonedDateTime nv = initial
                    .plusSeconds(step.getSecond()*factor)
                    .plusMinutes(step.getMinute()*factor)
                    .plusHours(step.getHour()*factor)
                    .plusDays(step.getDayOfMonth()*factor)
                    .plusMonths(step.getMonthValue()*factor)
                    .plusYears(step.getYear()*factor);
            return nv;
        };
    }

    /*private static long toNano(ZonedDateTime converted) {
        converted.getYear()*365;
    }*/
}
