package ch.epfl.rigel.coordinates;

import static ch.epfl.rigel.Preconditions.checkInInterval;
import static ch.epfl.rigel.math.ClosedInterval.CSymmetricInterOfSizePi;
import static ch.epfl.rigel.math.RightOpenInterval.ROInter_0To2Pi;

import java.util.Locale;

import ch.epfl.rigel.math.Angle;

/**
 * Type of spherical coordinates that describe the position of an object where
 * the center of the celestial sphere is the center of the earth
 * the plan of reference is the equatorial plan of the earth
 * direction of reference is the vernal point (intersection between equatorial and elliptic plans)
 *
 * @author Alexis FAVRE (310552)
 * see https://www.google.com/url?sa=i&url=https%3A%2F%2Ffr.wikipedia.org%2Fwiki%2FSyst%25C3%25A8me_de_coordonn
 * %25C3%25A9es_%25C3%25A9quatoriales&psig=AOvVaw0C67d4KY50a1oQVwH7tsQd&ust=1582971266102000&source=images&cd=vfe&ved=
 * 0CAIQjRxqFwoTCNDmhdKB9OcCFQAAAAAdAAAAABAR
 */
public final class EquatorialCoordinates extends SphericalCoordinates {

    private EquatorialCoordinates(double ra, double dec) {
        super(ra, dec);
    }
    
    /**
     * Construct {@code EquatorialCoordinates} for the given ascension and declination
     *
     * @param ra right ascension in radians (must be in [0,2Pi[)
     * @param dec declination in radians (must be in [-Pi/2,Pi/2])
     * @return new {@code EquatorialCoordinates} instance
     * with the given ascension and declination
     * @throws IllegalArgumentException if {@code ra} or {@code dec} does not belong to
     * respectively [0,2Pi[ and [-Pi/2,Pi/2]
     */
    public static EquatorialCoordinates of(double ra, double dec) throws IllegalArgumentException{
        checkInInterval(ROInter_0To2Pi, ra);
        checkInInterval(CSymmetricInterOfSizePi, dec);
        return new EquatorialCoordinates(ra, dec);
    }

    /**
     * @return right ascension in radians
     */
    public double ra() {
        return lon();
    }
    
    /**
     * @return declination in radians
     */
    public double dec() {
        return lat();
    }
    
    /**
     * @return right ascension in degrees
     */
    public double raDeg() {
        return Angle.toDeg(lon());
    }
    
    /**
     * @return declination in degrees
     */
    public double decDeg() {
        return Angle.toDeg(lat());
    }
    
    /**
     * @return right ascension in hours
     */
    public double raHr() {
        return Angle.toHr(ra());
    }

    /**
     * @return a {@code String} view of {@code this} with the format
     * (ra= x, dec= y)
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(ra=%.4fh, dec=%.4f°)", raHr(), dec());
    }

}
