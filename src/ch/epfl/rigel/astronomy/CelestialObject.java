package ch.epfl.rigel.astronomy;

import static ch.epfl.rigel.Preconditions.checkArgument;

import java.util.Objects;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

public abstract class CelestialObject {

    private String name;
    private EquatorialCoordinates equatorioalPos;
    private float angularSize;
    private float magnitude;

    CelestialObject(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        checkArgument(angularSize<=0);
        if(name == null || equatorialPos == null) {
            throw new NullPointerException();
        }
        
        this.name = name;
        this.equatorioalPos = Objects.requireNonNull(equatorialPos);
        this.angularSize = angularSize;
        this.magnitude = magnitude;
    
    }

    /**
     * @return the name
     */
    public String name() {
        return name;
    }

    /**
     * @return the equatorioalPos
     */
    public EquatorialCoordinates equatorioalPos() {
        return equatorioalPos;
    }

    /**
     * @return the angularSize
     */
    public double angularSize() {
        return angularSize;
    }

    /**
     * @return the magnitude
     */
    public double magnitude() {
        return magnitude;
    }
    
    /**
     * text that user will see
     * @return (String)
     */
    public String info() {
        return name;
    }
    
    /**
     * text that user will see
     * @return (String)
     */
    @Override
    public String toString() {
        return info();
    }

}
