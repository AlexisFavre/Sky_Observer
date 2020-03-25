package ch.epfl.rigel.math;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * represent an Interval, not instanciable
 * @author Alexis FAVRE (310552)
 */
public abstract class Interval {
    
    private final double low;
    private final double high;

    protected Interval(double low, double high) throws IllegalArgumentException {
        checkArgument(low<high);
        this.low = low;
        this.high = high;
    }
    
    /** @return low bound of the interval */
    public double low() {
        return low;
    }
    
    /** @return high bound of the interval */
    public double high() {
        return high;
    }
    
    /** @return the size of the interval */
    public double size() {
        return Math.abs(high-low);
    }
    
    /** @return true if and only if v belong of the interval */
    public abstract boolean contains(double value);
    
    
    
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
