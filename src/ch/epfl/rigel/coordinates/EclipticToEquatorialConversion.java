package ch.epfl.rigel.coordinates;

import java.time.ZonedDateTime;
import java.util.function.Function;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.RightOpenInterval;

public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {

    private final double sinOfEpsilon;
    private final double cosOfEpsilon;
    
    public EclipticToEquatorialConversion(ZonedDateTime when) {
        double epsilon = epsilon(when);
        sinOfEpsilon = Math.sin(epsilon);
        cosOfEpsilon = Math.cos(epsilon);
    };
    
    // return Elliptic obliqueness in radians 
    protected static double epsilon(ZonedDateTime when) {
        double T = Epoch.J2000.julianCenturiesUntil(when);
        return Angle.toDeg(Angle.ofDMS(23, 26, 21.45+0.00181*T*T*T - 0.0006*T*T - 46.815*T)) ;
    }

    @Override
    public EquatorialCoordinates apply(EclipticCoordinates equatorialCoordinates) {
        double lambda = equatorialCoordinates.lon();
        double beta = equatorialCoordinates.lat();
        double alpha = Math.atan2((Math.sin(lambda) * cosOfEpsilon  -  Math.tan(beta) * sinOfEpsilon) , Math.cos(lambda));
        double gamma = Math.asin(Math.sin(beta) * cosOfEpsilon  +  Math.cos(beta) * sinOfEpsilon * Math.sin(lambda));
        return EquatorialCoordinates.of(RightOpenInterval.of(0, Angle.TAU).reduce(alpha), RightOpenInterval.of(-Angle.TAU/4, Angle.TAU/4).reduce(gamma));
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
