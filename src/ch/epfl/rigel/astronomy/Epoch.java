package ch.epfl.rigel.astronomy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Defines specific time of reference
 * from which we can measure the time between an epoch and another given time
 *
 * @author Augustin ALLARD (299918)
 */
public enum Epoch {

    J2000(ZonedDateTime.of(
            LocalDate.of(2000, Month.JANUARY, 1),
            LocalTime.of(12, 0),
            ZoneOffset.UTC)
    ),
    J2010(ZonedDateTime.of(
            LocalDate.of(2010, Month.JANUARY, 1).minusDays(1),
            LocalTime.of(0, 0),
            ZoneOffset.UTC)
    );

    private final static double MILI_SEC_PER_DAY = 8.64e+7;
    private final static double NUMBER_OF_DAYS_PER_JULIAN_CENTURY = 36525;
    private final ZonedDateTime date;

    Epoch(ZonedDateTime date) {
        this.date = date;
    }

    /**
     * This calculates the time gap in days between {@code this} (the epoch date) and the given date
     *
     * @param when the date from which we want to measure the day gap with {@code this}
     * @return exact days from the Epoch {@code this} until the given date (negative if anterior)
     */
    public double daysUntil(ZonedDateTime when) {
        return date.until(when, ChronoUnit.MILLIS) / MILI_SEC_PER_DAY;
    };

    /**
     * This calculates the time gap in julianCenturies between {@code this} (the epoch date) and the given date
     *
     * @param when the date from which we want to measure the julianCenturies gap with {@code this}
     * @return exact julianCenturies from the Epoch {@code this} until the given date (negative if anterior)
     */
    public double julianCenturiesUntil(ZonedDateTime when) {
        return date.until(when, ChronoUnit.MILLIS) / (MILI_SEC_PER_DAY * NUMBER_OF_DAYS_PER_JULIAN_CENTURY);
    };
}
