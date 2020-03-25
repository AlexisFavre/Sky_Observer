package ch.epfl.rigel.coordinates;

import java.time.ZonedDateTime;
import java.util.function.Function;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.RightOpenInterval;
/**
 * this class enable the transformation of the  EquatorialCoordinates to HorizontalCoordinates
 * at a precise ZonedDateTime and GeographicCoordinates
 * @author Alexis FAVRE (310552)
 */
public final class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    private final double cosOfPhi;
    private final double sinOfPhi;
    private final double Sl;
    
    /**
     * initialise the parameters need for the conversion
     * @param when (ZonedDateTime) of the observation
     * @param where (GeographicCoordinates) of the observer
     */
    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        cosOfPhi = Math.cos(where.lat());
        sinOfPhi = Math.sin(where.lat());
        Sl = SiderealTime.local(when, where);
    }

    @Override
    /**
     * @return HorizontalCoordinates corresponding to these EquatorialCoordinates
     * for a precise ZonedDateTime and GeographicCoordinates @see EclipticToEquatorialConversion
     */
    public HorizontalCoordinates apply(EquatorialCoordinates equatorialCoordinates) {
        double H = Sl - equatorialCoordinates.ra(); //hourly angle
        double sinGamma = Math.sin(equatorialCoordinates.dec());
        double cosGamma = Math.cos(equatorialCoordinates.dec());
        
        double alt = Math.asin(sinGamma*sinOfPhi + cosGamma*cosOfPhi*Math.cos(H));
        double az = Math.atan2(-cosGamma*cosOfPhi*Math.sin(H) , sinGamma - sinOfPhi*Math.sin(alt));
        
        return HorizontalCoordinates.of(RightOpenInterval.of(0, Angle.TAU).reduce(az), RightOpenInterval.of(-Angle.TAU/4, Angle.TAU/4).reduce(alt));
    }
    
    @Override
    /** always throws UnsupportedOperationException */
    public final int hashCode() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override 
    /** always throws UnsupportedOperationException */
    public final boolean equals(Object interval) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
