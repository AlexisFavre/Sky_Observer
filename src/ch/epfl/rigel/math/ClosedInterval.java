package ch.epfl.rigel.math;

import java.util.Locale;

/**
 * represent an closed Interval, not instanciable
 * @author Alexis FAVRE (310552)
 */
public final class ClosedInterval extends Interval {

    private ClosedInterval(double low, double high) {
        super(low, high);
    }

    /**
     * to construct an Closed Interval
     * @param low 
     * (double) low bound of the interval
     * @param high
     * (double) high bound of the interval
     * @return new Closed Interval
     */
    public static ClosedInterval of(double low, double high) {
        return new ClosedInterval(low, high);
    }
    
    /**
     * to construct an Symmetric Closed Interval center in 0
     * @param size
     * (double) size of the interval
     * @return
     * (ClosedInterval) new Closed and Symmetric interval center in 0
     */
    public static ClosedInterval symmetric(double size) {
        return new ClosedInterval(-size/2, size/2);
    }
    
    @Override
    /** @return true if and only if v belong of the interval */
    public boolean contains(double value) {
        return (value>=low()) && (value<=high());
    }
    
    /**
     * clip values for this interval
     * @param v
     * (double) value
     * @return
     * (double) image of the clip function
     */
    public double clip(double v) {
        if(v<=low()) {
            return low();
        } else {
            if(v>=high()) {
                return high();
            } else { return v; }
        }
    }

    @Override
    /** @return an representaion of the interval */
    public String toString() {
        return String.format(Locale.ROOT, "[%s,%s]", low(), high());
    }
}
