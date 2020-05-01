package ch.epfl.rigel.city;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import ch.epfl.rigel.coordinates.GeographicCoordinates;

public class CityCatalogueTest {
    
    CityCatalogue ctc = new CityCatalogue();
    Map<String, GeographicCoordinates> map = ctc.coordinatesOfTheCity();

    @Test
    public void testSizeOfMap() throws Exception {
        assertEquals(12310, map.size());
    }
    
    @Test
    public void printMap() throws Exception {
        for (String s : map.keySet()) {
            System.out.println(s);
        }
    }

}
