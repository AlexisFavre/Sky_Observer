package ch.epfl.rigel.coordinates;

import java.time.ZonedDateTime;
import java.util.function.Function;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
import ch.epfl.rigel.math.RightOpenInterval;

/**
 * @author Augustin ALLARD (299918)
 */
public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {

    private final double sinOfEpsilon;
    private final double cosOfEpsilon;
    
    public EclipticToEquatorialConversion(ZonedDateTime when) {
        double epsilon = epsilon(when);
        sinOfEpsilon = Math.sin(epsilon);
        cosOfEpsilon = Math.cos(epsilon);
    };
    
    // return Elliptic obliqueness in radians 
    private double epsilon(ZonedDateTime when) {
        double T = Epoch.J2000.julianCenturiesUntil(when);
        return Angle.ofDMS(23, 26, 21.45 + Polynomial.of(0.00181, -0.0006, -46.815, 0).at(T));
    }

    @Override
    public EquatorialCoordinates apply(EclipticCoordinates eclipticCoordinates) {
        double lambda = eclipticCoordinates.lon();
        double beta = eclipticCoordinates.lat();
        double alpha = Math.atan2((Math.sin(lambda) * cosOfEpsilon  -  Math.tan(beta) * sinOfEpsilon) , Math.cos(lambda));
        double gamma = Math.asin(Math.sin(beta) * cosOfEpsilon  +  Math.cos(beta) * sinOfEpsilon * Math.sin(lambda));
        return EquatorialCoordinates.of(Angle.normalizePositive(alpha), gamma);
    }
    
    /**
     * always throws UnsupportedOperationException
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }
    
    
    /**
     * always throws UnsupportedOperationException
     */
    @Override 
    public final boolean equals(Object interval) {

        throw new UnsupportedOperationException();
    }
}
