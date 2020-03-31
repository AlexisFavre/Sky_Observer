package ch.epfl.rigel.gui;

import static ch.epfl.rigel.Preconditions.checkInInterval;

import java.util.List;

import ch.epfl.rigel.math.ClosedInterval;
import javafx.scene.paint.Color;

/**
 * To simulate the {@code Color} of a BlackBody
 * @author Alexis FAVRE (310552)
 */
public final class BlackBodyColor {

    private static List<Color> loadedList = ColorTemperatureLoader.Instance.load();
    
    private BlackBodyColor() {} // not instanciable

    
    /**
     * give the associated {@code Color} of the BlackBody depending of the round(to hundreds) of its temperature
     *
     * @param temp temperature of the BlackBody
     * @return the associated {@code Color} of the code BlackBody
     * @throws IllegalArgumentException if {@code temp} does not belong in the interval [1 000, 40 000]
     */
    public static  Color colorForTemperature(int temp) throws IllegalArgumentException {
        checkInInterval(ClosedInterval.of(1000, 40000),temp);
        return loadedList.get((int) Math.round(temp/100.0) -10);
    }
}
