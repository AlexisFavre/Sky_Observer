package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * @author Augustin ALLARD (299918)
 *
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


    private ZonedDateTime date;
    Epoch(ZonedDateTime date) {
        this.date = date;
    }

    /**
     * This calculates the time gap in days between this (the epoch date) and the given date
     *
     * @param when the date from which we want to measure the day gap with (this)
     * @return (double)
     *  exact days from the Epoch(this) until the given date (negative if anterior)
     */
    public double daysUntil(ZonedDateTime when) {
        double millisInDays = 8.64e+7;
        return date.until(when, ChronoUnit.MILLIS) / millisInDays;
    };

    /**
     * This calculates the time gap in julianCenturies between this (the epoch date) and the given date
     *
     * @param when the date from which we want to measure the julianCenturies gap with (this)
     * @return (double)
     *  exact julianCenturies from the Epoch(this) until the given date (negative if anterior)
     */
    public double julianCenturiesUntil(ZonedDateTime when) {
        double millisInDays = 8.64e+7;
        double daysInJCenturies = 3.15576e+12;
        return date.until(when, ChronoUnit.MILLIS) / (millisInDays * daysInJCenturies);
    };
}
