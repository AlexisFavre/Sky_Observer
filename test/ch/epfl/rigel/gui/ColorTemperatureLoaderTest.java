package ch.epfl.rigel.gui;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.List;

import org.junit.jupiter.api.Test;

//TODO Color jfx
class ColorTemperatureLoaderTest {
    // List of the ColorAssociated to the temperature 100i (i index dans list)
    private List<Color> loadedList = ColorTemperatureLoader.Instance.load();

    
    @Test
    void checkSizeOfTheListReturnedBy_ColorTemperatureLoader() throws Exception {
        assertEquals(391, loadedList.size());
    }
    
    @Test
    void checkListOfColorsNotNull() throws Exception {
        assertNotNull(loadedList);
    }
    
    @Test
    void visualTestOfLoadingOfTheList() throws Exception {
        for (Color color : loadedList) {
            System.out.println(color);
        }
    }
    
    @Test
    void checkColorForTemperatureOf10500K() throws Exception {
        assertEquals(web("#c8d9ff"), BlackBodyColor.colorForTemperature(10500));
    }
    
    @Test
    void checkColorForTemperatureOf1000K() throws Exception {
        assertEquals(web("#ff3800"), BlackBodyColor.colorForTemperature(1000));
    }
    
    @Test
    void checkColorForTemperatureOf4900K() throws Exception {
        assertEquals(web("#ffe3ca"), BlackBodyColor.colorForTemperature(4900));
    }
    
    @Test
    void checkColorForTemperatureOf11600K() throws Exception {
        assertEquals(web("#c1d4ff"), BlackBodyColor.colorForTemperature(11600));
    }
    
    @Test
    void checkColorForTemperatureOf40000K() throws Exception {
        assertEquals(web("#9bbcff"), BlackBodyColor.colorForTemperature(40000));
    }
    
    @Test
    void checkThat_ColorForTemperature_GivesTheHighBoond1() throws Exception {
        assertEquals(web("#a2c0ff"), BlackBodyColor.colorForTemperature(25899));
    }
    
    @Test
    void checkThat_ColorForTemperature_GivesTheHighBoond2() throws Exception {
        assertEquals(web("#a2c0ff"), BlackBodyColor.colorForTemperature(25850));
    }
    
    @Test
    void checkThat_ColorForTemperature_GivesTheLowBound1() throws Exception {
        assertEquals(web("#a2c1ff"), BlackBodyColor.colorForTemperature(25849));
    }
    
    @Test
    void checkThat_ColorForTemperature_GivesTheLowBound2() throws Exception {
        assertEquals(web("#a2c1ff"), BlackBodyColor.colorForTemperature(25801));
    }
    public Color web(String s) {
        return null; //TODO use web of javaFX
    }

}
