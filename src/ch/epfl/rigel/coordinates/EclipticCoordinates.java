package ch.epfl.rigel.coordinates;

import static ch.epfl.rigel.Preconditions.checkArgument;

import java.util.Locale;

import ch.epfl.rigel.math.Angle;

/**
 * 
 * @author Alexis FAVRE (310552)
 *
 */
public final class EclipticCoordinates extends SphericalCoordinates {

    private EclipticCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }
    
    /**
     * 
     * @return
     * (double) longitude in radians
     */
    public double lon() { return super.lon();}
    
    /**
     * 
     * @return
     * (double) latitude in radians
     */
    public double lat() {return super.lat();}
    
    /**
     * 
     * @return
     * (double) longitude in degrees
     */
    public double lonDeg() {
        return Angle.toDeg(lon());
    }
    
    /**
     * 
     * @return
     * (double) latitude in degrees
     */
    public double latDeg() {
        return Angle.toDeg(lat());
    }
    
    /**
     * to create new EclipticCoordinates
     * @param lon
     * (double) longitude in radians (must be in [0,2Pi[)
     * @param lat
     * (double) latitude in radians (must be in [-Pi/2,Pi/2])
     * @return
     * new EclipticCoordinates
     */
    public static EclipticCoordinates of(double lon, double lat) {
        checkArgument(0<=lon && lon < Angle.TAU);
        checkArgument(-Angle.TAU/4<= lat && lat<=Angle.TAU/4);
        return new EclipticCoordinates(lon, lat);
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(λ=%.4f°, β=%.4f°)", lonDeg(), latDeg());
    }

}
