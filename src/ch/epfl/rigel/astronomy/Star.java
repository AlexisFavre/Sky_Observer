package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

public final class Star extends CelestialObject { // TODO veriy immuable

    private int hipparcosId;
    private float c;

    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos,
                float magnitude, float colorIndex) throws IllegalArgumentException {  // TODO exception
        super(name, equatorialPos, 0, magnitude);
        this.hipparcosId = hipparcosId;
        c = colorIndex;
    }

    public int hipparcosId() {
        return hipparcosId;
    }

    public int colorTemperature() {
        return (int)Math.round(4600 * (1/(0.92*c + 1.7) + 1/(0.92*c + 0.62))); // TODO transtypage
    }
}
