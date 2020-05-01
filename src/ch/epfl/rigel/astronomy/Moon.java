package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import static ch.epfl.rigel.Preconditions.checkInInterval;
import java.util.Locale;
import ch.epfl.rigel.math.ClosedInterval;

/**
 * Represents the Moon
 * The characteristics of the Moon are computed with a {@code MoonModel}
 * @author Alexis FAVRE (310552)
 */
public final class Moon extends CelestialObject {

    private final float phase;
    private final static ClosedInterval CINTER_0TO1 = ClosedInterval.of(0,1);
    
    /**
     *
     * @param equatorialPos of the Moon
     * @param angularSize of the Moon
     * @param magnitude of the Moon
     * @param phase of the Moon
     * @throws IllegalArgumentException if phase is not in [0,1]
     */
    public Moon(EquatorialCoordinates equatorialPos,
            float angularSize, float magnitude, float phase) throws IllegalArgumentException{
        super("Lune", equatorialPos, angularSize, magnitude);
        this.phase = (float) checkInInterval(CINTER_0TO1, phase);
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String info() {
        return String.format(Locale.ROOT, "%s (%.1f", name(), phase*100) +"%)";
    }

}
