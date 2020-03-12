package ch.epfl.rigel.astronomy;

import java.util.ArrayList;
import java.util.List;

public final class Asterism {

    final private List<Star> stars;

    public Asterism(List<Star> stars) throws IllegalArgumentException {
        if(!stars.isEmpty()) {
            this.stars = new ArrayList<Star>();
            this.stars.addAll(stars);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public List<Star> stars() {
        List<Star> value = new ArrayList<Star>();
        value.addAll(stars);
        return value;
    }
}
