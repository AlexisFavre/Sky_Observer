package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * Describe a planet at a given location
 * The characteristics of the Planet are computed by a {@code PlanetModel}
 *
 * @author Alexis FAVRE (310552)
 */
public final class Planet extends CelestialObject{

    /**
     * @param name of the Planet
     * @param equatorialPos  of the Planet
     * @param angularSize  of the Planet
     * @param magnitude  of the Planet
     */
    public Planet(String name, EquatorialCoordinates equatorialPos, float angularSize,
            float magnitude, float dMin, float dMax, float dAv) {
        super(name, equatorialPos, angularSize, magnitude, dMin, dMax, dAv);
    }
}
