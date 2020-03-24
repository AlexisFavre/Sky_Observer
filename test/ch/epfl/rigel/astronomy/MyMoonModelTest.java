package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

class MyMoonModelTest {

    @Test
    void at() {
        assertEquals(14.211456457835897, MoonModel.MOON.at(-2313, 
                new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2003,  Month.SEPTEMBER, 1),LocalTime.of(0,0), 
                        ZoneOffset.UTC))).equatorialPos().raHr());
    }
    
    @Test
    void at2() {
        assertEquals(-0.20114171346014934, MoonModel.MOON.at(-2313, 
                new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2003,  Month.SEPTEMBER, 1),
                LocalTime.of(0,0), ZoneOffset.UTC))).equatorialPos().dec());
    }
    
    @Test
    void angularSize() {
        assertEquals(0.009225908666849136, MoonModel.MOON.at(Epoch.J2010.daysUntil(ZonedDateTime.of(LocalDate.of(1979, 9, 1),LocalTime.of(0, 0),
                ZoneOffset.UTC)), new EclipticToEquatorialConversion(ZonedDateTime.of(
                        LocalDate.of(1979, 9, 1),LocalTime.of(0, 0),ZoneOffset.UTC))).
                        angularSize());
    }
    
    @Test
    void info() {
        assertEquals("Lune (22.5%)", MoonModel.MOON.at(Epoch.J2010.daysUntil(ZonedDateTime.of(LocalDate.of(2003, 9, 1),LocalTime.of(0, 0),
                ZoneOffset.UTC)), new EclipticToEquatorialConversion(ZonedDateTime.of( LocalDate.of(2003, 9, 1),
                        LocalTime.of(0, 0),ZoneOffset.UTC))).
                        info());
    }

}
