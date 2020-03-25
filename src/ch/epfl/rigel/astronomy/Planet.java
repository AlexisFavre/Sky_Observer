package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * describe a planet
 * the characteristics of the Planet are calculate in PlanetModel
 * @author Alexis FAVRE (310552)
 *
 */
public final class Planet extends CelestialObject{

    /**
     * create a Planet with the following characteristics
     * @param name of the Planet
     * @param equatorialPos  of the Planet
     * @param angularSize  of the Planet
     * @param magnitude  of the Planet
     */
    public Planet(String name, EquatorialCoordinates equatorialPos, float angularSize,
            float magnitude) {
        super(name, equatorialPos, angularSize, magnitude);
    }
}
