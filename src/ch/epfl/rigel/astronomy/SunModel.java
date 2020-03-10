package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

public enum SunModel implements CelestialObjectModel<Sun> { // TODO Verify immuable

    SUN();

    final double E_G = Angle.ofDeg(279.557208);
    final double W_G = Angle.ofDeg(283.112438);
    final double E = 0; // TODO Value

    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        double meanAnomaly = 2 * Math.PI/365.242191 * daysSinceJ2010 + E_G- W_G;
        double trueAnomaly = meanAnomaly + 2 * E * Math.sin(meanAnomaly);
        double angularSize = Angle.ofDeg(0.533128) * (1 + E * Math.cos(trueAnomaly)) / (1 - E * E); // TODO Verify if of deg ok
        double longEcl = trueAnomaly + W_G;
        EclipticCoordinates position = EclipticCoordinates.of(longEcl, 0);
        // TODO Transtype
        return new Sun(position, eclipticToEquatorialConversion.apply(position), (float)angularSize, (float)meanAnomaly);
    }
}
