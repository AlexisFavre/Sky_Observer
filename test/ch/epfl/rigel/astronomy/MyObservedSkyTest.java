package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class MyObservedSkyTest {

    ZonedDateTime OBSERVATION_TIME = ZonedDateTime.of(
            LocalDate.of(2020, Month.APRIL, 4),
            LocalTime.of(0, 0),
            ZoneOffset.UTC);
    GeographicCoordinates OBSERVER_COORD = GeographicCoordinates.ofDeg(30, 45);
    HorizontalCoordinates OBSERVER_LOOK = HorizontalCoordinates.ofDeg(20, 22);

    ObservedSky SKY = new ObservedSky(OBSERVATION_TIME, OBSERVER_COORD, OBSERVER_LOOK, MyStarCatalogueTest.CATALOG);

    @Test
    void objectClosestTo() {
        EquatorialToHorizontalConversion convEquToHor = new EquatorialToHorizontalConversion(OBSERVATION_TIME, OBSERVER_COORD);
        CartesianCoordinates testPoint = SKY.projection().apply(convEquToHor.apply(
                EquatorialCoordinates.of(0.004696959812148989,-0.861893035343076)));
        assertEquals("Tau Phe", Objects.requireNonNull(SKY.objectClosestTo(testPoint, 0.1)).name());
    }

    @Test
    void sunPoint() {
    }

    @Test
    void moonPoint() {
    }

    @Test
    void planetPointsRefs() {
    }

    @Test
    void starsAndTheirPosition() {
        EquatorialToHorizontalConversion convEquToHor = new EquatorialToHorizontalConversion(OBSERVATION_TIME, OBSERVER_COORD);
        int i = 0;
        for (Star star : SKY.stars()) {
            assertEquals(SKY.projection().apply(convEquToHor.apply(star.equatorialPos())).x(), SKY.starPointsRefs()[i]);
            i += 2;
        }
        assert MyStarCatalogueTest.CATALOG != null;
        assertEquals(MyStarCatalogueTest.CATALOG.stars().size(), SKY.stars().size());
        //Si fail: Cloner/Copier le tableau
        double firstStarXMemory = SKY.starPointsRefs()[0];
        SKY.starPointsRefs()[0] = Double.MAX_VALUE;
        assertEquals(firstStarXMemory, SKY.starPointsRefs()[0]); // TODO Understand
        //assertEquals(Double.MAX_VALUE, SKY.starPointsRefs()[0]); not the same because not working
    }

    @Test
    void asterismIndices() {
    }

    @Test
    void asterisms() {
    }

    @Test
    void sun() {
    }

    @Test
    void moon() {
    }

    @Test
    void planets() {
    }
}