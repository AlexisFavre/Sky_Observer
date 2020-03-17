package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

public enum SunModel implements CelestialObjectModel<Sun> {

    SUN();

    final double E_G = Angle.ofDeg(279.557208);
    final double W_G = Angle.ofDeg(283.112438);
    final double E = 0.016705; // TODO Veriffy Unity of Value actually not degrees (no unity)

    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        double angularSize = Angle.ofDeg(0.533128) * (1 + E * Math.cos(longEclipticofSun(daysSinceJ2010)-W_G)) / (1 - E * E); // TODO Verify if of deg ok
        EclipticCoordinates position = EclipticCoordinates.of(longEclipticofSun(daysSinceJ2010), 0);
        // TODO Transtype
        return new Sun(position, eclipticToEquatorialConversion.apply(position), (float)angularSize, (float)meanAnomalyOfSun(daysSinceJ2010));
    }
    /**
     * 
     * @param daysSinceJ2010
     * @return
     * meanAnomaly (used for MoonModel too)
     */
    protected double meanAnomalyOfSun(double daysSinceJ2010) {
        return Angle.TAU/365.242191 * daysSinceJ2010 + E_G- W_G;
    }
    
    /**
     * 
     * @param daysSinceJ2010
     * @return
     * longitude ecliptic of the sun(used for the MoonModel too)
     */
    protected double longEclipticofSun(double daysSinceJ2010) {
        return meanAnomalyOfSun(daysSinceJ2010) + 2 * E * Math.sin(meanAnomalyOfSun(daysSinceJ2010)) + W_G;
    }
}
