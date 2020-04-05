package ch.epfl.rigel.coordinates;

import java.util.function.Function;

import ch.epfl.rigel.math.Angle;
/**
 * Function that convert {@code HorizontalCoordinates} to {@code CartesianCoordinates} (+inverse)
 * and proposes other functionality linked to the stereographic projection
 * like the computations needed for circle projections
 *
 * @author Alexis FAVRE (310552)
 */
public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates>{

    private final double centerAlt;
    private final double centerAzimuth;
    private final double sinCenterAlt;
    private final double cosCenterAlt;

    /**
     * @param center of the Stereographic projection
     */
    public StereographicProjection(HorizontalCoordinates center) {
        this.centerAlt = center.alt();
        this.centerAzimuth = center.az();
        this.sinCenterAlt = Math.sin(centerAlt);
        this.cosCenterAlt = Math.cos(centerAlt);
    }
    
    /**
     * Compute the circle center of the projected circle corresponding to the projection of the parallel
     * which is passing at the given point
     * 
     * @param hor point from which we want the projected parallel
     * @return the center of the circle made by the parallel projection
     */
    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates hor) {
        return CartesianCoordinates.of(0,cosCenterAlt/(sinCenterAlt + Math.sin(hor.alt())));
        
    }

    /**
     * Compute the circle radius of the projected circle corresponding to the projection of the parallel
     * which is passing at the given point
     *
     * @param parallel point from which we want the projected parallel
     * @return the radius of the circle made by the parallel projection
     */
    public double circleRadiusForParallel(HorizontalCoordinates parallel) {
        double parallelAlt = parallel.alt();
        return Math.cos(parallelAlt)/(sinCenterAlt + Math.sin(parallelAlt));
        
    }
    
    /**
     * Compute the diameter of the circle made by the projection of a sphere of angular size {@code rad}
     * when the projected circle is at the center of the global projection
     *
     * @param rad the diameter of the projected sphere
     * @return the diameter of the projected circle
     */
    public double applyToAngle(double rad) {
        return 2*Math.tan(rad/4);
        
    }

    /**
     * Gives the real {@code CartesianCoordinates} of a given point on the projection
     *
     * @param xy the cartesian of the point on the projection
     * @return the coordinates of the point of which xy are the coordinates of its projection
     */
    public HorizontalCoordinates inverseApply(CartesianCoordinates xy) {
        double x = xy.x();
        double y = xy.y();
        double p = Math.sqrt(x*x + y*y);
        double sinC = 2*p/(p*p +1);
        double cosC = (1 - p*p)/(p*p +1);
        
        double lamda = Math.atan2(x*sinC,(p*cosCenterAlt*cosC - y*sinCenterAlt*sinC)) + this.centerAzimuth;
        double phi   = Math.asin(cosC*sinCenterAlt + y*sinC*cosCenterAlt/p);
        
        return HorizontalCoordinates.of(Angle.normalizePositive(lamda), phi);
    }

    /**
     * to make a {@code StereographicProjection} of {@code HorizontalCoordinates}
     * @param azAlt which is the {@code HorizontalCoordinates} that 
     * we want project
     * @return the projection of azAlt
     */
    @Override
    public CartesianCoordinates apply(HorizontalCoordinates azAlt) {
        double cosPhi = Math.cos(azAlt.alt());
        double sinPhi = Math.sin(azAlt.alt());

        double lambdaD    = azAlt.az()-centerAzimuth;
        double cosLambdaD = Math.cos(lambdaD);
        double sinLambdaD = Math.sin(lambdaD);

        double d = 1.0/ (1 + sinCenterAlt*sinPhi + cosCenterAlt*cosPhi*cosLambdaD);

        double x = d*cosPhi*sinLambdaD;
        double y = d*(sinPhi*cosCenterAlt - cosPhi*sinCenterAlt*cosLambdaD);

        return CartesianCoordinates.of(x,y);
    }

    /**
     * Always throw exception
     * {@code conversion.projection()} is forbidden
     *
     * @throws UnsupportedOperationException in all conditions
     */
    @Override
    public final int hashCode() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throw exception
     * {@code conversion.equals()} is forbidden
     *
     * @throws UnsupportedOperationException in all conditions
     */
    @Override
    public final boolean equals(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * @return a {@code String} view of {@code this} with the format
     * StereographicProjection with parameter(HorizontalCoordinates) : (x= a, y= b)
     */
    @Override
    public String toString() {
        return "StereographicProjection with parameter(HorizontalCoordinates) : "
                + CartesianCoordinates.of(centerAzimuth, centerAlt).toString();
    }
}
