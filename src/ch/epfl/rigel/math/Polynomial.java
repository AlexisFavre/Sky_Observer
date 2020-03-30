package ch.epfl.rigel.math;

import static ch.epfl.rigel.Preconditions.checkArgument;
import java.util.Locale;

/**
 * Represents a polynomial function
 *
 * @author Alexis FAVRE (310552)
 */
public final class Polynomial {
    
    private final double[] coefs;

    private Polynomial(double coefficientN, double... coefficients) throws IllegalArgumentException {
        checkArgument(coefficientN != 0);
        coefs = new double[coefficients.length + 1];
        coefs[0] = coefficientN;
        System.arraycopy(coefficients, 0, coefs,1, coefficients.length);
    }

    /**
     * Construct a polynomial function for the given coefficient as Cn*x^n +...+ C0
     *
     * @param coefficientN the highest coefficient
     * @param coefficients other coefficients of the polynomial
     * @return a new instance of {@code Polynomial} for the given coefficients
     * @throws IllegalArgumentException if the highest coefficient is null
     */
    public static Polynomial of(double coefficientN, double... coefficients) throws IllegalArgumentException{
        return new Polynomial(coefficientN, coefficients);
    }

    /**
     *
     * @return the degree of the polynomial
     */
    public int degree(){
        return coefs.length-1;
    }
    
    /**
     * Compute the image of the value x by the polynomial function
     *
     * @param x the parameter given to the function
     * @return the image of x by {@code this}
     */
    public double at(double x) {
        double result = 0;
        for(int i = 0; i < coefs.length - 1; ++i) {
            result = x*(result + coefs[i]);
        }
        result += coefs[coefs.length - 1];
        return result;
    }

    /**
     *
     * @return a {@code String} view of {@code this} with the format
     *
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i<degree(); ++i) {
            if(coefs[i] !=0) {
                
                // add coefficients
                if(coefs[i] != 1 && coefs[i] != -1) // to don't write +/- 1.0x^ but only x^
                    s.append(String.format(Locale.ROOT, "%s", coefs[i]));
                if(coefs[i] == -1)
                    s.append("-");
                
                s.append("x");
                
                // add x and its exponent
                if(i < degree() - 1)
                    s.append(String.format(Locale.ROOT, "^%s", degree() - i)); // to don't write x^1 but only x
            }    
            
            // add symbol + only if next coefficient is positive
            if((coefs[i + 1] > 0))
                s.append("+");
        }
        
        // add the constant if not null
        if (coefs[degree()] != 0)
            s.append(coefs[degree()]);
        return s.toString();
    }

    /**
     * Always throw exception
     * {@code polynomial.hashCode()} is forbidden
     *
     * @throws UnsupportedOperationException in all conditions
     */
    @Override
    public final int hashCode() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Always throw exception
     * {@code polynomial.equals()} is forbidden
     *
     * @throws UnsupportedOperationException in all conditions
     */
    @Override
    public final boolean equals(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
