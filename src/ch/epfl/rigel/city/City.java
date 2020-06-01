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
    public City(String name, String country, GeographicCoordinates coordinates) {
        this.name        = requireNonNull(name);
        this.country     = requireNonNull(country);
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
     * @return the {@link GeographicCoordinates}
     */
    public GeographicCoordinates coordinates() {
        return coordinates;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s)", name, country);
    }

    @Override
    //in our files, some cities appear several times with each time
    // a little bit different coordinates 
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        City other = (City) obj;
        if (country == null) {
            if (other.country != null)
                return false;
        } else if (!country.equals(other.country))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
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
