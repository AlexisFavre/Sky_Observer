package ch.epfl.rigel.city;

import ch.epfl.rigel.coordinates.GeographicCoordinates;

/**
 * represent a city (on Earth)
 * @author Alexis FAVRE (310552)
 */
public final class City {
    
    private final String name;
    private final String country;
    private final GeographicCoordinates coordinates;
    
    /**
     * @param name
     * @param country
     * @param coordinates
     */
    public City(String name, String country,
            GeographicCoordinates coordinates) {
        this.name = name;
        this.country = country;
        this.coordinates = coordinates;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return the coordinates
     */
    public GeographicCoordinates getCoordinates() {
        return coordinates;
    }
    
    
}
