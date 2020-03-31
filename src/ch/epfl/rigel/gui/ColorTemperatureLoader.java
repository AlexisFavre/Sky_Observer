package ch.epfl.rigel.gui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

/**
 * Used to load the Color associated to the Temperature(in Kelvin) of a BlackBody
 * form the file /bbr_color.txt
 * Used in {@code BlackBodyColor}
 * 
 * @author Alexis FAVRE (310552)
 */
public enum ColorTemperatureLoader {

    Instance;
    
    /**
     * @return the given List which contains the Color associated 
     * to the Temperature(in Kelvin) of a BlackBody
     * @throws if I/O error occurs
     */
    public List<Color> load() throws UncheckedIOException {
        List<Color> colorList = new ArrayList<Color>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(
                        getClass().getResourceAsStream(("/bbr_color.txt"))));) {
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