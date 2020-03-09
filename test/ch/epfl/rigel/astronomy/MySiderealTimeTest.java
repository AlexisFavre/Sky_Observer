package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class MySiderealTimeTest {

    @Test
    void greenwichWorksOnTrivialTime() {
        assertEquals(1.22,
                SiderealTime.greenwich(
                    ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22),
                            LocalTime.of(14, 36, 51),
                            ZoneOffset.UTC)),
                1.0e-2);
    }

    @Test
    void greenwichTest() {
        /*assertEquals(1.2220619247737088, SiderealTime.greenwich(ZonedDateTime.of(1980,4,22,
                14,36,51,67, ZoneId.of("UTC"))), 1.0e-4);
        assertEquals(5.355270290366605, SiderealTime.greenwich(ZonedDateTime.of(2001,1,27,
                12,0,0,0, ZoneId.of("UTC"))), 1.0e-4);
        assertEquals(2.9257399567031235, SiderealTime.greenwich(ZonedDateTime.of(2004,9,23,
                11,0,0,0, ZoneId.of("UTC"))), 1.0e-4);*/
        assertEquals(1.9883078130455532, SiderealTime.greenwich(ZonedDateTime.of(2001,9,11,
                8,14,0,0, ZoneId.of("UTC"))), 1.0e-4);
    }

    @Test
    void localWorksOnTrivialTime() {
        assertEquals(1.74570958832716, SiderealTime.local(ZonedDateTime.of(1980,4,22,
                14,36,51,27,ZoneOffset.UTC),
                GeographicCoordinates.ofDeg(30,45)), 1.0e-4);
    }
}