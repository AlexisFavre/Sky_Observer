package ch.epfl.rigel.astronomy;

import java.util.Objects;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * 
 * @author Alexis FAVRE (310552)
 *
 */
public final class Sun extends CelestialObject {

    private final EquatorialCoordinates eclipticPos;
    private final float meanAnomaly;

    public Sun(EquatorialCoordinates eclipticPos, EquatorialCoordinates equatorialPos,
            float angularSize, float meanAnomaly) {
        super("Sun", equatorialPos, angularSize, -26.7f);
        this.eclipticPos = Objects.requireNonNull(eclipticPos);
        this.meanAnomaly = meanAnomaly;
    }

    /**
     * @return the eclipticPos
     */
    public EquatorialCoordinates eclipticPos() {
        return eclipticPos;
    }

    /**
     * @return the meanAnomaly
     */
    public double meanAnomaly() {
        return meanAnomaly;
    }

}
