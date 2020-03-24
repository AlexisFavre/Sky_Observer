package ch.epfl.rigel.math;

import static ch.epfl.rigel.Preconditions.checkInInterval;

/**
 * @author Alexis FAVRE (310552)
 * 
 * by default angles are in RADIANS !
 * non instanciable class
 */
public final class Angle {
    
    public final static double TAU = 2*Math.PI;
    private static final double DEG_PER_RAD = 360.0 / TAU;
    private static final double RAD_PER_HOUR = TAU / 24;


    private Angle() {}
    
    /**
     * Normalize angle on [0,tau[
     * @param rad
     * (double) angle
     * @return
     * (double) normalized angle
     */
    public static double normalizePositive(double rad) {
        return (rad%TAU + TAU)%TAU;
    }
    
    /**
     * convert Arc seconds in degrees
     * @param sec
     * (double)
     * @return
     * angle in radians
     */
    public static double ofArcsec(double sec) {
       return sec/(3600*DEG_PER_RAD); 
    }
    
    /**
     * convert an given angle from sexagecimal to degrees
     * @param deg
     * @param min
     * @param sec
     * @return
     * (double) angle in radians
     */
    public static double ofDMS(int deg, int min, double sec) {
        checkInInterval(RightOpenInterval.of(0,60), sec);
        checkInInterval(RightOpenInterval.of(0,60), min);
        return ofDeg(deg + min/60.0 + sec/3600.0);
    }
    
    /**
     * convert radians in degrees
     * @param rad
     * (double)
     * @return
     * angle in degrees
     */
    public static double toDeg(double rad) {
        return Math.toDegrees(rad);
      }
    
    /**
     * convert degrees in radians
     * @param deg
     * (double)
     * @return
     * angle in radians
     */
    public static double ofDeg(double deg) {
        return deg/DEG_PER_RAD;
    }
    
    /**
     * convert hours in radians
     * @param hr
     * (double)
     * @return
     * angle in radians
     */
    public static double ofHr(double hr) {
        return hr*RAD_PER_HOUR;
    }
    
    /**
     * convert radians to degree
     * @param rad
     * (double)
     * @return
     * angle in hours
     */
    public static double toHr(double rad) {
        return rad/RAD_PER_HOUR;
    }

}
