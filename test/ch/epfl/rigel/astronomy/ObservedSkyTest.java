package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.coordinates.EquatorialToHorizontalConversion;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;

class ObservedSkyTest {
    
    ZonedDateTime OBSERVATION_TIME = ZonedDateTime.of(
            LocalDate.of(2020, Month.APRIL, 4),
            LocalTime.of(0, 0),
            ZoneOffset.UTC);
    GeographicCoordinates OBSERVER_COORD = GeographicCoordinates.ofDeg(30, 45);
    HorizontalCoordinates OBSERVER_LOOK = HorizontalCoordinates.ofDeg(20, 22);

    ObservedSky SKY = new ObservedSky(OBSERVATION_TIME, OBSERVER_COORD,new StereographicProjection(OBSERVER_LOOK), MyStarCatalogueTest.CATALOG);

    @Test
    void objectClosestToTauPhe() {
        EquatorialToHorizontalConversion convEquToHor = new EquatorialToHorizontalConversion(OBSERVATION_TIME, OBSERVER_COORD);
        CartesianCoordinates testPoint = SKY.projection().apply(convEquToHor.apply(
                EquatorialCoordinates.of(0.004696959812148989,-0.861893035343076)));
        assertEquals("Tau Phe", Objects.requireNonNull(SKY.objectClosestTo(testPoint, 0.1)).get().name());
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
        assertEquals(firstStarXMemory, SKY.starPointsRefs()[0]); 
        //assertEquals(Double.MAX_VALUE, SKY.starPointsRefs()[0]); not the same because not working
    }
}