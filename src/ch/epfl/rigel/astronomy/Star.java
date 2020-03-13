package ch.epfl.rigel.astronomy;

import static ch.epfl.rigel.Preconditions.checkArgument;
import static ch.epfl.rigel.Preconditions.checkInInterval;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

public final class Star extends CelestialObject {

    private final int hipparcosId;
    private final float c;

    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos,
                float magnitude, float colorIndex) throws IllegalArgumentException {
        super(name, equatorialPos, 0, magnitude);
        checkArgument(hipparcosId>=0 || colorIndex < -0.5 || colorIndex > 0.5);
        c = colorIndex;
        checkInInterval(ClosedInterval.of(-0.5, 5.5), hipparcosId);
        this.hipparcosId = hipparcosId;
    }
    
    /**
     * 
     * @return hipparcosId
     */
    public int hipparcosId() {
        return hipparcosId;
    }

    /**
     * 
     * @return
     * colorTemperature of the star in degree Kelvin
     */
    public int colorTemperature() {
        return (int) Math.floor(1/(0.92*c + 1.7) + 1/(0.92*c + 0.62))*4600;
    }
}
