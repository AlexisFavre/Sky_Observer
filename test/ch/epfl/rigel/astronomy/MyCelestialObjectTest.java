package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.TestRandomizer;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.math.Angle;

class MyCelestialObjectTest {

    @Test
    void sterCircleCenterForParallelReturnsInfinity() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var az = rng.nextDouble(0, 2d * Angle.TAU);
            var alt = rng.nextDouble(-Angle.TAU / 4d, Angle.TAU / 4d);
            var c = HorizontalCoordinates.of(az, alt);
            var s = new StereographicProjection(c);
            
            var h = HorizontalCoordinates.of(az, -alt);
            
            assertTrue((s.circleCenterForParallel(h).toString().equals("(x=0.0000, y=Infinity)")));
        }
    }

    @Test
    void sterCircleRadiusForParallelReturnsInfinity() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var az = rng.nextDouble(0, 2d * Angle.TAU);
            var alt = rng.nextDouble(-Angle.TAU / 4d, Angle.TAU / 4d);
            var c = HorizontalCoordinates.of(az, alt);
            var s = new StereographicProjection(c);
            
            var h = HorizontalCoordinates.of(az, -alt);
            
            assertEquals(1.0 / 0.0, s.circleRadiusForParallel(h));
        }
    }

}
