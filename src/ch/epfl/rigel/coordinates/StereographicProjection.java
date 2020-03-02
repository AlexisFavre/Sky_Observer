package ch.epfl.rigel.coordinates;

import java.util.function.Function;
/**
 * 
 * @author Alexis FAVRE (310552)
 *
 */
public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates>{

    private double ɸ0;
    private double λ0;
    private double sinλ0;
    private double cosλ0;
    private double sinɸ0;
    private double cosɸ0;

    public StereographicProjection(HorizontalCoordinates center) {
        this.ɸ0 = center.az();
        this.λ0 = center.alt();
        this.sinɸ0 = Math.sin(ɸ0);
        this.cosɸ0 = Math.cos(ɸ0);
        this.sinλ0 = Math.sin(λ0);
        this.cosλ0 = Math.cos(λ0);
    }
    
    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates hor) {
        return null;
        
    }
    
    public double circleRadiusForParallel(HorizontalCoordinates parallel) {
        return 0;
        
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
        
        double lambdaD = azAlt.az()-λ0;
        double cosLambdaD = Math.cos(lambdaD);
        double sinLambdaD = Math.sin(lambdaD);
        
        double d = 1/ (1 + sinɸ0*sinPhi + cosɸ0*cosPhi*cosLambdaD);
        
        double x = d*cosPhi*sinLambdaD;
        double y = d*(sinPhi*cosɸ0 - cosPhi*sinɸ0*cosLambdaD);
        
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
        
        double lamda = Math.atan(x*sinC/(p*cosɸ0*cosC - y*sinɸ0*sinC)) + this.λ0;
        double phi = Math.asin(cosC*sinɸ0 + y*sinC*cosɸ0/p);
        
        return HorizontalCoordinates.of(lamda, phi);
        
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
    public final boolean equals(Object interval) {
        throw new UnsupportedOperationException();
    }

}
