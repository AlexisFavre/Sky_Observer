package ch.epfl.rigel.astronomy;

import java.util.List;
import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Describe an asterism which is a group of stars
 *
 * @author Augustin ALLARD (299918)
 */
public final class Asterism {

    private final List<Star> stars;

    /**
     * @param stars forming the asterism
     * @throws IllegalArgumentException if the provided {@code List} is empty
     */
    public Asterism(List<Star> stars) throws IllegalArgumentException {
        checkArgument(!stars.isEmpty());
        this.stars = List.copyOf(stars);
    }

    /**
     * @return stars composing {@code this}
     */
    public List<Star> stars() {
        return stars;
    }
    
    /**
     * @return hash of the sum of the hash of the stars
     */
    @Override
    public int hashCode() {
        int i = 0;
        for (Star star : stars) {
            i += star.hashCode();
        }
        return Integer.hashCode(i);
    }
}
