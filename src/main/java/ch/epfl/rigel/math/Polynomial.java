package ch.epfl.rigel.math;

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
     * @return
     * (int) degree of the polynomial
     */
    public int degree(){
        return coefs.length-1;
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
        for(int i = 0; i<degree(); ++i) {
            if(coefs[i] !=0) {
                
                // add coef
                if(coefs[i]!=1 && coefs[i]!=-1) { // to don't write +/- 1.0x^ but only x^
                    s += String.format(Locale.ROOT, "%s", coefs[i]);
                }
                if(coefs[i]==-1) {
                    s += "-";
                }
                
                s+= "x";
                
                // add x and its exponent
                if(i<degree()-1) {
                    s += String.format(Locale.ROOT, "^%s", degree()-i); // to don't write x^1 but only x
                }
            }    
            
            // add symbol + only if next coefficient is positive
            if((coefs[i+1] > 0)) { s += "+";} 
        }
        
        // add the constant if not null
        if (coefs[degree()] !=0) { s += coefs[degree()]; } 
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
