package ch.epfl.rigel.astronomy;

import static ch.epfl.rigel.Preconditions.checkArgument;

import java.io.IOException;
import java.io.InputStream;
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
    private final Map<Asterism, List<Integer>> asterisms;

    public StarCatalogue(List<Star> my_stars, List<Asterism> my_asterisms) {
        for (Asterism asterism : my_asterisms) {
            checkArgument(my_stars.containsAll(asterism.stars()));
        }
        
        stars = new ArrayList<Star>(List.copyOf(my_stars));
        asterisms = new HashMap<Asterism, List<Integer>>();
        
        for (Asterism asterism : my_asterisms) {
            List<Integer> indexsOfThisAsterism = new ArrayList<>();
            for(Star star : asterism.stars()) {
                indexsOfThisAsterism.add(this.stars.indexOf(star));
            }
            asterisms.put(asterism, indexsOfThisAsterism);
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
        return asterisms.keySet();
    }
    
    /**
     * 
     * @param asterism
     * @return
     * list of the indexes of the star in this asterism
     */
    public List<Integer> asterismIndices(Asterism asterism){
        return asterisms.get(asterism);
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

        /**
         * Load the content of the inputStream in this via the loader
         *
         * @param inputStream containing stars and asterisms informations to be loaded
         * @param loader by which it is loaded
         * @return this (loaded builder)
         * @throws IOException
         */
        Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
            loader.load(inputStream, this);
            return this;
        }

        StarCatalogue build() {
            return new StarCatalogue(stars, asterisms);
        }
    }


    //================================================================================================

    public interface Loader {

        void load(InputStream inputStream, Builder builder) throws IOException;
    }
}
