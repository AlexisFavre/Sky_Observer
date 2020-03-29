package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

class MyStarCatalogueTest {

    private static final String HYG_CATALOGUE_NAME =
            "/hygdata_v3.csv";

    private static final String ASTERISM_CATALOGUE_NAME =
            "/asterisms.txt";

    public StarCatalogue initializedCatalog() throws IOException {
        try (InputStream hygStream = getClass().getResourceAsStream(MyStarCatalogueTest.HYG_CATALOGUE_NAME);
             InputStream aStream = getClass().getResourceAsStream(MyStarCatalogueTest.ASTERISM_CATALOGUE_NAME)) {
            return new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .loadFrom(aStream, AsterismLoader.INSTANCE).build();
        }
    }

    protected Star loadedRigel() throws IOException {
        Star rigel = null;
        for (Star s : initializedCatalog().stars()) {
            if (s.name().equalsIgnoreCase("rigel"))
                rigel = s;
        }
        return rigel;
    }

    protected Star loadedSirius() throws IOException {
        Star sirius = null;
        for (Star s : initializedCatalog().stars()) {
            if (s.name().equalsIgnoreCase("sirius"))
                sirius = s;
        }
        return sirius;
    }

    protected Star loadedBetelgeuse() throws IOException {
        Star betelgeuse = null;
        for (Star s : initializedCatalog().stars()) {
            if (s.name().equalsIgnoreCase("betelgeuse"))
                betelgeuse = s;
        }
        return betelgeuse;
    }
    
    protected Asterism asterismOfRigel() throws IOException {
        for (Asterism ast : initializedCatalog().asterisms()) {
            System.out.println(ast);
            if(ast.stars().contains(loadedRigel())) {
                System.out.println("OK");
                return ast;
            }
        }
        return null;
    }
    
    
    @Test
    void checkIndexListRigel() throws IllegalArgumentException, IOException {
        List<Integer> list = initializedCatalog().asterismIndices(asterismOfRigel());
        assertEquals(1019, list.get(0));
    }
    
    @Test
    void checkRigelisInTheAsterism() throws IOException {
        assertNotNull(asterismOfRigel());
    }

    @Test
    void checkSizeStarCatalogue() throws IOException {
        System.out.println(initializedCatalog().asterisms().size());
    }
    @Test
    // CE TEST NE SERT A RIEN IL EST FORCEMENT JUSTE CAR CONDITION CODEE POUR CONSTRUCTION DU CATALOG
            // REGARDE LE CODE!!!!
    // TEST PLUTOT LE NB D ETOILE OU UN TRUC DANS LE STYLE POUR VOIR ILS TE RENVOIENT BIEN TOUT
    void getters() {
        /*try{
            boolean b = false;
            for (Asterism asterism : initializedCatalog().asterisms()) {
                b = initializedCatalog().stars().containsAll(asterism.stars());
                if(b == false) { throw new Error();}
            }
            assertFalse(false);
        }
        catch(Exception e){
            assertFalse(true);
        }*/
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

