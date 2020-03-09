package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

public enum SunModel implements CelestialObjectModel<Sun> { // TODO Verify immuable
    SUN();

    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        return new Sun();
    }
}
