package ch.epfl.rigel.coordinates;

import java.util.Locale;

public final class CartesianCoordinates {

    private double abscissa;
    private double ordinate;

    private CartesianCoordinates(double abscissa, double ordinate) {
        this.abscissa = abscissa;
        this.ordinate = ordinate;
    }
        
    public CartesianCoordinates of(double x, double y) {
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
