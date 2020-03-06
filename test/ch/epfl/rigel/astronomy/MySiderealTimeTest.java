package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MySiderealTimeTest {

    @Test
    void greenwichWorksOnTrivialTime() {
        SiderealTime.greenwich(
                ZonedDateTime.of(LocalDateTime.of(1980, 4, 22, 4, 40, 5.23))
        );
    }

    @Test
    void localWorksOnTrivialTime() {
    }
}