package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySunTest {

    Sun TRIVIAL = new Sun(EclipticCoordinates.of(Angle.ofDeg(53), Angle.ofDeg(38)),
            EquatorialCoordinates.of(Angle.ofDeg(55.8),Angle.ofDeg(24)),
            0.4f, 5.f);

    @Test
    void eclipticPos() {

        // TODO test
    }

    @Test
    void meanAnomaly() {
        assertEquals(5.f, TRIVIAL.meanAnomaly());
    }
}