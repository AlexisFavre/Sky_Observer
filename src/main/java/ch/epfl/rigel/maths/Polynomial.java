package ch.epfl.rigel.maths;

import static ch.epfl.rigel.Preconditions.checkArgument;

import java.util.Locale;
/**
 * 
 * @author Alexis FAVRE (310552)
 *
 */
public final class Polynomial {
    
    private double[] coefs;

    private Polynomial(double coefficientN, double... coefficients) {
        checkArgument(coefficientN!=0);
        coefs = new double[coefficients.length+1];
        coefs[0] = coefficientN;
        System.arraycopy(coefficients, 0, coefs,1, coefficients.length);
    }
    
    /**
     * 
     * @param coefficientN
     * @param coefficients
     * other coefficients of the polynomial
     * @return
     * a copy of our Polynomial
     */
    public final static Polynomial of(double coefficientN, double... coefficients) {
        return new Polynomial(coefficientN, coefficients);
    }
    
    /**
     * calculate the image of x by the polynomial
     * @param x
     * (double)
     * @return
     * the image
     */
    public double at(double x) {
        double result = 0;
        for(int i = 0; i < coefs.length-1; ++i) {
            result = x * (result + coefs[i]);
        }
        result += coefs[coefs.length-1];
        return result;
    }

    @Override
    public String toString() {
        String s = "";
        for(int i =coefs.length-1; i>0; --i) {
            if(coefs[i] !=0) {
                s += String.format(Locale.ROOT, "%sx^%s ", coefs[i], i);
            }
            if((coefs[i-1] >=0)) { s += "+ ";}
        }
        if (coefs[coefs.length-1] !=0) { s += coefs[coefs.length-1]; }
        return s;
    }
    
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }
    
    @Override 
    public final boolean equals(Object interval) {
        throw new UnsupportedOperationException();
    }
}
