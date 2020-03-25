package ch.epfl.rigel.math;

import static ch.epfl.rigel.Preconditions.checkArgument;
import java.util.Locale;

/**
 * represents a Polynomial function, not instanciable
 * @author Alexis FAVRE (310552)
 */
public final class Polynomial {
    
    private final double[] coefs;

    private Polynomial(double coefficientN, double... coefficients) throws IllegalArgumentException {
        checkArgument(coefficientN != 0);
        coefs = new double[coefficients.length+1];
        coefs[0] = coefficientN;
        System.arraycopy(coefficients, 0, coefs,1, coefficients.length);
    }
    
    /**
     * @return the degree of the polynomial
     */
    public int degree(){
        return coefs.length-1;
    }
    
    /**
     * to construct a Polynomial function
     * @param coefficientN the highest coefficient
     * @param coefficients other coefficients of the polynomial
     * @return the corresponding Polynomial function
     * @throws IllegalArgumentException if the highest coefficient is null
     */
    public final static Polynomial of(double coefficientN, double... coefficients) throws IllegalArgumentException{
        return new Polynomial(coefficientN, coefficients);
    }
    
    /**
     * calculate the image of the value x by the polynomial
     * @param x
     * @return the image of x
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
    /** @return a representation of the Polynomial */
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
    /** always throws UnsupportedOperationException */
    public final int hashCode() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    @Override 
    /** always throws UnsupportedOperationException */
    public final boolean equals(Object interval) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
