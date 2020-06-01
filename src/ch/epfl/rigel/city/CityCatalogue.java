package ch.epfl.rigel.city;

import static ch.epfl.rigel.math.RightOpenInterval.SymmetricROInterOfSize360;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.rigel.coordinates.GeographicCoordinates;

/**
 * {@code CityCatalogue} represents a catalog of cities of all the wolrd
 * It is mostly created via its {@code CityCatalogue.Builder} that is able to load cities
 * via a stream and a {@code CityCatalogue.Loader}
 * (ex to create a catalog from 2 streams that contains respectively the cities data:
 *     {@code new StarCatalogue.Builder().loadFrom(cityFileName, CityCatalogueLoader.INSTANCE);
 *  returns a {@code CityCatalogue} containing all the objects corresponding to file data)
 * @author Alexis FAVRE (310552)
 */
public final class CityCatalogue{
    
    private final static String FILE_OF_CITIES = "/worldcities.csv";
    private final static City EPFL = new City("Epfl", "Switzerland", GeographicCoordinates.ofDeg(6.57, 46.52));
    private final static List<City> AVAILABLE_CITIES = load(); 


    private CityCatalogue() {}
    
    private static List<City> load() { // apostrophe not included in ASCII so use UTF-8
        
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(
                CityCatalogue.class.getResourceAsStream(FILE_OF_CITIES), StandardCharsets.UTF_8))){
            
            String currentLine;
            List<City> cities = new ArrayList<>();
            cities.add(EPFL);

            
            reader.readLine();
            while((currentLine = reader.readLine()) != null) {
                try {
                    String[] lineInfos = currentLine.split(",");
                    String name = (! lineInfos[1].equals("")) ? lineInfos[1].substring(1, lineInfos[1].length()-1) : null;
                    double latitude = (! lineInfos[2].equals("")) ? Double.parseDouble(lineInfos[2].substring(1, lineInfos[2].length()-1)) : 0;
                    double longitude = (! lineInfos[3].equals("")) ? Double.parseDouble(lineInfos[3].substring(1, lineInfos[3].length()-1)) : 0;
                    String country = (! lineInfos[4].equals("")) ? lineInfos[4].substring(1, lineInfos[4].length()-1) : null;
                    if(name != null && country != null) {
                        City c = new City(name, country, GeographicCoordinates.ofDeg(SymmetricROInterOfSize360.reduce(longitude), latitude));
                        if(! cities.contains(c))
                                cities.add(c);
                    }
                } catch(NumberFormatException e) {
                    //some names of cities contain comma so the line is not correctly split
                    // and Double.ParseDouble fails when trying to read words and throws NumberFormatException
                    }
            }
            Collections.sort(cities);
            return List.copyOf(cities);
            
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * @return the coordinatesOfTheCity
     */
    public static List<City> availableCities() {
        return AVAILABLE_CITIES;
    }
    
    /**
     * @return the epfl city
     */
    public static City epfl() {
        return EPFL;
    }
}