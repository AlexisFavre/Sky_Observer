package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class ObservedSkyTest {

    @Test
    void objectClosestTo() {
        MyStarCatalogueTest test = new MyStarCatalogueTest();
        ObservedSky sky = new ObservedSky(ZonedDateTime.of(LocalDate.of(1980, Month.APRIL, 22),
                LocalTime.of(14, 36, 51),
                ZoneOffset.UTC),
                GeographicCoordinates.ofDeg(6.57, 46.52),
                HorizontalCoordinates.ofDeg(45, 45),
                MyStarCatalogueTest.CATALOG);
        for(int i = 0; i < 1; ++i) {
            assertEquals(PlanetModel.MERCURY, sky.objectClosestTo(sky.sunPoint(), 1.0e+3));
        }
    }
}