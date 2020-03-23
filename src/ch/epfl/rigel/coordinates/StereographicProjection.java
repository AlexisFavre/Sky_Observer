package ch.epfl.rigel.coordinates;

import java.util.function.Function;

import ch.epfl.rigel.math.Angle;
/**
 * 
 * @author Alexis FAVRE (310552)
 *
 */
public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates>{

    private final double centerAlt;
    private final double centerAzimut;
    private final double sinCenterAlt;
    private final double cosCenterAlt;

    public StereographicProjection(HorizontalCoordinates center) {
        this.centerAlt = center.alt();
        this.centerAzimut = center.az();
        this.sinCenterAlt = Math.sin(centerAlt);
        this.cosCenterAlt = Math.cos(centerAlt);
    }
    
    /**
     * 
     * @param hor (HorizontalCoordinates)
     * @return (CartesianCoordinates) of the center of the circle corresponding
     * to the parallel which across the point hor
     * (ordinate could be infinite)
     */
    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates hor) {
        return CartesianCoordinates.of(0,cosCenterAlt/(sinCenterAlt + Math.sin(hor.alt())));
        
    }
    
    /**
     * 
     * @param parallel (HorizontalCoordinates)
     * @return (double) radius of corresponding circle of the projection of the parallel
     * (could be infinite)
     */
    public double circleRadiusForParallel(HorizontalCoordinates parallel) {
        double parallelAlt = parallel.alt();
        return Math.cos(parallelAlt)/(sinCenterAlt + Math.sin(parallelAlt));
        
    }
    
    /**
     * 
     * @param rad
     * (double) the apparent diameter of the sphere centered at the center of the projection
     * @return
     * (double) the projected diameter of a sphere
     */
    public double applyToAngle(double rad) {
        return 2*Math.tan(rad/4);
        
    }
    /**
     * @param azAlt
     * (HorizontalCoordinates) coordinates of the point
     * @return
     * the carthesianCoordinates of the projection of the point
     */
    @Override
    public CartesianCoordinates apply(HorizontalCoordinates azAlt) {
        double cosPhi = Math.cos(azAlt.alt());
        double sinPhi = Math.sin(azAlt.alt());
        
        double lambdaD    = azAlt.az()-centerAzimut;
        double cosLambdaD = Math.cos(lambdaD);
        double sinLambdaD = Math.sin(lambdaD);
        
        double d = 1.0/ (1 + sinCenterAlt*sinPhi + cosCenterAlt*cosPhi*cosLambdaD);
        
        double x = d*cosPhi*sinLambdaD;
        double y = d*(sinPhi*cosCenterAlt - cosPhi*sinCenterAlt*cosLambdaD);
        
        return CartesianCoordinates.of(x,y);
    }
    /**
     * 
     * @param xy (CartesianCoordinates)
     * @return
     * (HorizontalCoordinates) of the point of which xy are coordinates of its projection
     */
    public HorizontalCoordinates inverseApply(CartesianCoordinates xy) {
        double x = xy.x();
        double y = xy.y();
        double p = Math.sqrt(x*x + y*y);
        double sinC = 2*p/(p*p +1);
        double cosC = (1 - p*p)/(p*p +1);
        
        double lamda = Math.atan2(x*sinC,(p*cosCenterAlt*cosC - y*sinCenterAlt*sinC)) + this.centerAzimut;
        double phi   = Math.asin(cosC*sinCenterAlt + y*sinC*cosCenterAlt/p);
        
        return HorizontalCoordinates.of(Angle.normalizePositive(lamda), phi);
    }
    
    @Override
    public String toString() {
        return "StereographicProjection with parameter(HorizontalCoordinates)"
                + " : " + CartesianCoordinates.of(centerAzimut, centerAlt).toString();
    }
    
    /**
     * always throws UnsupportedOperationException
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }
    
    
    /**
     * always throws UnsupportedOperationException
     */
    @Override 
    public final boolean equals(Object object) {
        throw new UnsupportedOperationException();
    }

}
