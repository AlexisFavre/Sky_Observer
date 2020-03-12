package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyCartesianCoordinatesTest {

    final CartesianCoordinates TRIVIAL = CartesianCoordinates.of(1.2, 3.7);

    @Test
    void of() {
        assertEquals(1.2, TRIVIAL.x(), 1.0e-10);
        assertEquals(3.7, TRIVIAL.y(), 1.0e-10);
    }

    @Test
    void testToString() {
        assertEquals("(x=1.2000, y=3.7000)", TRIVIAL.toString());
    }
}