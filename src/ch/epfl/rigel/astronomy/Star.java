package ch.epfl.rigel.astronomy;

import static ch.epfl.rigel.Preconditions.checkArgument;
import static ch.epfl.rigel.Preconditions.checkInInterval;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

/**
 * Describes a Star
 * Characteristic are extracted with a {@code StarCatalogue}
 *
 * @author Augustin ALLARD (299918)
 */
public final class Star extends CelestialObject {

    private final int hipparcosId;
    private final float c;

    /**
     * @param hipparcosId of the Star (must be positive or null)
     * @param name of the Star
     * @param equatorialPos of the Star
     * @param magnitude of the Star
     * @param colorIndex of the Star (must be in[-0.5, +5.5])
     * @throws IllegalArgumentException if the hipparcosId < 0 or the colorIndex is not in [-0.5, +5.5]
     */
    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos,
                float magnitude, float colorIndex) throws IllegalArgumentException {
        super(name, equatorialPos, 0, magnitude);
        checkArgument(hipparcosId>=0);
        checkInInterval(ClosedInterval.of(-0.5, 5.5), colorIndex);
        this.hipparcosId = hipparcosId;
        c = colorIndex;
    }
    
    /**
     * @return the hipparcosId of the star
     */
    public int hipparcosId() {
        return hipparcosId;
    }
    
    /**
     * @return hashCode of the hipparcosId of the Star
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(hipparcosId);
    }

    /**
     * @return the colorTemperature of the star in degree Kelvin
     */
    public int colorTemperature() {
        return (int) Math.floor((1/(0.92*c + 1.7) + 1/(0.92*c + 0.62))*4600);
    }
}
