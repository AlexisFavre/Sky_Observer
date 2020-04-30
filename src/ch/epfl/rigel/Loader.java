package ch.epfl.rigel;

import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a generic loader that loads data from a stream to a {@code Builder}
 * The load manner is specific to the type of file and data that is loaded
 *
 * @author Augustin ALLARD (299918)
 * @see HygDatabaseLoader
 * @see AsterismLoader
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
    void load(InputStream inputStream, GeneralBuilder builder) throws IOException;
}
