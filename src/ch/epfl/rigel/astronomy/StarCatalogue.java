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
 * {@code StarCatalogue} represents a catalog of stars and asterisms
 * It is mostly created via its {@code StarCatalogue.Builder} that is able to load stars or asterisms
 * via a stream and a {@code StarCatalogue.Loader}
 * (ex to create a catalogue from 2 streams that contains respectively the stars and the asterism data:
 *     {@code new StarCatalogue.Builder()
 *                 .loadFrom(starFileName, HygDatabaseLoader.INSTANCE)
 *                 .loadFrom(asterismFileName, AsterismLoader.INSTANCE).build()}
 *  returns a {@code StarCatalogue} containing all the objects corresponding to file data)
 *
 * @author Alexis FAVRE (310552)
 * @author Augustin ALLARD (299918)
 * @see HygDatabaseLoader
 * @see AsterismLoader
 */
public final class StarCatalogue {

    private final List<Star> stars;
    private final Map<Asterism, List<Integer>> asterismsStarIndexesMapping;

    /**
     * @param my_stars {@code List} of the stars to be added
     * @param my_asterisms {@code List} of the asterims to be added
     * @throws IllegalArgumentException if at least one star of an asterism is not given in the list of stars
     */
    public StarCatalogue(List<Star> my_stars, List<Asterism> my_asterisms) throws IllegalArgumentException {
        stars = List.copyOf(my_stars);
        List<Asterism> immutablesAsterisms = List.copyOf(my_asterisms);
        asterismsStarIndexesMapping = new HashMap<>();
        for (Asterism a : immutablesAsterisms) {
            // verify that a contains only stars in the catalog
            checkArgument(stars.containsAll(a.stars()));

            // map the star indexes of a to a
            List<Integer> starIndexesOfA = new ArrayList<>();
            for(Star s : a.stars()) {
                starIndexesOfA.add(this.stars.indexOf(s));
            }
            asterismsStarIndexesMapping.put(a, starIndexesOfA);
        }
    }

    /**
     * Gives the star indexes forming the given asterism
     *
     * @param asterism of which we want the indexes
     * @return {@code List} of the star indexes
     * @throws IllegalArgumentException if the given asterism does not belongs to {@code this}
     */
    public List<Integer> asterismIndices(Asterism asterism) throws IllegalArgumentException {
        checkArgument(asterismsStarIndexesMapping.containsKey(asterism));
        return asterismsStarIndexesMapping.get(asterism);
    }

    /**
     * @return stars of the catalog
     */
    public List<Star> stars() {
        return stars;
    }

    /**
     * @return asterisms of the catalog
     */
    public Set<Asterism> asterisms() {
        return asterismsStarIndexesMapping.keySet();
    }


    //================================================================================================
    
    /**
     * Builder represents a {@code StarCatalogue} during construction
     * Used to load asterisms and stars
     * via a file name and {@code Loader} with {@code loadFrom()}
     */
    public final static class Builder {

        private List<Star> stars;
        private List<Asterism> asterisms;
        
        public Builder() {
            this.stars     = new ArrayList<Star>();
            this.asterisms = new ArrayList<Asterism>();
        }

        /**
         * Load the objects corresponding to the stream content in the builder {@code this} via the loader
         * Be careful types of loader used is conditioning the types of objects added ({@code Star}/{@code Asterism})
         *
         * @param inputStream containing stars or asterisms information to be loaded
         * @param loader corresponding to the type of data to be loaded
         * @return {@code this} i.e the loaded builder
         * @throws IOException if the loader could not load the data from the stream correctly
         */
        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
            loader.load(inputStream, this);
            return this;
        }

        /**
         * @return a new StarCatalogue with the properties of the builder {@code this}
         */
        public StarCatalogue build() {
            return new StarCatalogue(stars, asterisms);
        }
        
        /**
         * Add the given star to {@code this} (catalog in construction)
         * @param star to be added
         * @return {@code this} the builder
         */
        public Builder addStar(Star star) {
            stars.add(star);
            return this;
        }

        /**
         *
         * Add the given asterism to {@code this} (catalog in construction)
         * @param asterism to be added
         * @return {@code this} the builder
         */
        public Builder addAsterism(Asterism asterism) {
            asterisms.add(asterism);
            return this;
        }
        
        /**
         * @return unmodifiable view of the stars in the builder
         */
        public List<Star> stars(){
            return Collections.unmodifiableList(stars);
        }
        
        /**
         * @return unmodifiable view of the asterisms in the builder
         */
        public List<Asterism> asterisms(){
            return Collections.unmodifiableList(asterisms);
        }
    }


    //================================================================================================
    /**
     * Represents a generic loader that loads data from a stream to a {@code Builder}
     * The load manner is specific to the type of file and data that is loaded
     */
    public interface Loader {

        /**
         * Load the objects where the type corresponds to the stream content
         * and add them to the given {@code StarCatalogue.Builder}
         *
         * @param inputStream the stream containing data to create the objects
         * @param builder receiving the objects
         * @throws IOException if the loader could not load the data from the stream correctly
         */
        void load(InputStream inputStream, Builder builder) throws IOException;
    }
}
