package ch.epfl.rigel.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import javafx.scene.paint.Color;


class ColorTemperatureLoaderTest {
    
    @Test
    void throwIAEifTemperatureSmallerThan1000() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {BlackBodyColor.colorForTemperature(999);});
    }
    
    @Test
    void throwIAEifTemperatureBiggerThan40000() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {BlackBodyColor.colorForTemperature(40001);});
    }
    
    @Test
    void checkColorForTemperatureOf10500K() throws Exception {
        assertEquals(Color.web("#c8d9ff"), BlackBodyColor.colorForTemperature(10500));
    }
    
    @Test
    void checkColorForTemperatureOf1000K() throws Exception {
        assertEquals(Color.web("#ff3800"), BlackBodyColor.colorForTemperature(1000));
    }
    
    @Test
    void checkColorForTemperatureOf4900K() throws Exception {
        assertEquals(Color.web("#ffe3ca"), BlackBodyColor.colorForTemperature(4900));
    }
    
    @Test
    void checkColorForTemperatureOf11600K() throws Exception {
        assertEquals(Color.web("#c1d4ff"), BlackBodyColor.colorForTemperature(11600));
    }
    
    @Test
    void checkColorForTemperatureOf40000K() throws Exception {
        assertEquals(Color.web("#9bbcff"), BlackBodyColor.colorForTemperature(40000));
    }
    
    @Test
    void checkThat_ColorForTemperature_GivesTheHighBoond1() throws Exception {
        assertEquals(Color.web("#a2c0ff"), BlackBodyColor.colorForTemperature(25899));
    }
    
    @Test
    void checkThat_ColorForTemperature_GivesTheHighBoond2() throws Exception {
        assertEquals(Color.web("#a2c0ff"), BlackBodyColor.colorForTemperature(25850));
    }
    
    @Test
    void checkThat_ColorForTemperature_GivesTheLowBound1() throws Exception {
        assertEquals(Color.web("#a2c1ff"), BlackBodyColor.colorForTemperature(25849));
    }
    
    @Test
    void checkThat_ColorForTemperature_GivesTheLowBound2() throws Exception {
        assertEquals(Color.web("#a2c1ff"), BlackBodyColor.colorForTemperature(25801));
    }
}