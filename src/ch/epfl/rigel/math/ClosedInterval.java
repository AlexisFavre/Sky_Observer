package ch.epfl.rigel.math;

import java.util.Locale;

/**
 * Represents a mathematical closed interval as
 * (ex: [a,b] for low and high bounds a and b)
 *
 * @author Alexis FAVRE (310552)
 */
public final class ClosedInterval extends Interval {
    
    /**
     * [-Pi/2, Pi/2]    (often used)
     */
    public static final ClosedInterval CSymmetricInterOfSizePi = symmetric(Math.PI);
    
    /**
     * [-90, 90]      (often used)
     */
    public static final ClosedInterval CSymmetricInterOfSize180 = symmetric(180);

    private ClosedInterval(double low, double high) {
        super(low, high);
    }

    /**
     * Construct a {@code ClosedInterval} for the given low and high bounds as
     * [{@code low}, {@code high}]
     *
     * @param low the low bound of the interval
     * @param high the high bound of the interval
     * @return a new instance of {@code ClosedInterval} for the given bounds
     */
    public static ClosedInterval of(double low, double high) {
        return new ClosedInterval(low, high);
    }
    
    /**
     * Construct a symmetric {@code ClosedInterval} centered in 0 of the given size
     * @param size wanted for the interval
     * @return the corresponding new instance of {@code ClosedInterval}
     */
    public static ClosedInterval symmetric(double size) {
        double demiSize = size/2d;
        return new ClosedInterval(-demiSize, demiSize);
    }

    
    /**
     * Computes and return the corresponding clipped value of {@code v}
     * that is by definition always into the interval {@code this} for any given {@code v}
     *
     * @param v the value of which we want the clipped image in the interval {@code this}
     * @return {@code v} if the value is already in the interval and the low or high bounds
     * if {@code v} is respectively too small or too big
     */
    public double clip(double v) {
        if(v <= low()) {
            return low();
        } else {
            return Math.min(v, high());
        }
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public boolean contains(double value) {
        return (value>=low()) && (value<=high());
    }

    /**
     *
     * @return a {@code String} view of {@code this} with the format
     * [a,b]
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%f,%f]", low(), high());
    }
}
