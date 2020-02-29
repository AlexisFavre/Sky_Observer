package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Augustin ALLARD (299918)
 *
 */
public final class SiderealTime {

    /**
     * This calculates the sidereal time at the greenwich time zone
     *
     * @param when the actual date from which we want the sidereal time
     * @return (double)
     *  In radians between [0, t[- the sidereal time of when at the greenwich time zone
     */
    public static double greenwich(ZonedDateTime when) {
        ZonedDateTime greenwichWhen = when.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime greenwichWhenDayStart = greenwichWhen.truncatedTo(ChronoUnit.HOURS);
        double millisInHours = 86164100;
        double T = Epoch.J2000.julianCenturiesUntil(greenwichWhen);
        double t = greenwichWhenDayStart.until(greenwichWhen, ChronoUnit.MILLIS) / millisInHours;
        double S0 = 0.000025862 * T * T  + 2400.051336 * T + 6.697374558;
        double S1 = 1.002737909 * t;
        return 0; // TODO in RADIANS
    };

    /**
     * This calculates the local sidereal time at the given coordinates
     *
     * @param when the actual date from which we want the sidereal time
     * @param where the local coordinates of where the sidereal time will be computed
     * @return (double)
     *  In radians between [0, t[- the local sidereal time
     */
    public static double local(ZonedDateTime when, GeographicCoordinates where) {
        return greenwich(when) + where.lat(); // TODO in RADIANS
    };
}
