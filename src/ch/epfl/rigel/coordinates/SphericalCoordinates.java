package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

/**
 * 
 * @author Alexis FAVRE (310552)
 * see https://cs108.epfl.ch/p/02_spherical-coords.html#geo-coord
 */
abstract class SphericalCoordinates {

    private final double longitude;
    private final double latitude;

    /**
     * 
     * @param longitude
     * (double) in radians
     * @param latitude
     * (double) in radians
     */
    SphericalCoordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
    /**
     * 
     * @return
     * (double) longitude in radians
     */
    double lon() {
        return longitude;
    }
    
    /**
     * 
     * @return
     * (double) latitude in radians
     */
    double lat() {
        return latitude;
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
