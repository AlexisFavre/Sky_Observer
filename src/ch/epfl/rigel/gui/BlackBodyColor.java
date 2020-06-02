package ch.epfl.rigel.gui;

import static ch.epfl.rigel.Preconditions.checkInInterval;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.scene.paint.Color;

/**
 * Non instantiable class containing only {@code static} methods
 *
 * Used to obtain the {@code Color} of a black body
 *
 * @author Alexis FAVRE (310552)
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

    private final static Color NIGHT_COLOR = Color.BLACK;
    private final static Color DAY_COLOR = Color.web("#1A384E");
    public static final Color LIGHT_NIGHT_COLOR = Color.web("0F0F26");
    public static final Color LIGHT_DAY_COLOR = Color.web("121E35");
    public static final Color TEST = Color.color(0.0f, 0.54509807f, 0.54509807f, 1.0f);

    // contains only colors corresponding to multiples of 100 temperatures
    private final static List<Color> allTemperatureColors = load();

    private BlackBodyColor() {}

    public static Color colorForSky(LocalTime time) {
        int t = time.getHour(); // TODO try with hexadecimal suffix for rgb and continuous
        if(t < 17 && t > 10) {
            return DAY_COLOR;
        } else if((t <= 8 && t >= 6) || (t <= 21 && t >= 19)) {
            return LIGHT_NIGHT_COLOR;
        } else if((t <= 10 && t > 8) || (t < 19 && t >= 17)) {
            return LIGHT_DAY_COLOR;
        }
        return NIGHT_COLOR;
    }

    public static Color colorForPath(HorizontalCoordinates from, HorizontalCoordinates to) {
        double opacity = 1;
        if(from.altDeg() < 0 || to.altDeg() < 0)
            opacity = 0.2;
        return Color.BLUE.deriveColor(0, 1, 1, opacity);
    }

    /**
     * Gives the associated {@code Color} of the BlackBody depending of its temperature and position
     *
     * @param temp temperature of the black body
     * @return the associated {@code Color} of the code black body
     * @throws IllegalArgumentException if {@code temp} does not belong in the interval [1 000, 40 000]
     */
    public static  Color starColorForParameters(int temp, HorizontalCoordinates position) throws IllegalArgumentException {
        checkInInterval(ClosedInterval.of(1000, 40000),temp);
        double opacity = 1;
        if(position.altDeg() < 0)
            opacity = 0.3;
        return allTemperatureColors.get((int) Math.round(temp/100.0) -10)
                .deriveColor(0, 1, 1, opacity);
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
