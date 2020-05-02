package ch.epfl.rigel.city;
import static java.util.Objects.requireNonNull;
import ch.epfl.rigel.coordinates.GeographicCoordinates;

/**
 * represent a city (on Earth)
 * @author Alexis FAVRE (310552)
 */
public final class City implements Comparable<City>{
    
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
        this.name = requireNonNull(name);
        this.country = requireNonNull(country);
        this.coordinates = requireNonNull(coordinates);
    }

    /**
     * @return the name
     */
    public String name() {
        return name;
    }

    /**
     * @return the country
     */
    public String country() {
        return country;
    }

    /**
     * @return the coordinates
     */
    public GeographicCoordinates coordinates() {
        return coordinates;
    }
    
    /**
     * compare object to this with their name attribute
     * @param o the object to compare with {@code this}
     */
    @Override
    public int compareTo(City o) {
        if(o == null)
        return -100000;
        City other = (City) o;
        return this.name.compareTo(other.name);
    }
}
