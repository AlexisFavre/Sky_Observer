package ch.epfl.rigel.coordinates;

import java.time.ZonedDateTime;
import java.util.function.Function;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.RightOpenInterval;
/**
 *  Enables the transformation of the EquatorialCoordinates to HorizontalCoordinates
 *  at a precise {@code ZonedDateTime} and place on the earth {@code GeographicCoordinates}
 *
 * @author Alexis FAVRE (310552)
 * @author Augustin ALLARD (299918)
 */
public final class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    private final double cosOfPhi;
    private final double sinOfPhi;
    private final double localSiderealTime;
    
    /**
     * initialise the parameters need for the conversion
     * @param when (ZonedDateTime) of the observation
     * @param where (GeographicCoordinates) of the observer
     */
    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        cosOfPhi = Math.cos(where.lat());
        sinOfPhi = Math.sin(where.lat());
        localSiderealTime = SiderealTime.local(when, where);
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equatorialCoordinates) {
        double H = localSiderealTime - equatorialCoordinates.ra(); //hourly angle
        double sinGamma = Math.sin(equatorialCoordinates.dec());
        double cosGamma = Math.cos(equatorialCoordinates.dec());
        
        double alt = Math.asin(sinGamma*sinOfPhi + cosGamma*cosOfPhi*Math.cos(H));
        double az = Math.atan2(-cosGamma*cosOfPhi*Math.sin(H) , sinGamma - sinOfPhi*Math.sin(alt));
        
        return HorizontalCoordinates.of(RightOpenInterval.of(0, Angle.TAU).reduce(az),
                RightOpenInterval.of(-Angle.TAU/4, Angle.TAU/4).reduce(alt));
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
