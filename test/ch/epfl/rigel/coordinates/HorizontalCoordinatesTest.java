package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HorizontalCoordinatesTest {

    @Test
    void ofWorksWithExpectedParameters() {
        EclipticCoordinates.of(0, Math.PI/2);
    }

    @Test
    void ofFailsWithNotExpectedParameters() {
        assertThrows(IllegalArgumentException.class, () -> {
            EclipticCoordinates.of(2 * Math.PI, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            EclipticCoordinates.of(-0.2, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            EclipticCoordinates.of(0, -Math.PI);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            EclipticCoordinates.of(0, Math.PI);
        });
    }

    @Test
    void ofDegWorksWithExpectedParameters() {
        EclipticCoordinates.of(0, 360);
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
    void angularDistanceToWorksOnTrivialCoordinates() {
        assertEquals(0.0279,
                HorizontalCoordinates.ofDeg(6.5682, 46.5183)
                        .angularDistanceTo(HorizontalCoordinates.ofDeg(8.5476, 47.3763)),
                1.0e-3);
    }

    @Test
    void azOctantNameWorksOnTrivialCoordinates() {
        assertEquals("N", HorizontalCoordinates.ofDeg(0, 0)
                .azOctantName("N", "E", "S", "W"));
        assertEquals("N", HorizontalCoordinates.ofDeg(22, -20)
                .azOctantName("N", "E", "S", "W"));
        assertEquals("N", HorizontalCoordinates.ofDeg(358, 10)
                .azOctantName("N", "E", "S", "W"));
        assertEquals("NE", HorizontalCoordinates.ofDeg(22.5, 0)
                .azOctantName("N", "E", "S", "W"));
        assertEquals("NE", HorizontalCoordinates.ofDeg(47, 0)
                .azOctantName("N", "E", "S", "W"));
        assertEquals("E", HorizontalCoordinates.ofDeg(98, 0)
                .azOctantName("N", "E", "S", "W"));
        assertEquals("SE", HorizontalCoordinates.ofDeg(132, 0)
                .azOctantName("N", "E", "S", "W"));
        assertEquals("S", HorizontalCoordinates.ofDeg(180, 0)
                .azOctantName("N", "E", "S", "W"));
        assertEquals("SW", HorizontalCoordinates.ofDeg(220, 0)
                .azOctantName("N", "E", "S", "W"));
        assertEquals("W", HorizontalCoordinates.ofDeg(277, 0)
                .azOctantName("N", "E", "S", "W"));
        assertEquals("NW", HorizontalCoordinates.ofDeg(319, 0)
                .azOctantName("N", "E", "S", "W"));
    }

    @Test
    void toStringWorksOnTrivialCoordinates() {
        assertEquals("(az=350.0000°, alt=7.2000°)",
                HorizontalCoordinates.ofDeg(350, 7.2).toString());
    }
}