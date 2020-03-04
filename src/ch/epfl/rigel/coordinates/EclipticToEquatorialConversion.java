package ch.epfl.rigel.coordinates;

import java.time.ZonedDateTime;
import java.util.function.Function;

import ch.epfl.rigel.astronomy.Epoch;

public final class EclipticToEquatorialConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    private double sinOfEpsilon;
    private double cosOfEpsilon; // TODO Be sure about the value that should be pre-computed
    public EclipticToEquatorialConversion(ZonedDateTime when) { // TODO Immutable?
        double epsilon = epsilon(when);
        sinOfEpsilon = Math.sin(epsilon);
        cosOfEpsilon = Math.cos(epsilon);
    };


    // TODO Hashcode & equals not found

    public static double epsilon(ZonedDateTime when) {
        double T = Epoch.J2000.julianCenturiesUntil(when);
        return 0.00181*3600 * T*T*T - 0.0006*3600 * T*T - 46.815*3600 * T + 23 + (26 + 21.45/60)/60;
    }

    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equatorialCoordinates) {
        double lambda = equatorialCoordinates.lon();
        double beta = equatorialCoordinates.lat();
        double alpha = Math.atan((Math.sin(lambda) * cosOfEpsilon - Math.tan(beta) * sinOfEpsilon) / Math.cos(lambda));
        double gamma = Math.asin(Math.sin(beta) * cosOfEpsilon + Math.cos(beta) * sinOfEpsilon * Math.sin(lambda));
        return HorizontalCoordinates.of(alpha, gamma);
    }

    /*private double dmsToDegrees(double degrees, double minutes, double seconds) {
        return (Math.abs(minutes) + Math.abs(seconds/60))/60 + Math.abs(degrees);
    }*/
}
