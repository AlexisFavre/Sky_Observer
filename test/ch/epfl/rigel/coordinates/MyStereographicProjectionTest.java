package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyStereographicProjectionTest {

    StereographicProjection TRIVIAL = new StereographicProjection(HorizontalCoordinates.ofDeg(45,45));

    @Test
    void circleCenterForParallel() {
        assertEquals(0.6089987400733187, TRIVIAL.circleCenterForParallel(HorizontalCoordinates.ofDeg(0,27)).y(),
                1.0e-5);
    }

    @Test
    void circleRadiusForParallel() {
        assertEquals(0.7673831804, TRIVIAL.circleRadiusForParallel(HorizontalCoordinates.ofDeg(0,27)),
                1.0e-5);
    }

    @Test
    void applyToAngle() {
        assertEquals(4.363330053e-3, TRIVIAL.applyToAngle(Angle.ofDeg(1/2.0)),
                1.0e-5);
    }

    @Test
    void apply() {
        assertEquals( -0.1316524976, TRIVIAL.apply(HorizontalCoordinates.ofDeg(45,30)).y(),
                1.0e-5);
    }

    @Test
    void inverseApply() {
        assertEquals( 3.648704525474978, TRIVIAL.inverseApply(CartesianCoordinates.of(10,0)).az(),
                1.0e-5);
    }

    @Test
    void testToString() {
        // TODO test
    }
}