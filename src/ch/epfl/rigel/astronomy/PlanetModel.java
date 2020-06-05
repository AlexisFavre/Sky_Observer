package ch.epfl.rigel.astronomy;

import java.util.List;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

/**
 * Model that compute a {@code Planet} state at a given time
 * Used to update a {@code Planet}
 *
 * @author Augustin ALLARD (299918)
 */
public enum PlanetModel implements  CelestialObjectModel<Planet> {

    MERCURY("Mercure", 0.24085, 75.5671, 77.612, 0.205627,
            0.387098, 7.0051, 48.449, 6.74, -0.42, 0.52, 1.48, 1.04),
    VENUS("Vénus", 0.615207, 272.30044, 131.54, 0.006812,
            0.723329, 3.3947, 76.769, 16.92, -4.40, 0.25, 1.74, 1.14),
    EARTH("Terre", 0.999996, 99.556772, 103.2055, 0.016671,
            0.999985, 0, 0, 0, 0, 0, 0, 0),
    MARS("Mars", 1.880765, 109.09646, 336.217, 0.093348,
            1.523689, 1.8497, 49.632, 9.36, -1.52, 0.37, 2.68, 1.69),
    JUPITER("Jupiter", 11.857911, 337.917132, 14.6633, 0.048907,
            5.20278, 1.3035, 100.595, 196.74, -9.40, 3.93, 6.47, 5.25),
    SATURN("Saturne", 29.310579, 172.398316, 89.567, 0.053853,
            9.51134, 2.4873, 113.752, 165.60, -8.88, 9.36, 10.03, 9.61),
    URANUS("Uranus", 84.039492, 356.135400, 172.884833, 0.046321,
            19.21814, 0.773059, 73.926961, 65.80, -7.19, 18, 20, 19.5),
    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483,
            30.1985, 1.7673, 131.879, 62.20, -6.87, 28.7, 31.4, 30);
    
    /**
     * List of all the planet instances of the model with their characteristics
     */
    public static List<PlanetModel> ALL = List.of(values());

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
    private final double dMin;
    private final double dMax;
    private final double dAv;

    /**
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
                double a, double degI, double degOmega, double tet0, double V0, double dMin, double dMax, double dAv) {
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
        this.dMin = dMin;
        this.dMax = dMax;
        this.dAv = dAv;
    }

    /**
     * @return meanAnomaly of the Planet in radians
     */
    private double meanAnomaly(double daysSinceJ2010) {
        return Angle.TAU*daysSinceJ2010/(365.242191*t) + eps - w;
    }

    /**
     * @return trueAnomaly of the Planet in radians
     */
    private double trueAnomaly(double daysSinceJ2010) {
        return meanAnomaly(daysSinceJ2010) + 2*e*Math.sin(meanAnomaly(daysSinceJ2010));
    }

    /**
     * @return radius in UA
     */
    private double r(double daysSinceJ2010) {
        return a*(1 - e*e) / (1 + e*Math.cos(trueAnomaly(daysSinceJ2010)));
    }

    /**
     * @return longitude in the orbit plan of the Planet in radians
     */
    private double l(double daysSinceJ2010) {
        return trueAnomaly(daysSinceJ2010) + w;
    }

    /**
     * {@inheritDoc}
     * t
     * @throws @code{UnsupportedOperationException} if this method is used to compute the coordinates of the Earth
     *          since we observe these Planets from the Earth
     */
    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) throws UnsupportedOperationException {
        if(this.a == EARTH.a)
            throw new UnsupportedOperationException("We observe the Planets from the Earth so we don't care about its Coordinates");
        
        // PLANET INFO DEPENDING ON THE TIME
        // ecliptic heliocentric latitude
        double eclHelioLat = Math.asin(Math.sin(l(daysSinceJ2010)- omega)*Math.sin(i));
        // projection of the radius on ecliptic plan
        double eclRadius = r(daysSinceJ2010)*Math.cos(eclHelioLat);
        // heliocentric longitude projected on ecliptic plan
        double eclLong = Math.atan2(Math.sin(l(daysSinceJ2010) - omega)*Math.cos(i),
                Math.cos(l(daysSinceJ2010) - omega)) + omega;
        // ECLIPTIC COORDINATES
        double longitude = 0;
        if(this.a < EARTH.a) {
            longitude = Math.atan2(eclRadius*Math.sin(EARTH.l(daysSinceJ2010) - eclLong),
                             (EARTH.r(daysSinceJ2010) - eclRadius*Math.cos(EARTH.l(daysSinceJ2010) - eclLong)))
                        + Angle.TAU/2 + EARTH.l(daysSinceJ2010);
        } else {
            longitude = Math.atan2(EARTH.r(daysSinceJ2010)*Math.sin(eclLong - EARTH.l(daysSinceJ2010)),
                            (eclRadius - EARTH.r(daysSinceJ2010)*Math.cos(eclLong - EARTH.l(daysSinceJ2010)))) + eclLong;
        }
        double latitude = Math.atan(eclRadius*Math.tan(eclHelioLat)*Math.sin(longitude - eclLong)
                        /(EARTH.r(daysSinceJ2010)*Math.sin(eclLong - EARTH.l(daysSinceJ2010))));

        EclipticCoordinates position = EclipticCoordinates.of(Angle.normalizePositive(longitude), latitude);

        // ANGULAR SIZE AND MAGNITUDE
        // distance to earth
        double p = Math.sqrt(Math.pow(EARTH.r(daysSinceJ2010),2) + Math.pow(r(daysSinceJ2010), 2)
                - 2*EARTH.r(daysSinceJ2010)*r(daysSinceJ2010)*Math.cos(l(daysSinceJ2010)
                - EARTH.l(daysSinceJ2010))*Math.cos(eclHelioLat));
        // light portion of planet visible from earth
        double phase = (1 + Math.cos(longitude - l(daysSinceJ2010)))/2;
        double angularSize = tet0/p;
        double magnitude = V0 + 5*Math.log10(r(daysSinceJ2010)*p/Math.sqrt(phase));

        return new Planet(this.name, eclipticToEquatorialConversion.apply(position), (float)angularSize, (float)magnitude,
                (float)dMin, (float)dMax, (float)dAv);
    }
}
