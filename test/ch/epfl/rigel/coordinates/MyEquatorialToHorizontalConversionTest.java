package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class MyEquatorialToHorizontalConversionTest {

    final EquatorialToHorizontalConversion TRIVIAL_CONVERSION = new EquatorialToHorizontalConversion(
            ZonedDateTime.of(
                    LocalDate.of(2009, Month.JULY, 22),
                    LocalTime.of(5, 9),
                    ZoneOffset.UTC),
            GeographicCoordinates.ofDeg(172, 6)
    );

//    @Test erronné !!!!!!
//    void applyWorksOnTrivialCoordinates() {
//        assertEquals(289.40, Angle.toDeg(
//                    TRIVIAL_CONVERSION.apply(EquatorialCoordinates.of(
//                            Angle.ofDeg(121.71),
//                            Angle.ofDeg(Angle.ofDMS(20, 14, 47.16))
//                    )).az()
//                ), 1.e-3);
//    }

    @Test
    void testHashCode() {
        assertThrows(UnsupportedOperationException.class, () -> {
            TRIVIAL_CONVERSION.hashCode();
        });
    }

    @Test
    void testEquals() {
        assertThrows(UnsupportedOperationException.class, () -> {
            TRIVIAL_CONVERSION.equals(new Object());
        });
    }
}