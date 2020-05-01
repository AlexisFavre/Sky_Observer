package ch.epfl.rigel.coordinates;

import static ch.epfl.rigel.Preconditions.checkInInterval;
import static ch.epfl.rigel.math.ClosedInterval.CSymmetricInterOfSizePi;
import static ch.epfl.rigel.math.RightOpenInterval.ROInter_0To2Pi;

import java.util.Locale;

import ch.epfl.rigel.math.Angle;

/**
 * Type of spherical coordinates that describe the position of an object where
 * center of the celestial sphere is the center of the earth (or sometimes center of the sun)
 * the plan of reference is the elliptic plan of the earth
 * direction of reference is the vernal point (intersection between equatorial and elliptic plans)
 *
 * @author Alexis FAVRE (310552)
 */
public final class EclipticCoordinates extends SphericalCoordinates {

    private EclipticCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }
    
    /**
     * Construct {@code EclipticCoordinates} for the given ecliptic longitude and ecliptic latitude
     *
     * @param lon ecliptic longitude in radians (must be in [0,2Pi[)
     * @param lat ecliptic latitude in radians (must be in [-Pi/2,Pi/2])
     * @return new {@code EclipticCoordinates} instance
     * with the given ecliptic longitude and ecliptic latitude
     * @throws IllegalArgumentException if {@code lon} or {@code lat} does not belong to
     * respectively [0,2Pi[ and [-Pi/2,Pi/2]
     */
    public static EclipticCoordinates of(double lon, double lat) throws IllegalArgumentException {
        checkInInterval(ROInter_0To2Pi, lon);
        checkInInterval(CSymmetricInterOfSizePi, lat);
        return new EclipticCoordinates(lon, lat);
    }
    
    /**
     * @return longitude in radians
     */
    @Override
    public double lon() { return super.lon();}
    
    /**
     * @return latitude in radians
     */
    @Override
    public double lat() {return super.lat();}
    
    /**
     * @return longitude in degrees
     */
    @Override
    public double lonDeg() {
        return Angle.toDeg(lon());
    }
    
    /**
     * @return latitude in degrees
     */
    @Override
    public double latDeg() {
        return Angle.toDeg(lat());
    }

    /**
     * @return a {@code String} view of {@code this} with the format
     * (λ= x, β= y)
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(λ=%.4f°, β=%.4f°)", lonDeg(), latDeg());
    }
}
