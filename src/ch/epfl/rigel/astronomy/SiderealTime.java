package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
import ch.epfl.rigel.math.RightOpenInterval;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Non instantiable class containing only {@code static} methods
 * This enables sidereal time computation at UTC and locally (coordinates)
 *
 * @author Augustin ALLARD (299918)
 */
public final class SiderealTime {

    /**
     * This calculates the sidereal time at the greenwich time zone
     *
     * @param when the actual date from which we want the sidereal time
     * @return the sidereal time of when at the greenwich time zone in radians between [0, 2pi[
     */
    public static double greenwich(ZonedDateTime when) {
        double millisInHours =  3.6e+6;
        ZonedDateTime greenwichWhen = when.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime truncatedGreenwichWhen = greenwichWhen.truncatedTo(ChronoUnit.DAYS);
        double jCentSinceJ2000 = Epoch.J2000.julianCenturiesUntil(truncatedGreenwichWhen);
        double hoursSinceDayStart = truncatedGreenwichWhen.until(greenwichWhen, ChronoUnit.MILLIS) / millisInHours;
        double S0 = Polynomial.of(0.000025862, 2400.051336, 6.697374558).at(jCentSinceJ2000) ;
        double S1 = 1.002737909 * hoursSinceDayStart;
        double Sg = Angle.ofHr(RightOpenInterval.of(0, 24).reduce(S0 + S1));
        return Angle.normalizePositive(Sg);
    };

    /**
     * This calculates the local sidereal time at the given coordinates
     *
     * @param when the actual date from which we want the sidereal time
     * @param where the local coordinates of where the sidereal time will be computed
     * @return the local sidereal time in radians between [0, 2pi[
     */
    public static double local(ZonedDateTime when, GeographicCoordinates where) {
        return RightOpenInterval.of(0, Angle.TAU).reduce(greenwich(when) + where.lon());
    };
}
