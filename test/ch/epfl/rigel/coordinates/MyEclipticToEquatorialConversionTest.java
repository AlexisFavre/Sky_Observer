package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class MyEclipticToEquatorialConversionTest {

    final EclipticToEquatorialConversion TRIVIAL_CONVERSION = new EclipticToEquatorialConversion(
            ZonedDateTime.of(
                    LocalDate.of(2009, Month.JULY, 6),
                    LocalTime.of(23, 0),
                    ZoneOffset.UTC)
    );

    @Test
    void applyWorksOnTrivialCoordinates() {

        EclipticCoordinates trivialCoordinates = EclipticCoordinates.of(
                Angle.ofDeg(Angle.ofDMS(139, 41, 10)),
                Angle.ofDeg(Angle.ofDMS(4, 52, 31))
        );

        assertEquals(143.72, TRIVIAL_CONVERSION.apply(trivialCoordinates).raDeg(), 1.0e-2);
        assertEquals(293.04, TRIVIAL_CONVERSION.apply(trivialCoordinates).decDeg(), 1.0e-2);
    }

    @Test
    void testHashCode() {
        assertThrows(UnsupportedOperationException.class, TRIVIAL_CONVERSION::hashCode);
    }

    @Test
    void testEquals() {
        assertThrows(UnsupportedOperationException.class, () -> {
            TRIVIAL_CONVERSION.equals(new Object());
        });
    }
}