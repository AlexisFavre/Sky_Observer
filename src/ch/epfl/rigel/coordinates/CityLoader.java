package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Loader;
import ch.epfl.rigel.astronomy.StarCatalogue;

import java.io.IOException;
import java.io.InputStream;

public enum CityLoader implements Loader {

    INSTANCE;

    //================================================================================================

    @Override
    public void load(InputStream inputStream,  builder) throws IOException {

    }

    private enum Id {
        CITY, CITY_ASCII, LAT, LNG, COUNTRY, ISO2, ISO3,
        ADMIN_NAME, CAPITAL, POPULATION, ID;
    }
}
