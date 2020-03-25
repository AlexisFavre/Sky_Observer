package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import static ch.epfl.rigel.Preconditions.checkInInterval;
import java.util.Locale;
import ch.epfl.rigel.math.ClosedInterval;

/**
 * represent the Moon
 * the characteristics of the Moon are calculate in MoonModel 
 * @author Alexis FAVRE (310552)
 */
public final class Moon extends CelestialObject {

    private final float phase;
    
    /**
     * create a Moon with the following characteristics
     * @param equatorialPos of the Moon
     * @param angularSize of the Moon
     * @param magnitude of the Moon
     * @param phase of the Moon
     * @throws IllegalArgumentException if phase is not in [0,1]
     */
    public Moon(EquatorialCoordinates equatorialPos,
            float angularSize, float magnitude, float phase) throws IllegalArgumentException{
        super("Lune", equatorialPos, angularSize, magnitude);
        checkInInterval(ClosedInterval.of(0,1), phase);
        this.phase = phase;
    }
    
    @Override
    /** @return an textual representation of the characteristics of the moon */
    public String info() {
        return String.format(Locale.ROOT, "%s (%.1f", name(), phase*100) +"%)";
    }

}
