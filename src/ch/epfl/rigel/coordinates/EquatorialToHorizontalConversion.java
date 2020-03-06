package ch.epfl.rigel.coordinates;

import java.time.ZonedDateTime;
import java.util.function.Function;

import ch.epfl.rigel.astronomy.SiderealTime;

public final class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    private final double cosOfPhi;
    private final double sinOfPhi;
    private final double Sl;
    
    /**
     * 
     * @param when (ZonedDateTime) of the observation
     * @param where (GeographicCoordinates) of the observer
     */
    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        cosOfPhi = Math.cos(where.lat());
        sinOfPhi = Math.sin(where.lat());
        Sl = SiderealTime.local(when, where);
    }

    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equatorialCoordinates) {
        double H = Sl - equatorialCoordinates.ra(); //hourly angle
        double sinGamma = Math.sin(equatorialCoordinates.dec());
        double cosGamma = Math.cos(equatorialCoordinates.dec());
        
        double alt = Math.asin(sinGamma*sinOfPhi + cosGamma*cosOfPhi*Math.cos(H));
        double az = Math.atan2(-cosGamma*cosOfPhi*Math.sin(H) , sinGamma - sinOfPhi*Math.sin(alt));
        
        return HorizontalCoordinates.of(az, alt);
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
