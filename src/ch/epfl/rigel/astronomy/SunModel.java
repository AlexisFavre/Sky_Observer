package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

/**
 * Model that compute the Sun state at a given time
 * Used to update the Sun
 *
 * @author Augustin ALLARD (299918)
 */
public enum SunModel implements CelestialObjectModel<Sun> {

    SUN;

    // Longitude of sun at J2010 in radians
    private final double E_G = Angle.ofDeg(279.557208);
    // Longitude of sun at perigee in radians
    private final double W_G = Angle.ofDeg(283.112438);
    // Eccentricity of the ellipse sun/earth no unity
    private final double E   = 0.016705;

    // sun mean anomaly at given time
    protected double meanAnomaly(double daysSinceJ2010) {
        return Angle.TAU/365.242191*daysSinceJ2010 + E_G - W_G;
    }

    // sun longitude projected on ecliptic plan at given time
    protected double longEcliptic(double daysSinceJ2010) {
        return Angle.normalizePositive(meanAnomaly(daysSinceJ2010) + 2*E*Math.sin(meanAnomaly(daysSinceJ2010)) + W_G);
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        double meanAnomaly = meanAnomaly(daysSinceJ2010);
        double trueAnomaly = meanAnomaly + 2*E*Math.sin(meanAnomaly);
        double angularSize = Angle.ofDeg(0.533128)*(1 + E*Math.cos(trueAnomaly))/(1 - E*E);
        EclipticCoordinates position = EclipticCoordinates.of(longEcliptic(daysSinceJ2010), 0);
        return new Sun(position, eclipticToEquatorialConversion.apply(position), (float)angularSize, (float)meanAnomaly);
    }
}
