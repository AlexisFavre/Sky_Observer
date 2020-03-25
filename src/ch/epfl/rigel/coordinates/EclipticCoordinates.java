package ch.epfl.rigel.coordinates;

import static ch.epfl.rigel.Preconditions.checkInInterval;

import java.util.Locale;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

/**
 * 
 * @author Alexis FAVRE (310552)
 * center of the celestial sphere is the center of the earth (or sometimes center of the sun)
 * plan of reference is the elliptic plan of the earth
 * direction of reference is the vernal point(intersection between equatorial and elliptic plans)
 */
public final class EclipticCoordinates extends SphericalCoordinates {

    private EclipticCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }
    
    /**
     * @return
     * (double) longitude in radians
     */
    public double lon() { return super.lon();}
    
    /**
     * @return
     * (double) latitude in radians
     */
    public double lat() {return super.lat();}
    
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
    
    /**
     * to create new EclipticCoordinates
     * @param lon
     * (double) longitude in radians (must be in [0,2Pi[)
     * @param lat
     * (double) latitude in radians (must be in [-Pi/2,Pi/2])
     * @return
     * new EclipticCoordinates
     */
    public static EclipticCoordinates of(double lon, double lat) {
        checkInInterval(RightOpenInterval.of(0, Angle.TAU), lon);
        checkInInterval(ClosedInterval.of(-Angle.TAU/4, Angle.TAU/4), lat);
        return new EclipticCoordinates(lon, lat);
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(λ=%.4f°, β=%.4f°)", lonDeg(), latDeg());
    }

}
