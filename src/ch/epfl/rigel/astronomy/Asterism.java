package ch.epfl.rigel.astronomy;

import java.util.ArrayList;
import java.util.List;

public final class Asterism { // TODO Verify Immuable

    private List<Star> stars;

    public Asterism(List<Star> stars) throws IllegalArgumentException { // TODO exception
        this.stars = new ArrayList<Star>();
        this.stars.addAll(stars);
    }

    /*public Asterism(Asterism other) {
        this.stars = new ArrayList<Star>();
        this.stars.addAll(other.stars);
    }*/

    public List<Star> stars() {
        List<Star> value = new ArrayList<Star>();
        value.addAll(stars);
        return value;
    }
}
