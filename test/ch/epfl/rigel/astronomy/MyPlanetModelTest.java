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

    @Test
    void atworks1() {
        assertEquals(11.187154934709678, PlanetModel.JUPITER.at(-2231.0,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22),
                                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC)))
                .equatorialPos().raHr(), 1e-10);
    }
        
        @Test
    void atworks2() {
        
        assertEquals(6.356635506685756 , PlanetModel.JUPITER.at(-2231.0,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22),
                                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC)))
                .equatorialPos().decDeg(), 1e-10);
    }
        
        @Test
    void atworks3() {
        
        assertEquals(35.11141185362771, Angle.toDeg(PlanetModel.JUPITER.at(-2231.0,
                new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22)
                        , LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC))).angularSize())*3600, 1e-10);
    }
        
    @Test
    void atworks4() {
        assertEquals(-1.9885659217834473, PlanetModel.JUPITER.at(-2231.0,new 
                EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22), 
                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC))).magnitude(),1e-10);
    }
    
    @Test
    void atworks5() {
        assertEquals(16.820074565897194, PlanetModel.MERCURY.at(-2231.0,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22),
                                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC)))
                .equatorialPos().raHr(), 1e-10);
        
        assertEquals(-24.500872462861274, PlanetModel.MERCURY.at(-2231.0,
                new EclipticToEquatorialConversion(
                        ZonedDateTime.of(LocalDate.of(2003, Month.NOVEMBER, 22),
                                LocalTime.of(0, 0, 0, 0), ZoneOffset.UTC)))
                .equatorialPos().decDeg(), 1e-10);
    }

}
