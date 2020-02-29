package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class EclipticToEquatorialConversionTest {

    @Test
    void epsilonWorksWithTrivialWhen() {
        assertEquals(23.43805531, EclipticToEquatorialConversion.epsilon(
                ZonedDateTime.of(
                        LocalDate.of(2009, Month.JULY, 6),
                        LocalTime.of(23, 0),
                        ZoneOffset.UTC)),
                1.0e-3);
    }

    @Test
    void applyWorksOnTrivialCoordinates() {
        
    }
}