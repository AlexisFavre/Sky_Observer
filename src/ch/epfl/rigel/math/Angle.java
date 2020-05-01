package ch.epfl.rigel.math;

import static ch.epfl.rigel.Preconditions.checkInInterval;
import static ch.epfl.rigel.math.RightOpenInterval.ROInter_0To2Pi;;

/**
 * Non instantiable class containing only {@code static} methods
 *
 * Used to normalize angles between 0-2pi and to perform conversion between angles unities
 * RADIANS is the unity that we want to use by defaults for computations
 *
 * @author Alexis FAVRE (310552)
 */
public final class Angle {

    public static final double TAU = 2*Math.PI;
    private static final double DEG_PER_RAD  = 360.0 / TAU;
    private static final double RAD_PER_HOUR = TAU / 24;
    private static final RightOpenInterval ROInter_0To60 = RightOpenInterval.of(0, 60);

    
    private Angle() {}
    
    /**
     * Normalize the given angle value in [0,2pi[
     * @param rad angle value to be normalized
     * @return normalized angle value
     */
    public static double normalizePositive(double rad) {
        return ROInter_0To2Pi.reduce(rad);
    }
    
    /**
     * Convert Arc seconds in degrees
     * @param sec angle value in Arc-seconds to be converted
     * @return corresponding angle value in radians
     */
    public static double ofArcsec(double sec) {
       return sec/(3600*DEG_PER_RAD); 
    }
    
    /**
     * Convert sexagesimal to degrees
     * @param deg degrees of angle value
     * @param min minutes of angle value
     * @param sec seconds of angle value
     * @return corresponding angle value in radians
     */
    public static double ofDMS(int deg, int min, double sec) {
        checkInInterval(ROInter_0To60, sec);
        checkInInterval(ROInter_0To60, min);
        return ofDeg(deg + min/60.0 + sec/3600.0);
    }
    
    /**
     * Convert radians in degrees
     * @param rad angle value in radians to be converted
     * @return corresponding angle value in degrees
     */
    public static double toDeg(double rad) {
        return Math.toDegrees(rad);
      }
    
    /**
     * Convert degrees in radians
     * @param deg angle value in degrees to be converted
     * @return corresponding angle value in radians
     */
    public static double ofDeg(double deg) {
        return deg/DEG_PER_RAD;
    }
    
    /**
     * Convert hours in radians
     * @param hr angle value in hours to be converted
     * @return corresponding angle value in radians
     */
    public static double ofHr(double hr) {
        return hr*RAD_PER_HOUR;
    }
    
    /**
     * Convert radians to hours
     * @param rad angle value in radians to be converted
     * @return corresponding angle values in hours
     */
    public static double toHr(double rad) {
        return rad/RAD_PER_HOUR;
    }

}
