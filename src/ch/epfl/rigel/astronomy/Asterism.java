package ch.epfl.rigel.astronomy;

import java.util.List;
import java.util.Locale;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Describe an asterism which is a group of stars
 *
 * @author Augustin ALLARD (299918)
 */
public final class Asterism {

    private final List<Star> stars;

    /**
     *
     * @param stars forming the asterism
     * @throws IllegalArgumentException if the provided {@code List} is empty
     */
    public Asterism(List<Star> stars) throws IllegalArgumentException {
        checkArgument(!stars.isEmpty());
        this.stars = List.copyOf(stars);
    }

    /**
     *
     * @return stars composing {@code this}
     */
    public List<Star> stars() {
        return stars;
    }

    /**
     * Compare Asterism stars equality one by one
     *
     * @param a the asterism that should be compare to {@code this}
     * @return {@code true} if every stars of the asterisms are equals
     */
    public final boolean equals(Asterism a) {
        return stars().containsAll(a.stars()) && a.stars().containsAll(stars());
    }

    /**
     *
     * @return a {@code String} view of {@code this} with the format
     * ASTERISM
     * star1 display
     * ...
     * star2 display
     * ...
     *
     */
    @Override
    public String toString() {
        StringBuilder asterismDisplay = new StringBuilder("ASTERISM\n");
        for(Star s: stars()) {
            asterismDisplay.append(s.toString()).append("\n...\n");
        }
        return asterismDisplay.toString();
    }
}
