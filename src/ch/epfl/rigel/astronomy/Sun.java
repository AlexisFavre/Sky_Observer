package ch.epfl.rigel.astronomy;

import java.util.Objects;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * Represents the Sun
 * The characteristics of the Sun are computed with a {@code SunModel}
 *
 * @author Alexis FAVRE (310552)
 */
public final class Sun extends CelestialObject {

    private final EclipticCoordinates eclipticPos;
    private final float meanAnomaly; // in radians

    /**
     *
     * @param eclipticPos of the Sun
     * @param equatorialPos of the Sun (notNull)
     * @param angularSize of the Sun
     * @param meanAnomaly of the Sun
     * @throws NullPointerException if the eclipticPos is Null
     */
    public Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos,
            float angularSize, float meanAnomaly) throws NullPointerException{
        super("Soleil", equatorialPos, angularSize, -26.7f);
        this.eclipticPos = Objects.requireNonNull(eclipticPos);
        this.meanAnomaly = meanAnomaly;
    }

    /**
     *
     * @return the eclipticPos
     */
    public EclipticCoordinates eclipticPos() {
        return eclipticPos;
    }

    /**
     *
     * @return the meanAnomaly in radians
     */
    public double meanAnomaly() {
        return meanAnomaly;
    }

}
