package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

class MyStarTest {

    @Test
    void testStarTemperature() {
        assertEquals(10515, new Star(24436, "Rigel", EquatorialCoordinates.of(0, 0), 0, -0.03f)
                .colorTemperature());
    }

    @Test
    void testStarTemperature2() {
        assertEquals(3793, new Star(27989, "Betelgeuse", EquatorialCoordinates.of(0, 0), 0, 1.50f)
        .colorTemperature());
    }
}
