package ch.epfl.rigel.astronomy;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.epfl.rigel.astronomy.StarCatalogue.Builder;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

class MyStarCatalogueTest {

    protected final StarCatalogue CATALOG = initCatalog();

    private static final String HYG_CATALOGUE_NAME =
            "/hygdata_v3.csv";

    private static final String ASTERISM_CATALOGUE_NAME =
            "/asterisms.txt";

    private StarCatalogue initCatalog() {
        try (InputStream hygStream = getClass().getResourceAsStream(MyStarCatalogueTest.HYG_CATALOGUE_NAME);
             InputStream aStream = getClass().getResourceAsStream(MyStarCatalogueTest.ASTERISM_CATALOGUE_NAME)) {
            return new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .loadFrom(aStream, AsterismLoader.INSTANCE).build();
        } catch (IOException e) {
            return null;
        }
    }
    
  //============================================================================
    // to check if methods add Star or Asterism of the Builder works
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
   
   //============================================================================
   //to take these Star from the catalog
    protected Star loadedRigel() {
        Star rigel = null;
        for (Star s : CATALOG.stars()) {
            if (s.name().equalsIgnoreCase("rigel"))
                rigel = s;
        }
        return rigel;
    }

    protected Star loadedSirius() {
        Star sirius = null;
        for (Star s : CATALOG.stars()) {
            if (s.name().equalsIgnoreCase("sirius"))
                sirius = s;
        }
        return sirius;
    }

    protected Star loadedBetelgeuse() {
        Star betelgeuse = null;
        for (Star s : CATALOG.stars()) {
            if (s.name().equalsIgnoreCase("betelgeuse"))
                betelgeuse = s;
        }
        return betelgeuse;
    }
    
    
  //============================================================================
    // to take the asterism which contains these stars
    protected Asterism Asterism1OfRigelserachWithName() {
        for (Asterism ast : CATALOG.asterisms()) {
            for(Star star : ast.stars()) {
                if (star.name().equalsIgnoreCase("Rigel")) {
                    return ast;
                }
            }
        }
        return null;
    }
    
    protected Asterism Asterism2OfRigelserachWithName() {
        for (Asterism ast : CATALOG.asterisms()) {
            for(Star star : ast.stars()) {
                if (star.name().equalsIgnoreCase("Rigel") && ast != Asterism1OfRigelserachWithName()) {
                    return ast;
                }
            }
        }
        return null;
    }
    
    protected Asterism asterismOfRigel() {
        for (Asterism ast : CATALOG.asterisms()) {
            if(ast.stars().contains(loadedRigel())) {
                return ast;
            }
        }
        return null;
    }
    
    protected Asterism asterismOfBetelgeuse() {
        for (Asterism ast : CATALOG.asterisms()) {
            if(ast.stars().contains(loadedBetelgeuse())) {
                return ast;
            }
        }
        return null;
    }
    
    protected Asterism Asterism1OfBetelgeuseserachWithName() {
        for (Asterism ast : CATALOG.asterisms()) {
            for(Star star : ast.stars()) {
                if (star.name().equalsIgnoreCase("Betelgeuse")) {
                    return ast;
                }
            }
        }
        return null;
    }
    
    
    protected Asterism Asterism2OfBetelgeuseserachWithName() {
        for (Asterism ast : CATALOG.asterisms()) {
            for(Star star : ast.stars()) {
                if (star.name().equalsIgnoreCase("Betelgeuse") && ast != Asterism1OfBetelgeuseserachWithName()) {
                    return ast;
                }
            }
        }
        return null;
    }
    
    protected Asterism asterismOfSirius() {
        for (Asterism ast : CATALOG.asterisms()) {
            if(ast.stars().contains(loadedSirius())) {
                return ast;
            }
        }
        return null;
    }

    
  //============================================================================
    // check index of the asterisms
    @Test
    void checkIndexInListStarsOfCatalogOfBetelgeuse() throws IllegalArgumentException {
        List<Integer> list = CATALOG.asterismIndices(Asterism1OfBetelgeuseserachWithName());
        List<Integer> list2 = CATALOG.asterismIndices(Asterism2OfBetelgeuseserachWithName());
        assertTrue(1213 == list.get(0) || 1213 == list.get(3));
    }
    
    @Test
    void checkIndexInListStarsOfCatalogOfSirius() throws IllegalArgumentException {
        List<Integer> list = CATALOG.asterismIndices(asterismOfSirius());
        assertEquals(1419, list.get(2));
    }
    
    @Test
    void checkIndexInListStarsOfCatalogOfRigel() throws IllegalArgumentException {
        List<Integer> list = CATALOG.asterismIndices(Asterism1OfRigelserachWithName());
        List<Integer> list2 = CATALOG.asterismIndices(Asterism2OfRigelserachWithName());
        assertTrue(1019 == list.get(0) || 1019 == list.get(2) || 1019 == list2.get(0) || 1019 == list2.get(2));
    }
    
  //============================================================================
    //check HippocId to check loading ok
    @Test
    void HippocIDBetelgeuse() {
        assertEquals(27989, loadedBetelgeuse().hipparcosId());
    }

    @Test
    void HippocIDRigel() {
        assertEquals(24436, loadedRigel().hipparcosId());
    }
}

