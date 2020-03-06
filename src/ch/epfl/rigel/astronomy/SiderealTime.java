package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.RightOpenInterval;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Augustin ALLARD (299918)
 * @author Alexis FAVRE (310552)
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
        ZonedDateTime greenwichWhenDayStart = greenwichWhen.truncatedTo(ChronoUnit.DAYS);
        double millisInHours = 3590170;
        double T = Epoch.J2000.julianCenturiesUntil(greenwichWhenDayStart);
        double t = greenwichWhenDayStart.until(greenwichWhen, ChronoUnit.MILLIS) / millisInHours;
        double S0 = 0.000025862 * T * T  + 2400.051336 * T + 6.697374558;
        double S1 = 1.002737909 * t;
        double Sg = Angle.ofHr(RightOpenInterval.of(0, 24).reduce(S0 + S1));
        return RightOpenInterval.of(0, Angle.TAU).reduce(Sg);
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
        return RightOpenInterval.of(0, Angle.TAU).reduce(greenwich(when) + where.lon());
    };
}
