package ch.epfl.rigel.gui;

import static ch.epfl.rigel.Preconditions.checkInInterval;

import java.util.List;

import ch.epfl.rigel.math.ClosedInterval;
import javafx.scene.paint.Color;

/**
 * Non instantiable class containing only {@code static} methods
 *
 * Used to obtain the {@code Color} of a black body
 *
 * @author Alexis FAVRE (310552)
 * @see ColorTemperatureLoader
 */
public final class BlackBodyColor {

    // contains only colors corresponding to multiples of 100 temperatures
    private static List<Color> allTemperatureColors = ColorTemperatureLoader.INSTANCE.load();
    
    private BlackBodyColor() {}

    /**
     * Gives the associated {@code Color} of the BlackBody depending of its temperature
     *
     * @param temp temperature of the black body
     * @return the associated {@code Color} of the code black body
     * @throws IllegalArgumentException if {@code temp} does not belong in the interval [1 000, 40 000]
     */
    public static  Color colorForTemperature(int temp) throws IllegalArgumentException {
        checkInInterval(ClosedInterval.of(1000, 40000),temp);
        return allTemperatureColors.get((int) Math.round(temp/100.0) -10);
    }
}
