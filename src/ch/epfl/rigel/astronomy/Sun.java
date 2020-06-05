package ch.epfl.rigel.astronomy;

import java.util.Objects;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * Represents the Sun at a given location
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
            float angularSize, float meanAnomaly, float distance) throws NullPointerException{
        super("Soleil", equatorialPos, angularSize, -26.7f, distance, distance, distance);
        this.eclipticPos = Objects.requireNonNull(eclipticPos);
        this.meanAnomaly = meanAnomaly;
    }

    /**
     * @return the ecliptic Position of the Sun
     */
    public EclipticCoordinates eclipticPos() {
        return eclipticPos;
    }

    /**
     * @return the meanAnomaly of in radians
     */
    public double meanAnomaly() {
        return meanAnomaly;
    }

}
