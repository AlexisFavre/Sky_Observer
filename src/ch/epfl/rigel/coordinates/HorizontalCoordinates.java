package ch.epfl.rigel.coordinates;

import static ch.epfl.rigel.Preconditions.checkInInterval;
import static ch.epfl.rigel.math.ClosedInterval.CSymmetricInterOfSize180;
import static ch.epfl.rigel.math.ClosedInterval.CSymmetricInterOfSizePi;
import static ch.epfl.rigel.math.RightOpenInterval.ROInter_0To2Pi;
import static ch.epfl.rigel.math.RightOpenInterval.ROInter_0To360;

import java.util.Locale;

import ch.epfl.rigel.math.Angle;

/**
 * Type of spherical coordinates that describe the position of an object where
 * the observer is the center of the sphere (ideal for observation)
 * the plan of reference is the astronomical horizon
 * the longitude is called azimuth, correspond to the clockwise angle of the object with the observer north (bounded)
 * the latitude is called altitude and is the angle of the object above the reference plan (negative if below & bounded)
 *
 * (ex: - you are standing straight, the head horizontal and you are facing the north :
 *        every object that you observe in front of you are at (0,0)
 *
 *      - then when you are turning head right you are increasing azimuth and head up increasing altitude)
 *
 * @author Alexis FAVRE (310552)
 */
public final class HorizontalCoordinates extends SphericalCoordinates {
    
    private HorizontalCoordinates(double azimuth, double altitude) {
        super(azimuth, altitude);
    }
    
    /**
     * Construct {@code HorizontalCoordinates} for the given azimuth and altitude
     *
     * @param az azimuth (clockwise angle of the object with the observer north)
     *           in radians (must be in [0,2Pi[)
     * @param alt altitude (angle of the object above the horizontal plan)
     *            in radians (must be in [-Pi/2,Pi/2])
     * @return new {@code HorizontalCoordinates} instance
     * with the given azimuth and altitude
     * @throws IllegalArgumentException if {@code az} or {@code alt} does not belong to
     * respectively [0,2Pi[ and [-Pi/2,Pi/2]
     */
    public static HorizontalCoordinates of(double az, double alt) throws IllegalArgumentException{
        checkInInterval(ROInter_0To2Pi, az);
        checkInInterval(CSymmetricInterOfSizePi, alt);
        return new HorizontalCoordinates(az, alt);
    }
    
    /**
     * Construct coordinates for the given azimuth and altitude
     *
     * @param azDeg azimuth (clockwise angle of the object with the observer north)
     *              in degrees (must be in [0,360°[)
     * @param altDeg altitude (angle of the object above the horizontal plan)
     *               in degrees (must be in [-90°,90°])
     * @return new {@code HorizontalCoordinates} instance
     * with the given azimuth and altitude
     * @throws IllegalArgumentException if az or alt does not belong to respectively [0,360°[ and [-90°,90°]
     */
    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) throws IllegalArgumentException {
        checkInInterval(ROInter_0To360, azDeg);
        checkInInterval(CSymmetricInterOfSize180, altDeg);
        
        return new HorizontalCoordinates(Angle.ofDeg(azDeg), Angle.ofDeg(altDeg));
    }
    
    /**
     * Computes the angular distance between {@code this} and another given point
     * (ex: angular distance between the point above your head and in front of you is pi/2)
     *
     * @param that coordinates of the second point
     * @return angular distance between this and the other point in radians
     */
    public double angularDistanceTo(HorizontalCoordinates that) {
        return Math.acos(Math.sin(this.alt()) * Math.sin(that.alt())
                + Math.cos(this.alt()) * Math.cos(that.alt()) * Math.cos(this.az()-that.az()));
    }

    /**
     * Gives the octant (piece of earth) in which {@code this} is
     * The name is composed of at most a combination of two cardinal initials
     *
     * @return the correspond octant of the coordinates
     */
    public String azOctantName() {
        return azOctantName("N", "E", "S", "W");
    }

    /**
     * Gives the octant (piece of earth) in which {@code this} is
     * The name is composed of at most a combination of two cardinal initials
     *
     * @param n must be "N"
     * @param e must be "E"
     * @param s must be "S"
     * @param w must be "W"
     * @return the correspond octant of the coordinates
     */
    public String azOctantName(String n, String e, String s, String w) {
        int normalizedAzimuth = (int) Math.round(az()*8/Angle.TAU);
        switch(normalizedAzimuth) {
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
     * @return the azimuth in radians
     */
    public double az() {
        return lon();
    }
    
    /**
     * @return the altitude in radians
     */
    public double alt() {
        return lat();
    }

    /**
     * @return the azimuth in degrees
     */
    public double azDeg() {
        return Angle.toDeg(az());
    }
    
    /**
     * @return the altitude in degrees
     */
    public double altDeg() {
        return Angle.toDeg(alt());
    }

    /**
     * @return a {@code String} view of {@code this} with the format
     * (az= x, alt= y)
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", azDeg(), altDeg());
    }
    
}
