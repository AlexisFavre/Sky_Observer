package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class HygDatabaseLoaderTest {

    private static final String HYG_CATALOGUE_NAME =
            "/hygdata_v3.csv";

    private Star rigel() throws IOException {
        Star rigel = null;
        try (InputStream hygStream = getClass()
                .getResourceAsStream(HYG_CATALOGUE_NAME)) {
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .build();
            for (Star s : catalogue.stars()) {
                if (s.name().equalsIgnoreCase("rigel"))
                    rigel = s;
            }
        }
        return rigel;
    }

    private Star sirius() throws IOException {
        Star sirius = null;
        try (InputStream hygStream = getClass()
                .getResourceAsStream(HYG_CATALOGUE_NAME)) {
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .build();
            for (Star s : catalogue.stars()) {
                if (s.name().equalsIgnoreCase("sirius"))
                    sirius = s;
            }
        }
        return sirius;
    }

    private Star betelgeuse() throws IOException {
        Star betelgeuse = null;
        try (InputStream hygStream = getClass()
                .getResourceAsStream(HYG_CATALOGUE_NAME)) {
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .build();
            for (Star s : catalogue.stars()) {
                if (s.name().equalsIgnoreCase("betelgeuse"))
                    betelgeuse = s;
            }
        }
        return betelgeuse;
    }

    @Test
    void hygDatabaseIsCorrectlyInstalled() throws IOException {
        try (InputStream hygStream = getClass()
                .getResourceAsStream(HYG_CATALOGUE_NAME)) {
            assertNotNull(hygStream);
        }
    }

    @Test
    void hygDatabaseContainsRigel() throws IOException {
        assertNotNull(rigel());
    }

    @Test
    void hygDatabaseContainsBetelgeuse() throws IOException {
        assertNotNull(betelgeuse());
    }

    @Test
    void hygDatabaseContainsSirius() throws IOException {
        assertNotNull(sirius());
    }

    @Test
    void trivialStarsMagnitudeIsWellLoaded() throws IOException {
        assertEquals(0.180, rigel().magnitude(), 1.0e-7);
        assertEquals(-1.440, sirius().magnitude(), 1.0e-7);
        assertEquals(0.450, betelgeuse().magnitude(), 1.0e-7); // betelgeuse not sure
    }

    @Test
    void trivialStarsColorIndexIsWellLoaded() throws IOException {
        assertEquals(, rigel().co(), 1.0e-10);
        //assertEquals(, betelgeuse().colorTemperature(), 1.0e-10);
    }

    @Test
    void trivialStarsPositionIsWellLoaded() throws IOException {
        //assertEquals(, rigel().equatorialPos().ra(), 1.0e-10);
        //assertEquals(, rigel().equatorialPos().dec(), 1.0e-10);
        //assertEquals(, betelgeuse().equatorialPos().ra(), 1.0e-10);
        //assertEquals(, betelgeuse().equatorialPos().dec(), 1.0e-10);
    }
}