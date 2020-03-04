package ch.epfl.rigel.coordinates;

import java.time.ZonedDateTime;
import java.util.function.Function;

import static ch.epfl.rigel.coordinates.EclipticToEquatorialConversion.epsilon;;

public final class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    private final double sinOfEpsilon;
    private final double cosOfEpsilon;
    
    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        double epsilon = epsilon(when); //used function of EclipticToEquatorialConversion Class
        sinOfEpsilon = Math.sin(epsilon);
        cosOfEpsilon = Math.cos(epsilon);
    };

    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equatorialCoordinates) {
        double H = equatorialCoordinates.ra();
        double phi = equatorialCoordinates.dec();
        double gamma = Math.asin(Math.sin(H) * cosOfEpsilon  +  Math.cos(H) * sinOfEpsilon * Math.sin(phi));
        
        double alt = Math.asin(Math.sin(gamma)*Math.sin(phi) + Math.cos(gamma)*Math.cos(phi)*Math.cos(H));
        double az = Math.atan(-Math.cos(gamma)*Math.cos(phi)*Math.sin(H)/(Math.sin(gamma)-Math.sin(phi)*Math.sin(alt)));
        return HorizontalCoordinates.of(az, alt);
        
        // λ = az = A = long = ra = phi
        // β = alt = h = lat = dec = H
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
