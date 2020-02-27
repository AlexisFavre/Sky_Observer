package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GeographicCoordinatesTest {

    @Test
    void ofDegWorksWithExpectedParameters() {
        //EclipticCoordinates.of(0, 360);
    }

    @Test
    void ofDegFailsWithNotExpectedParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            EclipticCoordinates.of(360, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            EclipticCoordinates.of(-0.2, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            EclipticCoordinates.of(0, -90);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            EclipticCoordinates.of(0, 90);
        });
    }

    @Test
    void isValidLonDegWorksOnExpectedDeg() {
        assertTrue(GeographicCoordinates.isValidLonDeg(-180));
        assertTrue(GeographicCoordinates.isValidLonDeg(-153));
        assertTrue(GeographicCoordinates.isValidLonDeg(0));
        assertTrue(GeographicCoordinates.isValidLonDeg(5));
        assertTrue(GeographicCoordinates.isValidLonDeg(179));
    }

    @Test
    void isValidLatDegWorksOnExpectedDeg() {
        assertTrue(GeographicCoordinates.isValidLonDeg(-90));
        assertTrue(GeographicCoordinates.isValidLonDeg(-76));
        assertTrue(GeographicCoordinates.isValidLonDeg(0));
        assertTrue(GeographicCoordinates.isValidLonDeg(7));
        assertTrue(GeographicCoordinates.isValidLonDeg(90));
    }


    @Test
    void isValidLatDegFailsOnNotSupportedValues() {
        assertFalse(GeographicCoordinates.isValidLonDeg(-91));
        assertFalse(GeographicCoordinates.isValidLonDeg(-2000));
        assertFalse(GeographicCoordinates.isValidLonDeg(91));
        assertFalse(GeographicCoordinates.isValidLonDeg(3000));
    }

    @Test
    void toStringWorksOnTrivialCoordinates() {
        assertEquals("(lon=6.5700°, lat=46.5200°)",
                GeographicCoordinates.ofDeg(6.57, 46.52).toString());
    }
}