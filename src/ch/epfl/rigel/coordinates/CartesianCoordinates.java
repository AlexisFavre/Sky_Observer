package ch.epfl.rigel.coordinates;

import java.util.Locale;

/**
 * 
 * @author Alexis FAVRE (310552)
 *
 */
public final class CartesianCoordinates {

    private final double abscissa;
    private final double ordinate;

    private CartesianCoordinates(double abscissa, double ordinate) {
        this.abscissa = abscissa;
        this.ordinate = ordinate;
    }
        
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
    public String toString() {
        return String.format(Locale.ROOT, "(x=%.4f°, y=%.4f°)", x(), y());
    }
    
    /**
     * always throws UnsupportedOperationException
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }
    
    
    /**
     * always throws UnsupportedOperationException
     */
    @Override 
    public final boolean equals(Object interval) {
        throw new UnsupportedOperationException();
    }

    }
