package ch.epfl.rigel.gui;

import java.awt.Color; // TODO must be JFXColor 

import ch.epfl.rigel.math.ClosedInterval;
import static ch.epfl.rigel.Preconditions.*;

public class BlackBodyColor {

    private BlackBodyColor() {}

    /**
     * 
     * @param temp temperature of the BlackBody
     * @return the associated Color of the BlackBody
     * @throws IllegalArgumentException if {@code temp} does not belong to [1000, 40 000]
     */
    public Color colorForTemperature(int temp) throws IllegalArgumentException {
        checkInInterval(ClosedInterval.of(1000, 40000),temp);
    
    
    }
}
