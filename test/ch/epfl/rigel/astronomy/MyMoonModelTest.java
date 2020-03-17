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

}
