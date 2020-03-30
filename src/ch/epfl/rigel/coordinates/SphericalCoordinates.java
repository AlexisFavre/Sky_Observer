package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

/**
 * Represent the concept of spherical coordinates
 *
 * @author Alexis FAVRE (310552)
 * @see GeographicCoordinates
 * @see HorizontalCoordinates
 * @see EquatorialCoordinates
 * @see EclipticCoordinates
 * see https://cs108.epfl.ch/p/02_spherical-coords.html#geo-coord
 */
abstract class SphericalCoordinates {

    private final double longitude;
    private final double latitude;

    /**
     *
     * @param longitude in radians
     * @param latitude in radians
     */
    SphericalCoordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * 
     * @return the longitude in radians
     */
    double lon() {
        return longitude;
    }
    
    /**
     * 
     * @return the latitude in radians
     */
    double lat() {
        return latitude;
    }
    
    /**
     * 
     * @return the longitude in degrees
     */
    double lonDeg() {
        return Angle.toDeg(lon());
    }
    
    /**
     * 
     * @return the latitude in degrees
     */
    double latDeg() {
        return Angle.toDeg(lat());
    }

    /**
     * Always throw exception
     * {@code polynomial.hashCode()} is forbidden
     *
     * @throws UnsupportedOperationException in all conditions
     */
    @Override
    public final int hashCode() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throw exception
     * {@code polynomial.equals()} is forbidden
     *
     * @throws UnsupportedOperationException in all conditions
     */
    @Override
    public final boolean equals(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
