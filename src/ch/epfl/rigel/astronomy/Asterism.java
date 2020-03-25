package ch.epfl.rigel.astronomy;

import java.util.List;
import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Models an asterism which is a group of stars
 * @author Augustin ALLARD (299918)
 */
public final class Asterism {

    private final List<Star> stars;

    /**
     * create an asterism 
     * @param (List) stars
     * @throws IllegalArgumentException if the List of Stars is empty
     */
    public Asterism(List<Star> stars) throws IllegalArgumentException {
        checkArgument(!stars.isEmpty());
        this.stars = List.copyOf(stars);
    }

    /**
     * @return list of the stars which composed the asterism
     */
    public List<Star> stars() {
        return stars;
    }
}
