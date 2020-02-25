package ch.epfl.rigel.coordinates;

import static ch.epfl.rigel.Preconditions.checkArgument;
import java.util.Locale;
import ch.epfl.rigel.math.Angle;

/**
 * 
 * @author Alexis FAVRE (310552)
 */
public final class GeographicCoordinates extends SphericalCoordinates {

    private GeographicCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
        // TODO Auto-generated constructor stub
    }
    
    public GeographicCoordinates ofDeg(double lonDeg, double latDeg) {
        checkArgument(isValidLatDeg(latDeg));
        checkArgument(isValidLonDeg(lonDeg));
        
        return new GeographicCoordinates(Angle.ofDeg(lonDeg), Angle.ofDeg(latDeg));
    }
    
    public static boolean isValidLonDeg(double lonDeg) {
        return lonDeg<180 && lonDeg>=-180;
    }
    
    public static boolean isValidLatDeg(double latDeg) {
        return latDeg<=90 && latDeg>=-90;
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
    
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(lon=%.4f°, lat=%.4f°)", lonDeg(), latDeg());
    }

}
