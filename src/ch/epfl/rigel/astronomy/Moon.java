package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import static ch.epfl.rigel.Preconditions.checkInInterval;
import java.util.Locale;
import ch.epfl.rigel.math.ClosedInterval;

/**
 * 
 * @author Alexis FAVRE (310552)
 *
 */
public final class Moon extends CelestialObject {

    private final float phase;

    public Moon(EquatorialCoordinates equatorialPos,
            float angularSize, float magnitude, float phase) {
        super("Lune", equatorialPos, angularSize, magnitude);
        checkInInterval(ClosedInterval.of(0,1), phase);
        this.phase = phase;
    }
    
    @Override
    public String info() {
        return String.format(Locale.ROOT, "%s (%f.2 percent)", name(), phase*100);
    }

}
