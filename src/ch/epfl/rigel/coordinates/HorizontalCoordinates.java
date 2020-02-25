package ch.epfl.rigel.coordinates;

import static ch.epfl.rigel.Preconditions.checkArgument;

import java.util.Locale;

import ch.epfl.rigel.math.Angle;
/**
 * 
 * @author Alexis FAVRE (310552)
 *
 */
public final class HorizontalCoordinates extends SphericalCoordinates {
    
    /**
     * 
     * @param azimut correspond to longitude
     * (double) in radians
     * @param altitude correspond to latitude
     * (double) in radians
     */
    private HorizontalCoordinates(double azimut, double altitude) {
        super(azimut, altitude);
    }
    
    /**
     * to create new HorizontalCoordinates
     * @param az
     * (double) azimut in radians (must be in [0,2Pi[)
     * @param alt
     * (double) altitude in radians (must be in [-Pi/2,Pi/2])
     * @return
     * new HorizontalCoordinates
     */
    public static HorizontalCoordinates of(double az, double alt) {
        checkArgument(0<=az && az < Angle.TAU);
        checkArgument(-Angle.TAU/4<= alt && alt<=Angle.TAU/4);
        return new HorizontalCoordinates(az, alt);
    }
    
    /**
     * to create new HorizontalCoordinates
     * @param az
     * (double) azimut in degrees (must be in [0,360[)
     * @param alt
     * (double) altitude in degrees (must be in [-90,90])
     * @return
     * new HorizontalCoordinates
     */
    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {
        checkArgument(0<=azDeg && azDeg< 360);
        checkArgument(-90<=altDeg && altDeg<=90);
        return new HorizontalCoordinates(Angle.ofDeg(azDeg), Angle.ofDeg(altDeg));
    }
    
    /**
     * 
     * @param that
     * (HorizontalCoordiantes) of the second point
     * @return
     * (double) angular distance between this and the other point
     * in radians
     */
    public double angularDistanceTo(HorizontalCoordinates that) {
        return Math.acos(Math.sin(this.alt()) * Math.sin(that.alt())
                + Math.cos(this.alt()) * Math.cos(that.alt()) * Math.cos(this.az()-that.az()));
    }
    
    public String azOctantName(String n, String e, String s, String w) {
        return ""; // en cours reflexion
    }
    
    /**
     * 
     * @return
     * (double) azimut in radians
     */
    public double az() {
        return lon();
    }
    
    /**
     * 
     * @return
     * (double) altitude in radians
     */
    public double alt() {
        return lat();
    }
    /**
     * 
     * @return
     * (double) azimut in degrees
     */
    public double azDeg() {
        return Angle.ofDeg(az());
    }
    
    /**
     * 
     * @return
     * (double) altitude in radians
     */
    public double altDeg() {
        return Angle.ofDeg(alt());
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", az(), alt());
    }
    
}
