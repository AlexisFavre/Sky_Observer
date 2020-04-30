package ch.epfl.rigel.astronomy;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

/**
 * Non instantiable class containing only {@code static} methods
 * This enables sidereal time computation at UTC and locally (coordinates)
 *
 * @author Augustin ALLARD (299918)
 */
public final class SiderealTime {
    
    private final static Polynomial POLYNOMIAL = Polynomial.of(0.000025862, 2400.051336, 6.697374558);
    private final static double MILI_SECONDS_PER_HOUR = 3.6e+6;
    private final static double CONST_FOR_S1 = 1.002737909;
    
    private SiderealTime() {}
    
    /**
     * This calculates the sidereal time at the greenwich time zone
     *
     * @param when the actual date from which we want the sidereal time
     * @return the sidereal time of when at the greenwich time zone in radians between [0, 2pi[
     */
    public static double greenwich(ZonedDateTime when) {
        ZonedDateTime greenwichWhen = when.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime truncatedGreenwichWhen = greenwichWhen.truncatedTo(ChronoUnit.DAYS);
        double jCentSinceJ2000 = Epoch.J2000.julianCenturiesUntil(truncatedGreenwichWhen);
        double hoursSinceDayStart = truncatedGreenwichWhen.until(greenwichWhen, ChronoUnit.MILLIS) / MILI_SECONDS_PER_HOUR;
        double S0 = POLYNOMIAL.at(jCentSinceJ2000) ;
        double S1 = CONST_FOR_S1 * hoursSinceDayStart;
        double Sg = Angle.ofHr(S0 + S1);
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
        return Angle.normalizePositive(greenwich(when) + where.lon());
    };
}
