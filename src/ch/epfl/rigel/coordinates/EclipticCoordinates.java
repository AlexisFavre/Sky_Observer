package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

/**
 * 
 * @author Alexis FAVRE (310552)
 *
 */
public final class EclipticCoordinates extends SphericalCoordinates {

    public EclipticCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }
    
    /**
     * 
     * @return
     * (double) longitude in radians
     */
    double lon() {
        return lon();
    }
    
    /**
     * 
     * @return
     * (double) latitude in radians
     */
    double lat() {
        return lat();
    }
    
    /**
     * 
     * @return
     * (double) longitude in degrees
     */
    double lonDeg() {
        return Angle.toDeg(lon());
    }
    
    /**
     * 
     * @return
     * (double) latitude in degrees
     */
    double latDeg() {
        return Angle.toDeg(lat());
    }

}
