package ch.epfl.rigel.coordinates;

import static ch.epfl.rigel.Preconditions.checkArgument;
import static ch.epfl.rigel.math.ClosedInterval.CSymmetricInterOfSize180;

import java.util.Locale;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.RightOpenInterval;

/**
 * Type of spherical coordinates that describe a point on the earth where
 * longitude (West- to East+, max_abs_val: 90) that have greenwich meridian reference
 * and latitude (South to North, max_abs_val: 180) that have earth equatorial plan reference
 * are bounded
 *
 * @author Alexis FAVRE (310552)
 */
public final class GeographicCoordinates extends SphericalCoordinates {
    
    private final static RightOpenInterval SymmetricROInterOfSize360 = RightOpenInterval.of(-180, 180);

    private GeographicCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }
    
    /**
     * Construct {@code GeographicCoordinates} for the given longitude and latitude
     * 
     * @param lonDeg the longitude in degrees [–180°, +180°[ (W -> E)
     * @param latDeg the latitude in degrees  [–90°, +90°] (S -> N)
     * @return new {@code GeographicCoordinates} instance
     * with the given longitude and latitude
     * @throws IllegalArgumentException if {@code lonDeg} or {@code latDeg} does not belong to
     * respectively [–180°, +180°[ and [–90°, +90°]
     */
    public static GeographicCoordinates ofDeg(double lonDeg, double latDeg) throws IllegalArgumentException{
        checkArgument(isValidLatDeg(latDeg));
        checkArgument(isValidLonDeg(lonDeg));
        
        return new GeographicCoordinates(Angle.ofDeg(lonDeg), Angle.ofDeg(latDeg));
    }
    
    /**
     * Check if the given longitude in degrees is valid or not
     *
     * @param lonDeg the longitude in degrees
     * @return {@code True} if and only if {@code longDeg} belongs to [–180°, +180°[
     */
    public static boolean isValidLonDeg(double lonDeg) {
        return SymmetricROInterOfSize360.contains(lonDeg);
    }

    /**
     * Check if the given latitude in degrees is valid or not
     *
     * @param latDeg the latitude in degrees
     * @return {@code True} if and only if {@code latDeg} belongs to [–180°, +180°[
     */
    public static boolean isValidLatDeg(double latDeg) {
        return CSymmetricInterOfSize180.contains(latDeg);
    }
    
    /**
     * @return the longitude in radians
     */
    @Override
    public double lon() {
        return super.lon();
    }
    
    /**
     * @return the latitude in radians
     */
    @Override
    public double lat() {
        return super.lat();
    }
    
    /**
     * @return the longitude in degrees
     */
    @Override
    public double lonDeg() {
        return Angle.toDeg(lon());
    }
    
    /**
     * @return the latitude in degrees
     */
    @Override
    public double latDeg() {
        return Angle.toDeg(lat());
    }

    /**
     * @return a {@code String} view of {@code this} with the format
     * (lon= x, lat= y)
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(lon=%.4f°, lat=%.4f°)", lonDeg(), latDeg());
    }

}
