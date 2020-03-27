package ch.epfl.rigel.math;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Non instantiable class
 *
 * Represents a mathematical interval
 * Provides size computation and value memberships verification
 *
 * @author Alexis FAVRE (310552)
 * @see RightOpenInterval
 * @see ClosedInterval
 */
public abstract class Interval {
    
    private final double low;
    private final double high;

    protected Interval(double low, double high) throws IllegalArgumentException {
        checkArgument(low<high);
        this.low = low;
        this.high = high;
    }
    
    /**
     *
     * @return low bound of the interval
     */
    public double low() {
        return low;
    }
    
    /**
     *
     * @return high bound of the interval
     */
    public double high() {
        return high;
    }
    
    /**
     *
     * @return the size of the interval
     */
    public double size() {
        return Math.abs(high-low);
    }
    
    /**
     *
     * @return the boolean value {@code True} if and only if {@code value}
     * belongs to the interval {@code this}
     */
    public abstract boolean contains(double value);


    /**
     * Always throw exception because of floating point representation
     * of low and high bounds
     * {@code interval.equals(another)} is forbidden
     *
     * @throws UnsupportedOperationException in all conditions
     */
    @Override
    public final boolean equals(Object interval) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throw exception because of floating point representation
     * of low and high bounds
     * {@code interval.hashCode()} is forbidden
     *
     * @throws UnsupportedOperationException in all conditions
     */
    @Override
    public final int hashCode() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
