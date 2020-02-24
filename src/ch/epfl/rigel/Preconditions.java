package ch.epfl.rigel;



/**
 * 
 * @author Alexis FAVRE (310552)
 * container for 2 functions
 * class non instanciable
 */
public final class Preconditions {

    private Preconditions() {}
    
    /**
     * throw exception is the argument is false
     * @param isTrue
     * the argument that has to be checked
     */
    public static void checkArgument(boolean isTrue) {
        if(!isTrue) {
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * 
     * @param interval
     * @param value
     * @return
     * return the value if it is contained in this interval
     * otherwise throws IllegalArgumentException
     */
    public static double checkInInterval(ch.epfl.rigel.math.Interval interval, double value) {
        if(interval.contains(value)) {
            return value;
        }
        else {
            throw new IllegalArgumentException();
        }
    }

}
