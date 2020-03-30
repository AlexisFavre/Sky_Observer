package ch.epfl.rigel.astronomy;

import static ch.epfl.rigel.Preconditions.checkArgument;

import java.util.Objects;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * Represents a generic celestial objects and its information like magnitude, angular size and position
 * 
 * @author Alexis FAVRE (310552)
 * @see Planet
 * @see Sun
 * @see Moon
 * @see Star
 */
public abstract class CelestialObject {

    private final String name;
    private final EquatorialCoordinates equatorialPos;
    private final float angularSize;
    private final float magnitude;

    CelestialObject(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude)
            throws IllegalArgumentException {
        checkArgument(angularSize>=0);
        this.name = Objects.requireNonNull(name); // throws NullPointerException if null
        this.equatorialPos = Objects.requireNonNull(equatorialPos); // same
        this.angularSize = angularSize;
        this.magnitude = magnitude;
    
    }

    /**
     *
     * @return the name
     */
    public String name() {
        return name;
    }

    /**
     *
     * @return the equatorialPos
     */
    public EquatorialCoordinates equatorialPos() {
        return equatorialPos;
    }

    /**
     *
     * @return the angularSize in radians
     */
    public double angularSize() {
        return angularSize;
    }

    /**
     *
     * @return the magnitude in [-30,6] the main part of the time
     */
    public double magnitude() {
        return magnitude;
    }
    
    /**
     *
     * @return object information as String for user
     */
    public String info() {
        return name;
    }

    /**
     *
     * @return a {@code String} view of {@code this} giving the object information
     */
    @Override
    public String toString() {
        return info();
    }

}
