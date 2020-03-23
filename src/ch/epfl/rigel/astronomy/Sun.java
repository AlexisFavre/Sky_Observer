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
    private final float meanAnomaly;

    public Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos,
            float angularSize, float meanAnomaly) {
        super("Soleil", equatorialPos, angularSize, -26.7f);
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
     * @return (double) the meanAnomaly
     */
    public double meanAnomaly() {
        return meanAnomaly;
    }

}
