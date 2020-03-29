package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class MyHygDatabaseLoaderTest {

    private static final String HYG_CATALOGUE_NAME =
            "/hygdata_v3.csv";

    public Star rigel() throws IOException {
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

    public Star betelgeuse() throws IOException {
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
        // manual read (wiki verification)
        assertEquals(0.180, rigel().magnitude(), 1.0e-7);
        assertEquals(-1.440, sirius().magnitude(), 1.0e-7);
        assertEquals(0.450, betelgeuse().magnitude(), 1.0e-7);
    }

    @Test
    void trivialStarsColorIndexIsWellLoaded() throws IOException {
        // wikipedia
        assertEquals(11000, rigel().colorTemperature(), 500);
        assertEquals(9940, sirius().colorTemperature(), 500);
        assertEquals(3500, betelgeuse().colorTemperature(), 500);
    }

    @Test
    void trivialStarsPositionIsWellLoaded() throws IOException {
        // wikipedia
        assertEquals(Angle.ofHr(5.2422), rigel().equatorialPos().ra(), 1.0e-4);
        assertEquals(Angle.ofDMS(-8, 12, 5), rigel().equatorialPos().dec(), 1.0e-1);
        assertEquals(Angle.ofHr(6.7522), sirius().equatorialPos().ra(), 1.0e-4);
        assertEquals(Angle.ofDMS(-16, 42, 58), sirius().equatorialPos().dec(), 1.0e-1);
        assertEquals(Angle.ofHr(5.9194), betelgeuse().equatorialPos().ra(), 1.0e-4);
        assertEquals(Angle.ofDMS(7, 24, 25), betelgeuse().equatorialPos().dec(), 1.0e-1);
    }

    @Test
    void syntax() throws IOException {
        try (InputStream hygStream = getClass()
                .getResourceAsStream(HYG_CATALOGUE_NAME)) {
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .build();
            int i = 0;
            for(Star s : catalogue.stars()) {
                if (s.name().charAt(0) == '?') {
                    i = 1;
                    assertEquals(' ', s.name().charAt(1));
                }
            }
            assertEquals(1,i);
        }
    }
}