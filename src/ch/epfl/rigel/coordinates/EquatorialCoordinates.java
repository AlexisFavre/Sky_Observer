package ch.epfl.rigel.coordinates;

import static ch.epfl.rigel.Preconditions.checkArgument;

import java.util.Locale;

import ch.epfl.rigel.math.Angle;

/**
 * @see https://www.google.com/url?sa=i&url=https%3A%2F%2Ffr.wikipedia.org%2Fwiki%2FSyst%25C3%25A8me_de_coordonn%25C3%25A9es_%25C3%25A9quatoriales&psig=AOvVaw0C67d4KY50a1oQVwH7tsQd&ust=1582971266102000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCNDmhdKB9OcCFQAAAAAdAAAAABAR
 * @author Alexis FAVRE (310552)
 * center of the celestial sphere is the center of the earth
 * plan of reference is the equatorial plan of the earth
 * direction of reference is the vernal point(intersection between equatorial and elliptic plans)
 */
public final class EquatorialCoordinates extends SphericalCoordinates {

    /**
     * 
     * @param ra correspond to longitude
     * (double) in radians
     * @param dec correspond to latitude
     * (double) in radians
     */
    private EquatorialCoordinates(double ra, double dec) {
        super(ra, dec);
    }
    
    /**
     * to create new EquatoriolCoordinates
     * @param ra
     * (double) right ascension in radians (must be in [0,2Pi[)
     * @param dec
     * (double) declination in radians (must be in [-Pi/2,Pi/2])
     * @return
     * new EquatoriolCoordinates (if characteristics are ok)
     * otherwise throw {@link IllegalArgumentException}
     */
    public static EquatorialCoordinates of(double ra, double dec) {
        checkArgument(0<=ra && ra < Angle.TAU);
        checkArgument(-Angle.TAU/4<= dec && dec<=Angle.TAU/4);
        return new EquatorialCoordinates(ra, dec);
    }

    /**
     * 
     * @return
     * (double) right ascension in radians
     */
    double ra() {
        return lon();
    }
    
    /**
     * 
     * @return
     * (double) declination in radians
     */
    double dec() {
        return lat();
    }
    
    /**
     * 
     * @return
     * (double) right ascension in degrees
     */
    double raDeg() {
        return Angle.toDeg(lon());
    }
    
    /**
     * 
     * @return
     * (double) declination in degrees
     */
    double decDeg() {
        return Angle.toDeg(lat());
    }
    
    /**
     * 
     * @return
     * (double) right ascension in hours
     */
    double raHr() {
        return Angle.toHr(ra());
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(ra=%.4fh, dec=%.4f°)", raHr(), dec());
    }

}
