package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicContainer.*;
import static org.junit.jupiter.api.DynamicTest.*;

import org.junit.jupiter.api.DynamicNode;

class MyStereographicProjectionTest {

    final StereographicProjection CENTER0 = new StereographicProjection(HorizontalCoordinates.of(0,0));
    final StereographicProjection CENTER_BIS = new StereographicProjection(HorizontalCoordinates.of(Math.PI/4,Math.PI/4));
    final StereographicProjection TRIVIAL = new StereographicProjection(HorizontalCoordinates.ofDeg(45,45));

    
    @Test
    void circleCenterForParallelBis() {
        assertEquals(0, CENTER0.circleCenterForParallel(HorizontalCoordinates.of(Math.PI/4,Math.PI/6)).x(),
                1.0e-10);
        assertEquals(2, CENTER0.circleCenterForParallel(HorizontalCoordinates.of(Math.PI/4,Math.PI/6)).y(),
                1.0e-10);
    }

    @Test
    void circleCenterForParallelFramePad() {
        assertEquals(0.6089987400733187, TRIVIAL.circleCenterForParallel(HorizontalCoordinates.ofDeg(0,27)).y(),
                1.0e-10);
    }

    @Test
    void circleRadiusForParallel() {
        assertEquals(0.7673831804, TRIVIAL.circleRadiusForParallel(HorizontalCoordinates.ofDeg(0,27)),
                1.0e-10);
        assertEquals(0, CENTER_BIS.circleRadiusForParallel(HorizontalCoordinates.of(Math.PI/2,Math.PI/2)),
                1.0e-10);
    }

    @Test
    void applyToAngle() {
        assertEquals(4.363330053e-3, TRIVIAL.applyToAngle(Angle.ofDeg(1/2.0)),
                1.0e-10);
    }

    @Test
    void apply() {
        assertEquals( -0.1316524976, TRIVIAL.apply(HorizontalCoordinates.ofDeg(45,30)).y(),
                1.0e-10);

        CartesianCoordinates expected1 = CartesianCoordinates.of(Math.sqrt(6)/(4+Math.sqrt(6)), 2/(4+Math.sqrt(6)));
        CartesianCoordinates expected2 = CartesianCoordinates.of(0, Math.sqrt(2)/(2+Math.sqrt(2)));

        assertEquals( expected1.x(), CENTER0.apply(HorizontalCoordinates.of(Math.PI/4,Math.PI/6)).x(),
                1.0e-10);
        assertEquals( expected1.y(), CENTER0.apply(HorizontalCoordinates.of(Math.PI/4,Math.PI/6)).y(),
                1.0e-10);
        assertEquals( expected2.x(), CENTER_BIS.apply(HorizontalCoordinates.of(Math.PI/2,Math.PI/2)).x(),
                1.0e-10);
        assertEquals( expected2.y(), CENTER_BIS.apply(HorizontalCoordinates.of(Math.PI/2,Math.PI/2)).y(),
                1.0e-10);
    }

    @Test
    void inverseApply() {
        assertEquals( 3.648704525474978, TRIVIAL.inverseApply(CartesianCoordinates.of(10,0)).az(),
                1.0e-6);
    }
}