package ch.epfl.rigel.astronomy;

import static ch.epfl.rigel.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Alexis FAVRE (310552)
 *
 */
public final class StarCatalogue {

    private final List<Star> stars;
    private final Map<Asterism, List<Integer>> catalogue;

    public StarCatalogue(List<Star> stars, List<Asterism> asterisms) {
        for (Asterism asterism : asterisms) {
            checkArgument(stars.containsAll(asterism.stars()));
        }
        
        this.stars = new ArrayList<Star>(List.copyOf(stars));
        this.catalogue = new HashMap<Asterism, List<Integer>>();
        
        for (Asterism asterism : asterisms) {
            List<Integer> indexsOfThisAsterism = new ArrayList<>();
            for(Star star : asterism.stars()) {
                indexsOfThisAsterism.add(this.stars.indexOf(star));
            }
            catalogue.put(asterism, indexsOfThisAsterism);
        }
    }

    /**
     * @return the stars
     */
    public List<Star> stars() {
        return stars;
    }

    /**
     * @return the asterisms
     */
    public Set<Asterism> asterisms() {
        return catalogue.keySet();
    }
    
    /**
     * 
     * @param asterism
     * @return
     * list of the indexes of the star in this asterism
     */
    public List<Integer> asterismIndices(Asterism asterism){
        return catalogue.get(asterism);
    }
//================================================================================================
    
    public final static class Builder {
        private List<Star> stars;
        private List<Asterism> asterisms;
        
        public Builder() {
            this.stars     = new ArrayList<Star>();
            this.asterisms = new ArrayList<Asterism>();
        }
        
        /**
         * add this star to the catalog in construction
         * @param star
         * @return this
         */
        Builder addStar(Star star) {
            stars.add(star);
            return this;
        }
        
        /**
         * @return unmodifiable view of the list of the stars in the catalog
         */
        List<Star> stars(){
            return Collections.unmodifiableList(stars);
        }
        
        /**
         * add this asterism to the catalog in construction
         * @param asterism
         * @return this
         */
        Builder addAsterism(Asterism asterism) {
            asterisms.add(asterism);
            return this;
        }
        
        /**
         * @return unmodifiable view of the list of the asterisms in the catalog
         */
        List<Asterism> asterisms(){
            return Collections.unmodifiableList(asterisms);
        }
    }
}
