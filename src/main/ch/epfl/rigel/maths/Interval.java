package ch.epfl.rigel.maths;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * @author Alexis FAVRE (310552)
 *
 */
public abstract class Interval {
    
    private final double low;
    private final double high;

    protected Interval(double low, double high) {
        checkArgument(low<high);
        this.low = low;
        this.high = high;
    }
    
    public double low() {
        return low;
    }
    public double high() {
        return high;
    }
    public double size() {
        return Math.abs(high-low);
    }
    /**
     * @return 
     * true if and only if v belong of the interval
     */
    public abstract boolean contains(double value);
    
    
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
