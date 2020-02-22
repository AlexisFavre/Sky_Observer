package ch.epfl.rigel.maths;

import java.util.Locale;

public final class ClosedInterval extends Interval {
    /**
     * 
     * @param low
     * (double) low bound of the interval
     * @param high
     * (double) high bound of the interval
     */
    private ClosedInterval(double low, double high) {
        super(low, high);
    }

    @Override
    public boolean contains(double value) {
        return (value>=low()) && (value<=high());
    }
    /**
     * 
     * @param low 
     * (double) low bound of the interval
     * @param high
     * (double) high bound of the interval
     * @return
     * (double) new Closed Interval
     */
    public static ClosedInterval of(double low, double high) {
        return new ClosedInterval(low, high);
    }
    
    /**
     * 
     * @param size
     * (double) size of the interval
     * @return
     * (ClosedInterval) new Closed and Symmetric interval center in 0
     */
    public static ClosedInterval symmetric(double size) {
        return new ClosedInterval(-size/2, size/2);
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
    public String toString() {
        return String.format(Locale.ROOT, "[%s,%s]", low(), high());
    }
}
