package ch.epfl.rigel.astronomy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

/**
 * Model of the Planets to calculte the diffrents characteristics of the
 * Differents planets at a precise day
 * @author Augustin ALLARD (299918)
 */
public enum PlanetModel implements  CelestialObjectModel<Planet> {

    MERCURY("Mercure", 0.24085, 75.5671, 77.612, 0.205627,
            0.387098, 7.0051, 48.449, 6.74, -0.42),
    VENUS("Vénus", 0.615207, 272.30044, 131.54, 0.006812,
            0.723329, 3.3947, 76.769, 16.92, -4.40),
    EARTH("Terre", 0.999996, 99.556772, 103.2055, 0.016671,
            0.999985, 0, 0, 0, 0),
    MARS("Mars", 1.880765, 109.09646, 336.217, 0.093348,
            1.523689, 1.8497, 49.632, 9.36, -1.52),
    JUPITER("Jupiter", 11.857911, 337.917132, 14.6633, 0.048907,
            5.20278, 1.3035, 100.595, 196.74, -9.40),
    SATURN("Saturne", 29.310579, 172.398316, 89.567, 0.053853,
            9.51134, 2.4873, 113.752, 165.60, -8.88),
    URANUS("Uranus", 84.039492, 271.063148, 172.884833, 0.046321,
            19.21814, 0.773059, 73.926961, 65.80, -7.19),
    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483,
            30.1985, 1.7673, 131.879, 62.20, -6.87);
    
    /**
     * List of all these Planets with their characteristics
     */
    public static List<PlanetModel> ALL = new ArrayList<>(List.copyOf(Arrays.asList(PlanetModel.values())));

    private final String name;
    private final double t;
    private final double eps;
    private final double w;
    private final double e;
    private final double a;
    private final double i;
    private final double omega;
    private final double tet0;
    private final double V0;

    /**
     *
     * @param name the name of the planetModel
     * @param t the revolution period in tropic years
     * @param degEps the longitude at J2010 in degrees
     * @param degW the longitude at the perigee in degrees
     * @param e the eccentricity no unity
     * @param a the half major axis of the orbit in UA
     * @param degI the inclination of the orbit at the ecliptic in degrees
     * @param degOmega the longitude of ascending node in degrees
     * @param tet0 the angular size at a one UA distance in arc seconds
     * @param V0 the magnitude no unity
     */
    PlanetModel(String name, double t, double degEps, double degW, double e,
                double a, double degI, double degOmega, double tet0, double V0) {
        this.name = name;
        this.t = t;
        this.eps = Angle.ofDeg(degEps);
        this.w = Angle.ofDeg(degW);
        this.e = e;
        this.a = a;
        this.i = Angle.ofDeg(degI);
        this.omega = Angle.ofDeg(degOmega);
        this.tet0 = Angle.ofArcsec(tet0);
        this.V0 = V0;
    }

    /**
     *
     * @return in radians
     */
    private double meanAnomaly(double daysSinceJ2010) {
        return Angle.TAU * daysSinceJ2010 / (365.242191*t) + eps - w;
    }

    /**
     *
     * @return in radians
     */
    private double trueAnomaly(double daysSinceJ2010) {
        return meanAnomaly(daysSinceJ2010) + 2 * e * Math.sin(meanAnomaly(daysSinceJ2010));
    }

    /**
     *
     * @return radius in UA
     */
    private double r(double daysSinceJ2010) {
        return a * (1 - e * e) / (1 + e * Math.cos(trueAnomaly(daysSinceJ2010)));
    }

    /**
     *
     * @return heliocentric longitude in the orbit plan in radians
     */
    private double l(double daysSinceJ2010) {
        return trueAnomaly(daysSinceJ2010) + w;
    }

    @Override
    /**
     * calculate postion of the Planet at a precise day
     * @param daysSinceJ2010 and this day
     * @param eclipticToEquatorialConversion is a converter from elliptic to equatorial coordinates 
     * @return the Planet at this day
     */
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
//        if(this.a == EARTH.a) {
//            throw new UnsupportedOperationException();
//        }
        
        // planet info at the given time
        double phi = Math.asin(Math.sin((l(daysSinceJ2010)- omega)) * Math.sin(i)); // lat ecliptic heliocentric
        double r_  = r(daysSinceJ2010) * Math.cos(phi); // r projection on ecliptic
        double l_  = // long ecliptic heliocentric projected
                        Math.atan2(
                                Math.sin(l(daysSinceJ2010) - omega) * Math.cos(i)
                                , Math.cos(l(daysSinceJ2010) - omega)
                        ) + omega;

        // Ecliptic coordinates
        double longitude = 0;
        if(this.a < EARTH.a) {
            longitude =
                    Math.atan2(
                            r_ * Math.sin(EARTH.l(daysSinceJ2010) - l_),
                             (EARTH.r(daysSinceJ2010) - r_ * Math.cos(EARTH.l(daysSinceJ2010) - l_))
                    ) + Angle.TAU/2 + EARTH.l(daysSinceJ2010);
        } else {
            longitude =
                    Math.atan2(
                            EARTH.r(daysSinceJ2010) * Math.sin(l_ - EARTH.l(daysSinceJ2010))
                            , (r_ - EARTH.r(daysSinceJ2010) * Math.cos(l_ - EARTH.l(daysSinceJ2010)))
                    ) + l_;
        }

        double latitude =
                Math.atan(
                        r_ * Math.tan(phi) * Math.sin(longitude - l_)
                                / (EARTH.r(daysSinceJ2010) * Math.sin(l_ - EARTH.l(daysSinceJ2010)))
                );
        EclipticCoordinates position = EclipticCoordinates.of(Angle.normalizePositive(longitude), latitude);

        // angular size and magnitude
        double p = Math.sqrt(Math.pow(EARTH.r(daysSinceJ2010),2) 
                + Math.pow(r(daysSinceJ2010),2) 
                - 2 * EARTH.r(daysSinceJ2010) * r(daysSinceJ2010)
                * Math.cos(l(daysSinceJ2010) - EARTH.l(daysSinceJ2010)) * Math.cos(phi)
        );
        double phase = (1 + Math.cos(longitude - l(daysSinceJ2010)))/2;
        double angularSize = tet0 / p;
        double magnitude = V0 + 5 * Math.log10(r(daysSinceJ2010) * p / Math.sqrt(phase));

        return new Planet(this.name, eclipticToEquatorialConversion.apply(position), (float)angularSize, (float)magnitude);
    }
}
