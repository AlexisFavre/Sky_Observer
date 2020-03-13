package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

public enum SunModel implements CelestialObjectModel<Sun> {

    SUN();

    private final double E_G = Angle.ofDeg(279.557208); //longitude of sun at J2010
    private final double W_G = Angle.ofDeg(283.112438); // longitude of sun at perige
    private final double E = 0.016705; // eccentricity of the ellipse sun/earth

    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        double meanAnomaly = daysSinceJ2010 * Angle.TAU/365.242191 + E_G - W_G;
        double trueAnomaly = meanAnomaly + 2 * E * Math.sin(meanAnomaly);
        double angularSize = Angle.ofDeg(0.533128) * Math.round((1 + E * Math.cos(trueAnomaly)) / (1 - E * E));
        double longEcl = trueAnomaly + W_G;
        EclipticCoordinates position = EclipticCoordinates.of(longEcl, 0);
        
        return new Sun(position, eclipticToEquatorialConversion.apply(position), (float)angularSize, (float)meanAnomaly);
    }
}
