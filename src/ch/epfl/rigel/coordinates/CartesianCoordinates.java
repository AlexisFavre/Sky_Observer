package ch.epfl.rigel.coordinates;

import java.util.Locale;

/**
 * represent the Cartesian coordinates System
 * @author Alexis FAVRE (310552)
 */
public final class CartesianCoordinates {

    private final double abscissa;
    private final double ordinate;

    private CartesianCoordinates(double abscissa, double ordinate) {
        this.abscissa = abscissa;
        this.ordinate = ordinate;
    }
    
    /**
     * @param x (double) abscissa
     * @param y (double) ordinate
     * @return
     * new CartesianCoordinates of these characteristics
     */
    public static CartesianCoordinates of(double x, double y) {
        return new CartesianCoordinates(x,y);
        }

    /**
     * @return the abscissa
     */
    public double x() {
        return abscissa;
    }

    /**
     * @return the ordinate
     */
    public double y() {
        return ordinate;
    }
    
    @Override
    /** @return a representation of the coordinates */
    public String toString() {
        return String.format(Locale.ROOT, "(x=%.4f, y=%.4f)", x(), y());
    }
    
    @Override
    /** always throws UnsupportedOperationException */
    public final int hashCode() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override 
    /** always throws UnsupportedOperationException */
    public final boolean equals(Object interval) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    }
