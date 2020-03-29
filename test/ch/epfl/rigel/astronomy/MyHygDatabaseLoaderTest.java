package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class MyHygDatabaseLoaderTest {

    private MyStarCatalogueTest CATALOG_TEST = new MyStarCatalogueTest();

    @Test
    void hygDatabaseIsCorrectlyInstalled() throws IOException {
        try (InputStream hygStream = getClass()
                .getResourceAsStream("/hygdata_v3.csv")) {
            assertNotNull(hygStream);
        }
    }

    @Test
    void hygDatabaseContainsRigel() throws IOException {
        assertNotNull(CATALOG_TEST.loadedRigel());
    }

    @Test
    void hygDatabaseContainsBetelgeuse() throws IOException {
        assertNotNull(CATALOG_TEST.loadedBetelgeuse());
    }

    @Test
    void hygDatabaseContainsSirius() throws IOException {
        assertNotNull(CATALOG_TEST.loadedSirius());
    }

    @Test
    void trivialStarsMagnitudeIsWellLoaded() throws IOException {
        // manual read (wiki verification)
        assertEquals(0.180, CATALOG_TEST.loadedRigel().magnitude(), 1.0e-7);
        assertEquals(-1.440, CATALOG_TEST.loadedSirius().magnitude(), 1.0e-7);
        assertEquals(0.450, CATALOG_TEST.loadedBetelgeuse().magnitude(), 1.0e-7);
    }

    @Test
    void trivialStarsColorIndexIsWellLoaded() throws IOException {
        // wikipedia
        assertEquals(11000, CATALOG_TEST.loadedRigel().colorTemperature(), 500);
        assertEquals(9940, CATALOG_TEST.loadedSirius().colorTemperature(), 500);
        assertEquals(3500, CATALOG_TEST.loadedBetelgeuse().colorTemperature(), 500);
    }

    @Test
    void trivialStarsPositionIsWellLoaded() throws IOException {
        // wikipedia
        assertEquals(Angle.ofHr(5.2422), CATALOG_TEST.loadedRigel().equatorialPos().ra(), 1.0e-4);
        assertEquals(Angle.ofDMS(-8, 12, 5), CATALOG_TEST.loadedRigel().equatorialPos().dec(), 1.0e-1);
        assertEquals(Angle.ofHr(6.7522), CATALOG_TEST.loadedSirius().equatorialPos().ra(), 1.0e-4);
        assertEquals(Angle.ofDMS(-16, 42, 58), CATALOG_TEST.loadedSirius().equatorialPos().dec(), 1.0e-1);
        assertEquals(Angle.ofHr(5.9194), CATALOG_TEST.loadedBetelgeuse().equatorialPos().ra(), 1.0e-4);
        assertEquals(Angle.ofDMS(7, 24, 25), CATALOG_TEST.loadedBetelgeuse().equatorialPos().dec(), 1.0e-1);
    }

    @Test
    void syntax() throws IOException {
        int i = 0;
        for(Star s : CATALOG_TEST.initCatalog().stars()) {
            if (s.name().charAt(0) == '?') {
                i = 1;
                assertEquals(' ', s.name().charAt(1));
            }
        }
        assertEquals(1,i);
    }
}