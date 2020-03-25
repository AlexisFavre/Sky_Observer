package ch.epfl.rigel.math;

import java.util.Locale;

/**
 * represent an right open Interval, not instanciable
 * @author Alexis FAVRE (310552)
 */
public final class RightOpenInterval extends Interval {
    
    private RightOpenInterval(double low, double high) {
        super(low, high);
    }

    @Override
    /** @return true if and only if v belong of the interval */
    public boolean contains(double value) {
        return (low()<=value) && (value < high());
    }
    
    /**
     * to construct an right open Interval
     * @param low 
     * low bound of the interval
     * @param high
     * high bound of the interval
     * @return
     * new Closed Interval
     */
    public static RightOpenInterval of(double low, double high) {
        return new RightOpenInterval(low, high);
    }
    
    /**
     * to construct an Symmetric right open Interval center in 0
     * @param size
     * size of the interval
     * @return
     * new Closed and Symmetric interval center in 0
     */
    public static RightOpenInterval symmetric(double size) {
        return new RightOpenInterval(-size/2, size/2);
    }
    
    @Override
    /** @return an representation of the interval */
    public String toString() {
        return String.format(Locale.ROOT, "[%s, %s[", low(), high());
    }
    
    /**
     * reduce the value on this interval
     * @param v
     * (double) value
     * @return
     * the reduced value
     */ 
    public double reduce(double v) {
        double a = v - low();
        double b = high() - low();
        //return low() + Math.floorMod((int)(v-low()), (int)(high()-low()));
        return low() + a - b*Math.floor(a/b);
    }

}
