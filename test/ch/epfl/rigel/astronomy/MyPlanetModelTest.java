package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

class MyPlanetModelTest {

    final Planet TRIVIAL_JUPITER = PlanetModel.JUPITER.at(-2231.0,
            new EclipticToEquatorialConversion(
            ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22),
                                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC)));

    final Planet TRIVIAL_MERCURY = PlanetModel.MERCURY.at(-2231.0,
            new EclipticToEquatorialConversion(
                    ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22),
                            LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC)));

    @Test
    void atLongitudeWorksWithTrivialJupiter() {
        assertEquals(11.187154934709678, TRIVIAL_JUPITER.equatorialPos().raHr(), 1e-10);
    }
        
    @Test
    void atLatitudeWorksWithTrivialJupiter() {
        
        assertEquals(6.356635506685756 , TRIVIAL_JUPITER.equatorialPos().decDeg(), 1e-10);
    }
    
    @Test
    void atLongitudeWorksWithTrivialMercury() {
        assertEquals(16.820074565897194, TRIVIAL_MERCURY.equatorialPos().raHr(), 1e-10);
    }

    @Test
    void atLatitudeWorksWithTrivialMercury() {
        assertEquals(-24.500872462861274, TRIVIAL_MERCURY.equatorialPos().decDeg(), 1e-10);
    }

    @Test
    void atAngularSizeWorksWithTrivialJupiter() {

        assertEquals(35.11141185362771, Angle.toDeg(TRIVIAL_JUPITER.angularSize()) * 3600, 1e-10);
    }

    @Test
    void atMagnitudeWorksWithTrivialJupiter() {
        assertEquals(-1.9885659217834473, TRIVIAL_JUPITER.magnitude(),1e-10);
    }

    /*@Test
    void atAngularSizeWorksWithTrivialMercury() {
        assertEquals(35.11141185362771, Angle.toDeg(TRIVIAL_MERCURY.angularSize()) * 3600, 1e-1);
    }

    @Test
    void atMagnitudeWorksWithTrivialMercury() {
        assertEquals(-1.9885659217834473, TRIVIAL_MERCURY.magnitude(),1e-1);
    }*/
}
