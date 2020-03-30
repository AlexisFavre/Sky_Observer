package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

/**
 * Non instantiable class containing only {@code static} methods
 *
 * Used to check arguments validity or verifies membership in an interval
 * It typically verify by not throwing {@code IllegalArgumentException} that
 * a {@code boolean} argument condition proposition is verified or that
 * a value is indeed in an interval
 *
 * @author Alexis FAVRE (310552)
 */
public final class Preconditions {

    private Preconditions() {}
    
    /**
     * Check that the boolean proposition {@code isTrue} corresponding to
     * a method argument restriction is true
     *
     * @param isTrue method argument restriction (ex: {@code argument > 0})
     * @throws IllegalArgumentException if {@code isTrue} is false
     */
    public static void checkArgument(boolean isTrue) throws IllegalArgumentException {
        if(!isTrue) {
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * Check that the {@code value} is in the {@code interval}
     *
     * @param interval that should contain the {@code value}
     * @param value that we want to verify the {@code interval} membership
     * @return return the {@code value} if it is contained
     * @throws IllegalArgumentException if the {@code value} is not contained
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
