package ch.epfl.rigel.astronomy;

import java.time.LocalDate;

import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyEpochTest {

    @Test
    void checkDaysUntilWorks() {
        ZonedDateTime a = ZonedDateTime.of(
                LocalDate.of(2003, Month.JULY, 30),
                LocalTime.of(15, 0),
                ZoneOffset.UTC);
        ZonedDateTime b = ZonedDateTime.of(
                LocalDate.of(2020, Month.MARCH, 20),
                LocalTime.of(0, 0),
                ZoneOffset.UTC);
        ZonedDateTime c = ZonedDateTime.of(
                LocalDate.of(2006, Month.JUNE, 16),
                LocalTime.of(18, 13),
                ZoneOffset.UTC);
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 3),
                LocalTime.of(18, 0),
                ZoneOffset.UTC);
        ZonedDateTime e = ZonedDateTime.of(
                LocalDate.of(1999, Month.DECEMBER, 6),
                LocalTime.of(23, 3),
                ZoneOffset.UTC);
        ZonedDateTime f = ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 1),
                LocalTime.of(12, 30),
                ZoneOffset.UTC);
        ZonedDateTime g = ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 2),
                LocalTime.of(0, 0),
                ZoneOffset.UTC);
        ZonedDateTime h = ZonedDateTime.of(
                LocalDate.of(2009, Month.DECEMBER, 31),
                LocalTime.of(0, 0),
                ZoneOffset.UTC);
        ZonedDateTime i = ZonedDateTime.of(
                LocalDate.of(2009, Month.JANUARY, 1),
                LocalTime.of(0, 0),
                ZoneOffset.UTC);
        ZonedDateTime j = ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 3),
                LocalTime.of(18, 0),
                ZoneOffset.UTC);
        ZonedDateTime k = ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 1),
                LocalTime.of(12, 0),
                ZoneOffset.UTC);
        
        assertEquals(Epoch.J2000.daysUntil(j), 2.25);
        assertEquals(Epoch.J2000.daysUntil(k), 0);
        assertEquals(Epoch.J2000.daysUntil(f), 1d/48d);
        assertEquals(Epoch.J2000.daysUntil(g), 0.5d);
        assertEquals(Epoch.J2010.daysUntil(h), 0);
        assertEquals(Epoch.J2010.daysUntil(i), -364);

        assertEquals(1306.125, Epoch.J2000.daysUntil(a));
        assertEquals(7383.5, Epoch.J2000.daysUntil(b));
        assertEquals(2358.259028, Epoch.J2000.daysUntil(c), 1e-6);
        assertEquals(2.25, Epoch.J2000.daysUntil(d));
        assertEquals(-25.539583, Epoch.J2000.daysUntil(e), 1e-6);

        assertEquals(-2345.375, Epoch.J2010.daysUntil(a), 1e-6);
        assertEquals(3732, Epoch.J2010.daysUntil(b));
        assertEquals(-1293.240972, Epoch.J2010.daysUntil(c), 1e-6);
        assertEquals(-3649.25, Epoch.J2010.daysUntil(d), 1e-6);
        assertEquals(-3677.039583, Epoch.J2010.daysUntil(e), 1e-6);
    }

}
