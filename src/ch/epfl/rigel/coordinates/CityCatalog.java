package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.GeneralBuilder;
import ch.epfl.rigel.Loader;
import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.StarCatalogue;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static ch.epfl.rigel.Preconditions.checkArgument;

public class CityCatalog {

    private final List<City> cities;

    /**
     * @param my_stars {@code List} of the stars to be added
     * @param my_asterisms {@code List} of the asterims to be added
     * @throws IllegalArgumentException if at least one star of an asterism is not given in the list of stars
     */
    public CityCatalog(List<City> cities) {
        this.cities = List.copyOf(cities);
    }

    /**
     * @return cities of the catalog
     */
    public List<City> cities() {
        return cities;
    }

    //================================================================================================

    /**
     * Builder represents a {@code StarCatalogue} during construction
     * Used to load asterisms and stars
     * via a file name and {@code Loader} with {@code loadFrom()}
     */
    public final static class Builder implements GeneralBuilder {

        private final List<City> cities;

        public Builder() {
            cities = new ArrayList<City>();
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
        public CityCatalog.Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
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
        public StarCatalogue.Builder addStar(Star star) {
            stars.add(star);
            return this;
        }

        /**
         * @return unmodifiable view of the stars in the builder
         */
        public List<Star> stars(){
            return Collections.unmodifiableList(stars);
        }
    }
}
