package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

/**
 * Model that represents a {@code CelestialObject} state at a given time
 * Used to update a the {@code CelestialObject} corresponding to the model
 *
 * @author Augustin ALLARD (299918)
 */
public interface CelestialObjectModel <O> {

    /**
     * This gives the object O in the state corresponding to the given parameters
     *
     * @param daysSinceJ2010 the days between J2010 and the time at which we want the object
     * @param eclipticToEquatorialConversion a conversion corresponding to the given time
     * @return The object in the corresponding state
     */
    O at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion);
}
