/**
 * 
 */
package ch.epfl.rigel.maths;

import java.util.Locale;

/**
 * @author Alexis FAVRE (310552)
 *
 */
public final class RightOpenInterval extends Interval {

    /**
     * @param low
     * @param high
     */
    private RightOpenInterval(double low, double high) {
        super(low, high);
    }

    @Override
    public boolean contains(double value) {
        return (low()<=value) && (value < high());
    }
    
    /**
     * 
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
     * 
     * @param size
     * size of the interval
     * @return
     * new Closed and Symmetric interval center in 0
     */
    public static RightOpenInterval symmetric(double size) {
        return new RightOpenInterval(-size/2, size/2);
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%s, %s[", low(), high());
    }
    
    /**
     * reduced function of the interval
     * @param v
     * (double) value
     * @return
     * image of the value v by the reduced function
     */ 
    public double reduce(double v) {
        return low() + Math.floorMod((int)(v-low()), (int)(high()-low()));
    }

}
