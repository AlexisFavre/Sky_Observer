package ch.epfl.rigel.gui;

import static ch.epfl.rigel.Preconditions.checkInInterval;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    private final static List<Color> ALL_TEMPERATURES_COLORS = load();
    
    private final static ClosedInterval RANGE_OF_TEMPERATURES = ClosedInterval.of(1000, 40000); //in Kelvin
    private final static int SMALLEST_TEMPERATURE = 1000;
    private final static double RANGE_BETWEEN_TEMPERATURES = 100;
    
    private final static String DIESE = "#";
    private final static String TYPE_OF_TEMPERATURES = "10deg";
    private final static String NAME_OF_FILE_OF_TEMPERATURES = "/bbr_color.txt";
    
    private final static int INDEX_BEGIN_TYPE_TEMP = 10;
    private final static int INDEX_END_TYPE_TEMP = 15;
    private final static int INDEX_BEGIN_COLOR_RGB = 80;
    private final static int INDEX_END_COLOR_RGB = 87;
    
    private BlackBodyColor() {}

    /**
     * Gives the associated {@code Color} of the BlackBody depending of its temperature
     *
     * @param temp temperature of the black body
     * @return the associated {@code Color} of the code black body
     * @throws IllegalArgumentException if {@code temp} does not belong in the interval [1 000, 40 000]
     */
    public static  Color colorForTemperature(int temp, boolean visible) {
        checkInInterval(RANGE_OF_TEMPERATURES,temp);
        Color c = ALL_TEMPERATURES_COLORS.get((int) Math.round((temp - SMALLEST_TEMPERATURE) / RANGE_BETWEEN_TEMPERATURES));
        
        if(visible)
            return c;
        return c.deriveColor(0, 1, 1, 0.3);
    }
    
    /**
     * Load the set of {@code Color} corresponding to all possible temperatures of a black body
     *
     * @return the {@code Color} set corresponding to all multiples of 100 temperatures
     * @throws UncheckedIOException if I/O error occurs
     */
    private static List<Color> load() {
        List<Color> colorList = new ArrayList<Color>();
        
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(
                BlackBodyColor.class.getResourceAsStream(NAME_OF_FILE_OF_TEMPERATURES), StandardCharsets.US_ASCII));) {
            
            String currentLine;
            while((currentLine = reader.readLine()) != null) {
                
                if(currentLine.charAt(0) != DIESE.charAt(0) 
                        && currentLine.substring(INDEX_BEGIN_TYPE_TEMP, INDEX_END_TYPE_TEMP)
                           .equals(TYPE_OF_TEMPERATURES)) {
                    
                    colorList.add(Color.web(currentLine.substring
                            (INDEX_BEGIN_COLOR_RGB, INDEX_END_COLOR_RGB)));
                }
            }
            
        } catch (FileNotFoundException e) {
             System.err.println("No such file found : " + NAME_OF_FILE_OF_TEMPERATURES);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return colorList;
    }
}
