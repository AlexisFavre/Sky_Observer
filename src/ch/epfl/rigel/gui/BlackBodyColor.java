package ch.epfl.rigel.gui;

import static ch.epfl.rigel.Preconditions.checkInInterval;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.time.LocalTime;
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
 * //@see ColorTemperatureLoader
 */
public final class BlackBodyColor {

    // contains only colors corresponding to multiples of 100 temperatures
    private final static List<Color> allTemperatureColors = load();
    
    private BlackBodyColor() {}

    public static Color skyColorAt(LocalTime time) {
        double advancementOfDay = time.toSecondOfDay()/86400.0;
        System.out.println(time);
        //double sid = 86400;
        return Color.color(0.1 * (1 - Math.cos((advancementOfDay-0.15)*6.28)),
                0.15 * (1 - Math.cos((advancementOfDay-0.15)*6.28)),
                0.25 * (1 - Math.cos((advancementOfDay-0.15)*6.28)),
                1-(0.1 * (1 - Math.cos((advancementOfDay-0.15)*6.28))));
    }

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
    
    /**
     * Load the set of {@code Color} corresponding to all possible temperatures of a black body
     *
     * @return the {@code Color} set corresponding to all multiples of 100 temperatures
     * @throws UncheckedIOException if I/O error occurs
     */
    private static List<Color> load() throws UncheckedIOException {
        List<Color> colorList = new ArrayList<Color>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(
                BlackBodyColor.class.getResourceAsStream(("/bbr_color.txt"))));) {
            String currentLine;
            while((currentLine = reader.readLine()) != null) {
                if(currentLine.charAt(0) != "#".charAt(0) && currentLine.substring(10, 15).equals("10deg")) {
                    colorList.add(Color.web(currentLine.substring(80, 87)));
                }
            }
            
        } catch (FileNotFoundException e) {
             System.err.println("No such File found : bbr_color.txtTEST");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return colorList;
    }
}
