package ch.epfl.rigel.coordinates;

import static ch.epfl.rigel.Preconditions.checkArgument;
import java.util.Locale;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

/**
 * system of coordinates :
 * plan of reference is the equatorial plan of the earth
 * direction of reference is the Greenwich Meridian
 * @author Alexis FAVRE (310552)
 */
public final class GeographicCoordinates extends SphericalCoordinates {

    private GeographicCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }
    
    /**
     * 
     * @param lonDeg
     * (double) longitude in degrees [–180°, +180°[ (west to east)
     * @param latDeg
     * (double) latitude in degrees  [–90°, +90°] (South to North)
     * @return
     * new GeographicCoordinates with these characteristic(if they are ok)
     * @throws IllegalArgumentException if lonDes does not belong in [–180°, +180°[
     *       or if latDeg is not in [–90°, +90°]
     */
    public static GeographicCoordinates ofDeg(double lonDeg, double latDeg) throws IllegalArgumentException{
        checkArgument(isValidLatDeg(latDeg));
        checkArgument(isValidLonDeg(lonDeg));
        
        return new GeographicCoordinates(Angle.ofDeg(lonDeg), Angle.ofDeg(latDeg));
    }
    
    /**
     * check is the given longitude in degree is valid or not
     * @param lonDeg
     * (double) longitude in degrees 
     * @return
     * true if and only if longitude is in [–180°, +180°[
     */
    public static boolean isValidLonDeg(double lonDeg) {
        return RightOpenInterval.of(-180, 180).contains(lonDeg);
    }
    
    /**
     * check is the given latitude in degree is valid or not
     * @param latDeg
     * (double) latitude in degrees
     * @return
     * true if and only if longitude is in [–90°, +90°]
     */
    public static boolean isValidLatDeg(double latDeg) {
        return ClosedInterval.of(-90, 90).contains(latDeg);
    }
    
    /**
     * @return
     * (double) longitude in radians
     */
    public double lon() {
        return super.lon();
    }
    
    /**
     * @return
     * (double) latitude in radians
     */
    public double lat() {
        return super.lat();
    }
    
    /**
     * @return
     * (double) longitude in degrees
     */
    public double lonDeg() {
        return Angle.toDeg(lon());
    }
    
    /**
     * @return
     * (double) latitude in degrees
     */
    public double latDeg() {
        return Angle.toDeg(lat());
    }
    
    @Override
    /** @return an representation of the coordinates */
    public String toString() {
        return String.format(Locale.ROOT, "(lon=%.4f°, lat=%.4f°)", lonDeg(), latDeg());
    }

}
