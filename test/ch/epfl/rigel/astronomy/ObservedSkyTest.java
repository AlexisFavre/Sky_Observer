package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.coordinates.EquatorialToHorizontalConversion;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ObservedSkyTest {
    
    ZonedDateTime OBSERVATION_TIME = ZonedDateTime.of(
            LocalDate.of(2020, Month.APRIL, 4),
            LocalTime.of(0, 0),
            ZoneOffset.UTC);
    GeographicCoordinates OBSERVER_COORD = GeographicCoordinates.ofDeg(30, 45);
    HorizontalCoordinates OBSERVER_LOOK = HorizontalCoordinates.ofDeg(20, 22);

    ObservedSky SKY = new ObservedSky(OBSERVATION_TIME, OBSERVER_COORD, OBSERVER_LOOK, MyStarCatalogueTest.CATALOG);

    @Test
    void objectClosestToTauPhe() {
        EquatorialToHorizontalConversion convEquToHor = new EquatorialToHorizontalConversion(OBSERVATION_TIME, OBSERVER_COORD);
        CartesianCoordinates testPoint = SKY.projection().apply(convEquToHor.apply(
                EquatorialCoordinates.of(0.004696959812148989,-0.861893035343076)));
        assertEquals("Tau Phe", Objects.requireNonNull(SKY.objectClosestTo(testPoint, 0.1)).name());
    }

    @Test
    void objectClosestToMercury() {
        MyStarCatalogueTest test = new MyStarCatalogueTest();
        ObservedSky sky = new ObservedSky(ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22),
                LocalTime.of(14, 36, 51),
                ZoneOffset.UTC),
                GeographicCoordinates.ofDeg(6.57, 46.52),
                HorizontalCoordinates.ofDeg(45, 45),
                MyStarCatalogueTest.CATALOG);
        for(int i = 0; i < 1; ++i) {
            assertEquals(PlanetModel.MERCURY.name(), sky.objectClosestTo(sky.sunPoint(), 1.0e+3).name());
        }
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
}