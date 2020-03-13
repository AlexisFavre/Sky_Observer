package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

public final class Star extends CelestialObject {

    final private int hipparcosId;
    final private float c;

    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos,
                float magnitude, float colorIndex) throws IllegalArgumentException {
        super(name, equatorialPos, 0, magnitude);
        if(!(hipparcosId<0 || colorIndex < -0.5 || colorIndex > 0.5)) {
            this.hipparcosId = hipparcosId;
            c = colorIndex;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int hipparcosId() {
        return hipparcosId;
    }

    public int colorTemperature() {
        return (int)Math.floor(4600 * (1/(0.92*c + 1.7) + 1/(0.92*c + 0.62)));
    }
}
