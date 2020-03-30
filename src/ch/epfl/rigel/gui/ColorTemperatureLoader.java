package ch.epfl.rigel.gui;

import java.awt.Color; //TODO color de jfx
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public enum ColorTemperatureLoader {

    Instance;
    
    public List<Color> load() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/bbr_color.txtTEST")));){
            List<Color> colorList = new ArrayList<Color>();
            String currentLine;
            while((currentLine = reader.readLine()) != null) {
                if(currentLine.charAt(0) != "#".charAt(0) && currentLine.substring(10, 15) == "10deg") {
                    colorList.add(web(currentLine.substring(80, 87)));
                }
            }
            
            
            
            
            
            
            
        } catch (FileNotFoundException e) {
             System.err.println("No such File found : bbr_color.txtTEST");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return null;
    }
        
    
    public Color web(String s) {
        return null; //TODO use web of javaFX
    }

}