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
     * @param azimuth correspond to longitude
     * (double) in radians
     * @param altitude correspond to latitude
     * (double) in radians
     */
    private HorizontalCoordinates(double azimuth, double altitude) {
        super(azimuth, altitude);
    }
    
    /**
     * to create new HorizontalCoordinates
     * @param az
     * (double) azimuth in radians (must be in [0,2Pi[)
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
     * @param azDeg
     * (double) azimuth in degrees (must be in [0,360[)
     * @param altDeg
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
     * (HorizontalCoordinates) of the second point
     * @return
     * (double) angular distance between this and the other point
     * in radians
     */
    public double angularDistanceTo(HorizontalCoordinates that) {
        return Math.acos(Math.sin(this.alt()) * Math.sin(that.alt())
                + Math.cos(this.alt()) * Math.cos(that.alt()) * Math.cos(this.az()-that.az()));
    }
    
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
     * 
     * @return
     * (double) azimuth in radians
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
     * (double) azimuth in degrees
     */
    public double azDeg() {
        return Angle.toDeg(az());
    }
    
    /**
     * 
     * @return
     * (double) altitude in radians
     */
    public double altDeg() {
        return Angle.toDeg(alt());
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", azDeg(), altDeg());
    }
    
}
