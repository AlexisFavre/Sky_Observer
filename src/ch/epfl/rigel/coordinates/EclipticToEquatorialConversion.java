package ch.epfl.rigel.coordinates;
import java.time.ZonedDateTime;
import java.util.function.Function;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
import ch.epfl.rigel.math.RightOpenInterval;

/**
 * Function that enables the transformation of the EclipticCoordinates to EquatorialCoordinates
 * at a precise {@code ZonedDateTime}
 *
 * @author Augustin ALLARD (299918)
 */
public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {

    private final double sinOfEpsilon;
    private final double cosOfEpsilon;
    private final static Polynomial POLYNOMIAL = Polynomial.of(0.00181, -0.0006, -46.815, 0);
    private final static RightOpenInterval INTER_0TO60 = RightOpenInterval.of(0, 60);
    
    /**
     * @param when {@code ZonedDateTime} at which the conversion and observation is made
     */
    public EclipticToEquatorialConversion(ZonedDateTime when) {
        double jCentSinceJ2000 = Epoch.J2000.julianCenturiesUntil(when);
        double epsilon = Angle.ofDMS(23, 26, 
               INTER_0TO60.reduce(21.45 + POLYNOMIAL.at(jCentSinceJ2000)));
        sinOfEpsilon = Math.sin(epsilon);
        cosOfEpsilon = Math.cos(epsilon);
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public EquatorialCoordinates apply(EclipticCoordinates eclipticCoordinates) {
        double lambda = eclipticCoordinates.lon();
        double beta = eclipticCoordinates.lat();
        double sinLambda = Math.sin(lambda);
        double alpha = Math.atan2((sinLambda * cosOfEpsilon  -  Math.tan(beta) * sinOfEpsilon) , Math.cos(lambda));
        double gamma = Math.asin(Math.sin(beta) * cosOfEpsilon  +  Math.cos(beta) * sinOfEpsilon * sinLambda);
        return EquatorialCoordinates.of(Angle.normalizePositive(alpha), gamma);
    }

    /**
     * Always throw exception
     * {@code conversion.hashCode()} is forbidden
     *
     * @throws UnsupportedOperationException in all conditions
     */
    @Override
    public final int hashCode() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throw exception
     * {@code conversion.equals()} is forbidden
     *
     * @throws UnsupportedOperationException in all conditions
     */
    @Override
    public final boolean equals(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
