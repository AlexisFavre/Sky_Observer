package ch.epfl.rigel.coordinates;

import static ch.epfl.rigel.Preconditions.checkInInterval;

import java.util.Locale;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
/**
 * system of coordinates :
 * the observer is the center of the sphere
 * plan of reference is the horizon plan of the earth
 * direction of reference is the North
 * @author Alexis FAVRE (310552)
 */
public final class HorizontalCoordinates extends SphericalCoordinates {
    
    private HorizontalCoordinates(double azimuth, double altitude) {
        super(azimuth, altitude);
    }
    
    /**
     * to create new HorizontalCoordinates
     * @param az
     * (double) azimuth in radians (must be in [0,2Pi[)
     * angle between North and vertical plan which contains the observed object
     * @param alt
     * (double) altitude in radians (must be in [-Pi/2,Pi/2])
     * vertical angle between horizontal plan and the observed object
     * @return new HorizontalCoordinates
     * @throws IllegalArgumentException if az does not belong in [0,2Pi[
     *         or if alt is not in [-Pi/2,Pi/2]
     */
    public static HorizontalCoordinates of(double az, double alt) throws IllegalArgumentException{
        checkInInterval(RightOpenInterval.of(0, Angle.TAU), az);
        checkInInterval(ClosedInterval.of(-Angle.TAU/4, Angle.TAU/4), alt);
        return new HorizontalCoordinates(az, alt);
    }
    
    /**
     * to create new HorizontalCoordinates
     * @param azDeg
     * (double) azimuth in degrees (must be in [0°,360°[)
     * angle between North and vertical plan which contains the observed object
     * @param altDeg
     * (double) altitude in degrees (must be in [-90°,90°])
     * vertical angle between horizontal plan and the observed object
     * @return new HorizontalCoordinates
     * @throws IllegalArgumentException if lonDes does not belong in [0°, +360°[
     *       or if latDeg is not in [–90°, +90°]
     */
    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) throws IllegalArgumentException {
        checkInInterval(RightOpenInterval.of(0, 360), azDeg);
        checkInInterval(ClosedInterval.of(-90, 90), altDeg);
        
        return new HorizontalCoordinates(Angle.ofDeg(azDeg), Angle.ofDeg(altDeg));
    }
    
    /**
     * @param that
     * (HorizontalCoordinates) of the second point
     * @return
     * (double) angular distance between this and the other point
     * in radians
     */
    public double angularDistanceTo(HorizontalCoordinates that) {
        return Math.acos(Math.sin(this.alt()) * Math.sin(that.alt())
                + Math.cos(this.alt()) * Math.cos(that.alt()) * Math.cos(this.az()-that.az()));
    }
    /**
     * @param n must be "N"
     * @param e must be "E"
     * @param s must be "S"
     * @param w must be "W"
     * @return
     * (String) the correspond octant of the coordinates
     */
    public String azOctantName(String n, String e, String s, String w) {
        int normalizedAzimuth = (int) Math.round(az()*8/Angle.TAU);
        switch(normalizedAzimuth) {
        case 0 : return n;
        case 1 : return n + e;
        case 2 : return e;
        case 3 : return s + e;
        case 4 : return s;
        case 5 : return s + w;
        case 6 : return w;
        case 7 : return n + w;
        default : return n;
        }
    }
    
    /**
     * @return
     * (double) azimuth in radians
     */
    public double az() {
        return lon();
    }
    
    /**
     * @return
     * (double) altitude in radians
     */
    public double alt() {
        return lat();
    }
    /**
     * @return
     * (double) azimuth in degrees
     */
    public double azDeg() {
        return Angle.toDeg(az());
    }
    
    /**
     * @return
     * (double) altitude in radians
     */
    public double altDeg() {
        return Angle.toDeg(alt());
    }
    
    @Override
    /** @return an representation of the coordinates */
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", azDeg(), altDeg());
    }
    
}
