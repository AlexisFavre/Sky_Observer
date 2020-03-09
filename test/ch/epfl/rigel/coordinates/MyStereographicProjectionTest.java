package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyStereographicProjectionTest {

    @Test
    void circleCenterForParallel() {
        StereographicProjection test = new StereographicProjection(HorizontalCoordinates.ofDeg(45,45));
        assertEquals(0.6089987400733187, test.circleCenterForParallel(HorizontalCoordinates.ofDeg(0,27)).y(),
                1.0e-5);
    }

    @Test
    void circleRadiusForParallel() {
    }

    @Test
    void applyToAngle() {
    }

    @Test
    void apply() {
    }

    @Test
    void inverseApply() {
    }

    @Test
    void testToString() {
    }
}