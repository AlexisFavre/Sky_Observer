package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

/**
 * @author Augustin ALLARD (299918)
 */
public enum SunModel implements CelestialObjectModel<Sun> {

    SUN();

    private final double E_G = Angle.ofDeg(279.557208); // longitude of sun at J2010 in radians
    private final double W_G = Angle.ofDeg(283.112438); // longitude of sun at perigee in radians
    private final double E = 0.016705; // eccentricity of the ellipse sun/earth no unity

    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        double meanAnomaly = daysSinceJ2010 * Angle.TAU/365.242191 + E_G - W_G;
        double trueAnomaly = meanAnomaly + 2 * E * Math.sin(meanAnomaly);
        double angularSize = Angle.ofDeg(0.533128) * Math.round((1 + E * Math.cos(trueAnomaly)) / (1 - E * E));
        double longEcl = Angle.normalizePositive(trueAnomaly + W_G);
        EclipticCoordinates position = EclipticCoordinates.of(longEcl, 0);
        return new Sun(position, eclipticToEquatorialConversion.apply(position), (float)angularSize, (float)meanAnomaly);
    }
    /**
     * 
     * @param daysSinceJ2010
     * @return
     * meanAnomaly (used for MoonModel too)
     */
    protected double meanAnomaly(double daysSinceJ2010) {
        return Angle.TAU/365.242191 * daysSinceJ2010 + E_G- W_G;
    }
    
    /**
     * 
     * @param daysSinceJ2010
     * @return
     * longitude ecliptic of the sun(used for the MoonModel too)
     */
    protected double longEcliptic(double daysSinceJ2010) {
        return meanAnomaly(daysSinceJ2010) + 2 * E * Math.sin(meanAnomaly(daysSinceJ2010)) + W_G;
    }
}
