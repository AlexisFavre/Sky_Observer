package ch.epfl.rigel.city;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import ch.epfl.rigel.coordinates.GeographicCoordinates;

public class CityCatalogueTest {
    
    private GeographicCoordinates gc = GeographicCoordinates.ofDeg(-79.88, 40.291165502);
    private GeographicCoordinates gc2 = GeographicCoordinates.ofDeg(140, -37.36);
    
    private City c1 = new City("Clairton", "United Stated", gc);
    private City c2 = new City("Penola", "Australia", gc2);
    
    CityCatalogue ctc = new CityCatalogue();
    Set<City> set = ctc.coordinatesOfTheCity();

    @Test
    public void testSizeOfMap() throws Exception {
        assertEquals(13481, set.size());
    }
    
    @Test
    public void printNamesAndCountry() throws Exception {
        for (City c : set) {
            System.out.printf("name : %s  country : %s %n", c.name(), c.country());
        }
    }
    
    @Test
    public void testClairtonLat() throws Exception {
        Object[] tab = set.stream().filter(c -> c.name().equals(c1.name())).toArray();
        City myC = (City) tab[0];
        assertEquals( c1.coordinates().lat(), myC.coordinates().lat(), 1e-1);
    }
    
    @Test
    public void testClairtonLong() throws Exception {
        Object[] tab = set.stream().filter(c -> c.name().equals(c1.name())).toArray();
        City myC = (City) tab[0];
        assertEquals( c1.coordinates().lon(), myC.coordinates().lon(), 1e-1);    }
    
    @Test
    public void testPenolaLat() throws Exception {
        Object[] tab = set.stream().filter(c -> c.name().equals(c2.name())).toArray();
        City myC = (City) tab[0];
        assertEquals( c2.coordinates().lat(), myC.coordinates().lat(), 1e-1);    }
    
    @Test
    public void testPenolaLong() throws Exception {
        Object[] tab = set.stream().filter(c -> c.name().equals(c2.name())).toArray();
        City myC = (City) tab[0];
        assertEquals( c2.coordinates().lon(), myC.coordinates().lon(), 1e-1);    }

}
