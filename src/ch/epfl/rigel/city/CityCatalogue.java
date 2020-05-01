package ch.epfl.rigel.city;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

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
public final class CityCatalogue extends Catalogue{
    
    private final static String FILE_OF_CITIES = "/worldcities.csv";
    private final Map<String, GeographicCoordinates> coordinatesOfTheCity;

    /**
     * @param coordinatesOfTheCity
     */
    public CityCatalogue() {
        this.coordinatesOfTheCity = load();
    }
    
    private Map<String, GeographicCoordinates> load() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(FILE_OF_CITIES), StandardCharsets.US_ASCII))){
            String currentLine;
            TreeMap<String, GeographicCoordinates> coordinatesOfTheCity = new TreeMap<>();
            int i = 0;
            reader.readLine();
            while((currentLine = reader.readLine()) != null) {
                try {
                String[] lineInfos = currentLine.split(",");
                String name = (! lineInfos[1].equals("")) ? lineInfos[1] : null;
                double latitude = (! lineInfos[2].equals("")) ? Double.parseDouble(lineInfos[2].substring(1, lineInfos[2].length()-2)) : 0;
                //double latitude = (! lineInfos[2].equals("")) ? Double.parseDouble(lineInfos[2]) : 0;
                double longitude = (! lineInfos[3].equals("")) ? Double.parseDouble(lineInfos[3].substring(1, lineInfos[3].length()-2)) : 0;
                //double longitude = (! lineInfos[3].equals("")) ? Double.parseDouble(lineInfos[3]) : 0;
                String country = (! lineInfos[4].equals("")) ? lineInfos[4] : null;
                if(name != null && country != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(name).append(" (").append(country).append(")");
                    GeographicCoordinates coordinates = GeographicCoordinates.ofDeg(longitude, latitude);
                    coordinatesOfTheCity.put(sb.toString(), coordinates);
//                    ++i;
//                    System.out.println(i);
                }
                } catch(Exception e) {
//                    
                } finally {}
            }
            return Map.copyOf(coordinatesOfTheCity);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * @return the coordinatesOfTheCity
     */
    public Map<String, GeographicCoordinates> coordinatesOfTheCity() {
        return coordinatesOfTheCity;
    }
    
    //========================================================================================================
    
    public final static class Builder extends Catalogue.Builder{
        private TreeMap<String, GeographicCoordinates> coordinatesOfTheCity;

        /**
         * @param coordinatesOfTheCity
         */
        public Builder() {this.coordinatesOfTheCity = new TreeMap<>();}

        /**
         * @return the coordinatesOfTheCity
         */
        public TreeMap<String, GeographicCoordinates> getCoordinatesOfTheCity() {
            return (TreeMap<String, GeographicCoordinates>) Collections.unmodifiableMap(coordinatesOfTheCity);
        }
        
        /**
         * 
         * @param city to add to the builder of the catalogue
         */
        public void addCity(City city) {
            coordinatesOfTheCity.put(city.getName(), city.getCoordinates());
        }
        
        /**
         * @return a new CityCatalogue with the properties of the builder
         */
        @Override
        public CityCatalogue build() {
            return new CityCatalogue();
        }
    }
}