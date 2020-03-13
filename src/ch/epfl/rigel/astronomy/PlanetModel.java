package ch.epfl.rigel.astronomy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

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

    public static List<PlanetModel> ALL = new ArrayList<>(List.copyOf(Arrays.asList(PlanetModel.values())));

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
        this.eps = Angle.ofDeg(degEps);
        this.w = Angle.ofDeg(degW);
        this.e = e;
        this.a = a;
        this.i = Angle.ofDeg(degI);
        this.omega = Angle.ofDeg(degOmega);
        this.tet0 = tet0;
        this.v0 = v0;
    }

    private double meanAnomaly(double daysSinceJ2010) {
        return Angle.TAU * daysSinceJ2010 / (365.242191*t) + e - w;
    }
    private double trueAnomaly(double daysSinceJ2010) {
        return meanAnomaly(daysSinceJ2010) + 2 * e * Math.sin(meanAnomaly(daysSinceJ2010));
    }
    private double radius(double daysSinceJ2010) {
        return a * (1 - e * e) / (1 - e * Math.cos(trueAnomaly(daysSinceJ2010)));
    }
    private double longPlan(double daysSinceJ2010) {
        return trueAnomaly(daysSinceJ2010) + w;
    }

    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        // planet info at the given time
        double latEclHelio = Math.asin(Math.sin((longPlan(daysSinceJ2010)- omega) * Math.sin(i)));
        double radiusProj = radius(daysSinceJ2010) * Math.cos(latEclHelio);
        double longPlanProj =
                Math.atan2(
                        Math.sin(longPlan(daysSinceJ2010) - omega) * Math.cos(i)
                                , Math.cos(longPlan(daysSinceJ2010) - omega)
                ) + omega;

        // Ecliptic coordinates
        double longitude = 0;
        if(this.a < EARTH.a) { // TODO what about earth? -> center of elliptic coordinates
            longitude =
                    Math.atan2(
                            radiusProj * Math.sin(EARTH.longPlan(daysSinceJ2010) - longPlanProj),
                             (EARTH.radius(daysSinceJ2010) - radiusProj * Math.cos(EARTH.longPlan(daysSinceJ2010) - longPlanProj))
                    ) + Angle.TAU/2 + EARTH.longPlan(daysSinceJ2010);
        } else {
            longitude =
                    Math.atan2(
                            EARTH.radius(daysSinceJ2010) * Math.sin(longPlanProj - EARTH.longPlan(daysSinceJ2010))
                            , (radiusProj - EARTH.radius(daysSinceJ2010) * Math.cos(longPlanProj - EARTH.longPlan(daysSinceJ2010)))
                    ) + longPlanProj;
        }

        double latitude =
                Math.atan2(
                        radiusProj * Math.tan(latEclHelio) * Math.sin(longitude - longPlanProj)
                                , (EARTH.radius(daysSinceJ2010) * Math.sin(longPlanProj - EARTH.longPlan(daysSinceJ2010)))
                );
        EclipticCoordinates position = EclipticCoordinates.of(longitude, latitude);

        // angular size and magnitude
        double p = Math.sqrt(Math.pow(EARTH.radius(daysSinceJ2010),2) 
                + Math.pow(radius(daysSinceJ2010),2) 
                - 2 * EARTH.radius(daysSinceJ2010) * radius(daysSinceJ2010)
                * Math.cos(longPlan(daysSinceJ2010) - EARTH.longPlan(daysSinceJ2010)) * Math.cos(latEclHelio)
        ); // TODO verify neg -> lancerait exception
        double phase = (1 + Math.cos(longitude - longPlan(daysSinceJ2010)))/2;
        double angularSize = Angle.ofDeg(Angle.ofArcsec(tet0)) / p;
        double magnitude = v0 + 5 * Math.log10(radius(daysSinceJ2010) * p / Math.sqrt(phase));

        // TODO transtype ???
        return new Planet(this.name, eclipticToEquatorialConversion.apply(position), (float)angularSize, (float)magnitude);
    }
}
