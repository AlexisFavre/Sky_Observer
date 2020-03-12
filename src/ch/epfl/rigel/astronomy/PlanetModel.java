package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

import java.util.ArrayList;
import java.util.List;

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

    final public static PlanetModel[] ALL = PlanetModel.values(); // TODO Verify if is considered as a list

    final private String name;
    final private double t;
    final private double eps;
    final private double w;
    final private double e;
    final private double a;
    final private double i;
    final private double omega;
    final private double tet0;
    final private double v0;

    PlanetModel(String name, double t, double degEps, double degW, double e,
                double a, double degI, double degOmega, double tet0, double v0) {
        this.name = name;
        this.t = t;
        eps = Angle.ofDeg(degEps);
        w = Angle.ofDeg(degW);
        this.e = e;
        this.a = a;
        i = Angle.ofDeg(degI);
        omega = Angle.ofDeg(degOmega);
        this.tet0 = tet0;
        this.v0 = v0;
    }

    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        double meanAnomaly = 2 * Math.PI/365.242191 * daysSinceJ2010/t + e- w;
        double trueAnomaly = meanAnomaly + 2 * e * Math.sin(meanAnomaly);
        double radius = a * (1 - e*e)/(1 - e * Math.cos(trueAnomaly));
        double longPlan = trueAnomaly + w;
        double latEcl = Math.asin(Math.sin((longPlan- omega) * Math.sin(i))); 
        double angularSize = Angle.ofDeg(0.533128) * (1 + E * Math.cos(trueAnomaly)) / (1 - E * E); // TODO Verify if of deg ok
        double longEcl = trueAnomaly + W_G;
        EclipticCoordinates position = EclipticCoordinates.of(longEcl, 0);
        return new Planet(this.name, );
    }
}
