package ch.epfl.rigel.astronomy;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.astronomy.StarCatalogue.Builder;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

class MyStarCatalogueTest {

    private static final String HYG_CATALOGUE_NAME =
            "/hygdata_v3.csv";

    private static final String ASTERISM_CATALOGUE_NAME =
            "/asterisms.txt";

    public StarCatalogue initCatalog() throws IOException {
        try (InputStream hygStream = getClass().getResourceAsStream(MyStarCatalogueTest.HYG_CATALOGUE_NAME);
             InputStream aStream = getClass().getResourceAsStream(MyStarCatalogueTest.ASTERISM_CATALOGUE_NAME)) {
            return new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .loadFrom(aStream, AsterismLoader.INSTANCE).build();
        }
    }
    public Star star = new Star(9999999, "starTest", EquatorialCoordinates.of(0, 0), 0, 0);
    public Asterism asterism = new Asterism(List.of(star));
    public StarCatalogue.Builder bd = new Builder();
    
   @Test
   void addStarBuilderWorks() {
       int s1 = bd.stars().size();
       bd.addStar(star);
       int s2 = bd.stars().size();
       assertTrue( s1 == (s2-1) && bd.stars().contains(star));
   }
   
   @Test
   void addAsterismBuilderWorks() {
       int s1 = bd.asterisms().size();
       bd.addAsterism(asterism);
       int s2 = bd.asterisms().size();
       assertTrue( s1 == (s2-1) && bd.asterisms().contains(asterism));
   }

    protected Star loadedRigel() throws IOException {
        Star rigel = null;
        for (Star s : initCatalog().stars()) {
            if (s.name().equalsIgnoreCase("rigel"))
                rigel = s;
        }
        return rigel;
    }
    
    protected Asterism AsterismOfRigelserachWithName1() throws IOException {
        for (Asterism ast : initCatalog().asterisms()) {
            for(Star star : ast.stars()) {
                if (star.name().equalsIgnoreCase("Rigel")) {
                    return ast;
                }
            }
        }
        return null;
    }
    
    protected Asterism AsterismOfRigelserachWithName2() throws IOException {
        for (Asterism ast : initCatalog().asterisms()) {
            for(Star star : ast.stars()) {
                if (star.name().equalsIgnoreCase("Rigel") && ast != AsterismOfRigelserachWithName1()) {
                    return ast;
                }
            }
        }
        return null;
    }

    protected Star loadedSirius() throws IOException {
        Star sirius = null;
        for (Star s : initCatalog().stars()) {
            if (s.name().equalsIgnoreCase("sirius"))
                sirius = s;
        }
        return sirius;
    }

    protected Star loadedBetelgeuse() throws IOException {
        Star betelgeuse = null;
        for (Star s : initCatalog().stars()) {
            if (s.name().equalsIgnoreCase("betelgeuse"))
                betelgeuse = s;
        }
        return betelgeuse;
    }
    
    @Test
    void checkIndexListRigel() throws IllegalArgumentException, IOException {
        List<Integer> list = initCatalog().asterismIndices(AsterismOfRigelserachWithName2());
        assertEquals(1019, list.get(0));
    }
    
    @Test
    void indexBetelgeuse() throws IOException {
        assertEquals(27989, loadedBetelgeuse().hipparcosId());
    }

    @Test
    void indexRigel() throws IOException {
        MyHygDatabaseLoaderTest hygL = new MyHygDatabaseLoaderTest();
        assertEquals(24436, loadedRigel().hipparcosId());
    }
}

