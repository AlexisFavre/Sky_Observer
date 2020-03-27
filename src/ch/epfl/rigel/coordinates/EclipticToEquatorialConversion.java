package ch.epfl.rigel.coordinates;

import java.time.ZonedDateTime;
import java.util.function.Function;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
import ch.epfl.rigel.math.RightOpenInterval;

/**
 * Enables the transformation of the EclipticCoordinates to EquatorialCoordinates
 * at a precise {@code ZonedDateTime}
 *
 * @author Augustin ALLARD (299918)
 */
public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {

    private final double sinOfEpsilon;
    private final double cosOfEpsilon;
    
    /**
     *
     * @param when {@code ZonedDateTime} at which the conversion and observation is made
     */
    public EclipticToEquatorialConversion(ZonedDateTime when) {
        double jCentSinceJ2000 = Epoch.J2000.julianCenturiesUntil(when);
        double epsilon = Angle.ofDMS(23, 26, 21.45
                + Polynomial.of(0.00181, -0.0006, -46.815, 0).at(jCentSinceJ2000));
        sinOfEpsilon = Math.sin(epsilon);
        cosOfEpsilon = Math.cos(epsilon);
    };

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public EquatorialCoordinates apply(EclipticCoordinates eclipticCoordinates) {
        double lambda = eclipticCoordinates.lon();
        double beta = eclipticCoordinates.lat();
        double alpha = Math.atan2((Math.sin(lambda) * cosOfEpsilon  -  Math.tan(beta) * sinOfEpsilon) , Math.cos(lambda));
        double gamma = Math.asin(Math.sin(beta) * cosOfEpsilon  +  Math.cos(beta) * sinOfEpsilon * Math.sin(lambda));
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
    public final boolean equals(Object interval) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
