package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EclipticCoordinatesTest {

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
    void toStringWorksOnTrivialCoordinates() {
        assertEquals("(λ=22.5000°, β=18.0000°)",
                EclipticCoordinates.of(Math.PI/8, Math.PI/10).toString());
    }
}