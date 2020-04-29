package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

/**
 * Model that compute the Sun state at a given time
 * Used to update the Sun
 *
 * @author Alexis FAVRE (310552)
 */
public enum MoonModel implements CelestialObjectModel<Moon>{

    MOON;

    // CONSTANTS FOR MOON AT J2010
    // mean longitude
    private final static double l0 = Angle.ofDeg(91.929336);
    // mean longitude at perigee
    private final static double P0 = Angle.ofDeg(130.143076);
    // longitude at ascendant node
    private final static double N0 = Angle.ofDeg(291.682547);
    // orbit eccentricity no unity
    private final static double e = 0.0549;
    // orbit inclination
    private final static double i  = Angle.ofDeg(5.145396);

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        // mean orbital longitude without correction
        double l  = Angle.ofDeg(13.1763966)*daysSinceJ2010 + l0;
        // mean anomaly without correction
        double M = l - Angle.ofDeg(0.1114041)*daysSinceJ2010 - P0;
        // mean ascendant node longitude without corrections
        double N = N0 - Angle.ofDeg(0.0529539)*daysSinceJ2010;

        // MEAN ANOMALY COMPUTATION WITH CORRECTION DUE TO SUN INFLUENCE
        // constants for mean anomaly corrections due to sun influence on trajectory
        double Ev = Angle.ofDeg(1.2739)*Math.sin(2*(l - SunModel.SUN.longEcliptic(daysSinceJ2010)) - M);
        double Ae = Angle.ofDeg(0.1858)*Math.sin(SunModel.SUN.meanAnomaly(daysSinceJ2010));
        double A3 = Angle.ofDeg(0.37)*Math.sin(SunModel.SUN.meanAnomaly(daysSinceJ2010));

        /*-----*/ double meanAnomaly = M + Ev - Ae - A3;

        // ORBITAL LONGITUDE COMPUTATION WITH CORRECTION DUE TO SUN INFLUENCE
        // constants for longitude corrections due to sun influence on trajectory
        double Ec = Angle.ofDeg(6.2886)*Math.sin(meanAnomaly);
        double A4 = Angle.ofDeg(0.214)*Math.sin(2*meanAnomaly);
        // adjusted longitude
        double l_   = l + Ev + Ec - Ae + A4;
        // correction term depending on adjusted longitude
        double V  = Angle.ofDeg(0.6583)*Math.sin(2*(l_-SunModel.SUN.longEcliptic(daysSinceJ2010)));

        /*-----*/ double orb_longitude = l_ + V;
        
        // ECLIPTIC POSITION COMPUTATION
        // mean ascendant node longitude with correction due to sun influence
        double asc_nod_long = N - Angle.ofDeg(0.16)*Math.sin(SunModel.SUN.meanAnomaly(daysSinceJ2010));
        double eclipticLong = Math.atan2(Math.sin(orb_longitude - asc_nod_long)*Math.cos(i),
                                         Math.cos(orb_longitude - asc_nod_long)) + asc_nod_long;
        double eclipticLat  = Math.asin(Math.sin(orb_longitude - asc_nod_long)*Math.sin(i));

        /*-----*/ EclipticCoordinates position = EclipticCoordinates.of(Angle.normalizePositive(eclipticLong), eclipticLat);
        
        // PHASE AND ANGULAR SIZE COMPUTATION
        // length of the semi major axis of the orbit
        double p = (1- e * e) / (1 + e *Math.cos(meanAnomaly + Ec));
        /*-----*/ double phase = (1 - Math.cos(orb_longitude - SunModel.SUN.longEcliptic(daysSinceJ2010))) / 2;
        /*-----*/ double angularSize = Angle.ofDeg(0.5181) / p;
        
        return new Moon(eclipticToEquatorialConversion.apply(position), (float)angularSize, (float)0, (float)phase);
    }
}
