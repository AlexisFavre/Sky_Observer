package ch.epfl.rigel.astronomy;

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
    void localWorksOnTrivialTime() {
    }
}