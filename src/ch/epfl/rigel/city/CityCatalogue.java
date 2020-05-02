package ch.epfl.rigel.city;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;

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
    // city are ordered compared to their name because the user will enter a name to select a city
    private final Set<City> coordinatesOfTheCity; 

    /**
     * @param coordinatesOfTheCity
     */
    public CityCatalogue() {
        this.coordinatesOfTheCity = load();
    }
    
    private Set<City> load() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(FILE_OF_CITIES), StandardCharsets.US_ASCII))){
            String currentLine;
            TreeSet<City> coordinatesOfTheCity = new TreeSet<>();
            reader.readLine();
            while((currentLine = reader.readLine()) != null) {
                try {
                    String[] lineInfos = currentLine.split(",");
                    String name = (! lineInfos[1].equals("")) ? lineInfos[1].substring(1, lineInfos[1].length()-1) : null;
                    double latitude = (! lineInfos[2].equals("")) ? Double.parseDouble(lineInfos[2].substring(1, lineInfos[2].length()-1)) : 0;
                    double longitude = (! lineInfos[3].equals("")) ? Double.parseDouble(lineInfos[3].substring(1, lineInfos[3].length()-1)) : 0;
                    String country = (! lineInfos[4].equals("")) ? lineInfos[4].substring(1, lineInfos[4].length()-1) : null;
                    if(name != null && country != null) {
                        City c = new City(name, country, GeographicCoordinates.ofDeg(longitude, latitude));
                        coordinatesOfTheCity.add(c);
                    }
                } catch(NumberFormatException e) {
                    //some names of cities contain comma so the line is not correctly split
                    // and Double.ParseDouble try to read words but can't and throws NumberFormatException
                    }
            }
            return Set.copyOf(coordinatesOfTheCity);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * @return the coordinatesOfTheCity
     */
    public Set<City> coordinatesOfTheCity() {
        return coordinatesOfTheCity;
    }
}