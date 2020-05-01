package ch.epfl.rigel.math;

import java.util.Locale;;

/**
 * Represents a mathematical right open interval as
 * (ex: [a,b[ for low and high bounds a and b)
 *
 * @author Alexis FAVRE (310552)
 */
public final class RightOpenInterval extends Interval {
    /** [0, 2Pi]  (often used) */
    public static final RightOpenInterval ROInter_0To2Pi = new RightOpenInterval(0, Angle.TAU);
    /** [0, 360] (often used) */
    public static final RightOpenInterval ROInter_0To360 = new RightOpenInterval(0, 360);
    
    private RightOpenInterval(double low, double high) {
        super(low, high);
    }

    /**
     * Construct a {@code ClosedInterval} for the given low and high bounds as
     * [{@code low}, {@code high}[
     *
     * @param low the low bound of the interval
     * @param high the high bound of the interval
     * @return a new instance of {@code ClosedInterval} for the given bounds
     */
    public static RightOpenInterval of(double low, double high) {
        return new RightOpenInterval(low, high);
    }

    /**
     * Construct a symmetric {@code ClosedInterval} centered in 0 of the given size
     * @param size wanted for the interval
     * @return the corresponding new instance of {@code ClosedInterval}
     */
    public static RightOpenInterval symmetric(double size) {
        double demiSize = size/2d;
        return new RightOpenInterval(-demiSize, demiSize);
    }

    /**
     * Computes and return the corresponding reduced value of {@code v}
     * that is by definition always into the interval {@code this} for any given {@code v}
     *
     * @param v the value of which we want the reduced image in the interval {@code this}
     * @return {@code v} if the value is already in the interval and
     * the result of the reduce function if not (see the graph of the function for more info)
     */
    public double reduce(double v) {
        if(this.contains(v))
            return v;
        double a = v - low();
        double b = size();
        return low() + a - b*Math.floor(a/b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(double value) {
        return (low()<=value) && (value < high());
    }

    /**
     * @return a {@code String} view of {@code this} with the format
     * [a,b[
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%f, %f[", low(), high());
    }

}
