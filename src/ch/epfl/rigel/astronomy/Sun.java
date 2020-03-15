package ch.epfl.rigel.astronomy;

import java.util.Objects;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * 
 * @author Alexis FAVRE (310552)
 *
 */
public final class Sun extends CelestialObject {

    private final EclipticCoordinates eclipticPos;
    private final float meanAnomaly; // in radians

    public Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos,
            float angularSize, float meanAnomaly) {
        super("Sun", equatorialPos, angularSize, -26.7f);
        this.eclipticPos = Objects.requireNonNull(eclipticPos);
        this.meanAnomaly = meanAnomaly;
    }

    /**
     * @return the eclipticPos
     */
    public EclipticCoordinates eclipticPos() {
        return eclipticPos;
    }

    /**
     * @return (double) the meanAnomaly in radians
     */
    public double meanAnomaly() {
        return meanAnomaly;
    }

}
