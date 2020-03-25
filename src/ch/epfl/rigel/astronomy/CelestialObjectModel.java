package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

/**
 * to models an celestial object at a given time
 * @author Augustin ALLARD (299918)
 */
public interface CelestialObjectModel <O> {

    /**
     * This gives the object O in the state corresponding to the given parameters
     *
     * @param daysSinceJ2010 the days between J2010 and the time at which we want the object
     * @param eclipticToEquatorialConversion a conversion corresponding to the given time
     * @return (O)
     *  The object in the corresponding state
     */
    O at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion);
}
