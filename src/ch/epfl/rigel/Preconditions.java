package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

/**
 * container for 2 functions used to check arguments
 * class non instanciable
 * @author Alexis FAVRE (310552)
 */
public final class Preconditions {

    private Preconditions() {}
    
    /**
     * check given argument (isTrue)
     * @param isTrue 
     * @throws IllegalArgumentException if the argument(isTrue) is False
     */
    public static void checkArgument(boolean isTrue) throws IllegalArgumentException {
        if(!isTrue) {
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * check that the value is contained in this interval
     * @param interval
     * @param value
     * @return return the value if it is contained in this interval
     * @throws IllegalArgumentException if this interval does not contain this value
     */
    public static double checkInInterval(Interval interval, double value) throws IllegalArgumentException {
        if(interval.contains(value)) {
            return value;
        }
        else {
            throw new IllegalArgumentException();
        }
    }

}
