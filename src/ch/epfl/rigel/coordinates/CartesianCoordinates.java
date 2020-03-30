package ch.epfl.rigel.coordinates;

import java.util.Locale;

/**
 * Represents the cartesian coordinates System
 *
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
     * @param x abscissa
     * @param y ordinate
     * @return new {@code CartesianCoordinates} instance with the given {@code x} and {@code y}
     */
    public static CartesianCoordinates of(double x, double y) {
        return new CartesianCoordinates(x,y);
        }

    /**
     * Gives the distance between the given point and {@code this}
     *
     * @param other the other point
     * @return the distance between the two points, infinite if other is null
     */
    public double distance(CartesianCoordinates other) {
        if(other == null)
            return Double.POSITIVE_INFINITY;
        return Math.hypot(x() - other.x(), y() - other.y());
    }

    /**
     *
     * @return the abscissa
     */
    public double x() {
        return abscissa;
    }

    /**
     *
     * @return the ordinate
     */
    public double y() {
        return ordinate;
    }

    /**
     * Always throw exception
     * {@code conversion.hashCode()} is forbidden
     *
     * @throws UnsupportedOperationException in all conditions
     */
    /*@Override
    public final int hashCode() throws UnsupportedOperationException { //TODO Hascode
        throw new UnsupportedOperationException();
    }*/

    /**
     * Always throw exception
     * {@code conversion.equals()} is forbidden
     *
     * @throws UnsupportedOperationException in all conditions
     */
    @Override
    public final boolean equals(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @return a {@code String} view of {@code this} with the format
     * (x= a, y= b)
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(x=%.4f, y=%.4f)", x(), y());
    }
}
