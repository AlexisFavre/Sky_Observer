package ch.epfl.rigel.coordinates;

import java.time.ZonedDateTime;
import java.util.function.Function;

import ch.epfl.rigel.astronomy.Epoch;

public final class EquatorialToHorizontalConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {

    private double sinOfEpsilon;
    private double cosOfEpsilon;
    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) { // TODO Immuable?
        double T = Epoch.J2000.julianCenturiesUntil(when);
        double epsilon = 0.00181 * T*T*T - 0.0006 * T*T - 46.815 * T + 0;// TODO Replace 0 by right formula
        sinOfEpsilon = Math.sin(epsilon);
        cosOfEpsilon = Math.cos(epsilon);
    };


    // TODO Hascode & equals not found

    @Override
    public EquatorialCoordinates apply(EclipticCoordinates eclipticCoordinates) {
        double lambda = eclipticCoordinates.lon();
        double beta = eclipticCoordinates.lat();
        double alpha = Math.atan((Math.sin(lambda) * cosOfEpsilon - Math.tan(beta) * sinOfEpsilon) / Math.cos(lambda));
        double gamma = Math.asin(Math.sin(beta) * cosOfEpsilon + Math.cos(beta) * sinOfEpsilon * Math.sin(lambda));
        return EquatorialCoordinates.of(alpha, gamma);
    }
}
