package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

/**
 * @author Alexis FAVRE (310552)
 * Model of the moon
 */
public enum MoonModel implements CelestialObjectModel<Moon>{
    
    MOON;
    
    private final double l0 = Angle.ofDeg(91.929336); 
    private final double P0 = Angle.ofDeg(130.143076);
    private final double N0 = Angle.ofDeg(291.682547);
    private final double i  = Angle.ofDeg(5.145396);
    private final double e  = 0.0549; 

    @Override
    public Moon at(double daysSinceJ2010,
            EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        
        double lambda = SunModel.SUN.longEcliptic(daysSinceJ2010);
        double M = SunModel.SUN.meanAnomaly(daysSinceJ2010);
        
        // to determine longitude orbital of the Moon
        double l  = Angle.ofDeg(13.1763966)*daysSinceJ2010 + l0;
        double Mm = l - Angle.ofDeg(0.1114041)*daysSinceJ2010 - P0;
        double Ev = Angle.ofDeg(1.2739) * Math.sin(2*(l-lambda) - Mm);
        double Ae = Angle.ofDeg(0.1858) * Math.sin(M);
        double A3 = Angle.ofDeg(0.37)   * Math.sin(M);
        double MmCcorrect = Mm + Ev - Ae - A3; // Mm' in formulas
        double Ec = Angle.ofDeg(6.2886) * Math.sin(MmCcorrect);
        double A4 = Angle.ofDeg(0.214)  * Math.sin(2*MmCcorrect);
        double lCorrect   = l + Ev + Ec - Ae + A4;
        double V  = Angle.ofDeg(0.6583) * Math.sin(2*(lCorrect-lambda));
        double trueLong   = lCorrect + V; // l'' in formulas
        
        // to determine ecliptic position of the moon
        double N = N0 - Angle.ofDeg(0.0529539)*daysSinceJ2010;
        double NCorrect = N - Angle.ofDeg(0.16)*Math.sin(M);
        double eclipticLong = Math.atan2(Math.sin(trueLong - NCorrect) * Math.cos(i) 
                                , Math.cos(trueLong - NCorrect)) + NCorrect;
        double eclipticLat  = Math.asin(Math.sin(trueLong - NCorrect) * Math.sin(i));
        EclipticCoordinates position = EclipticCoordinates.of(Angle.normalizePositive(eclipticLong), eclipticLat);
        
        // phase of the moon
        double F = (1 - Math.cos(trueLong - lambda)) / 2;
        
        // to determine angularSize of the moon
        double p = (1- e*e) / (1 + e*Math.cos(MmCcorrect + Ec));
        double angularSize = Angle.ofDeg(0.5181) / p;
        
        return new Moon(eclipticToEquatorialConversion.apply(position), (float)angularSize, (float)0, (float)F);
    }

}
