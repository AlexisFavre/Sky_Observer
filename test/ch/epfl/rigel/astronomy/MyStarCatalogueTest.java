package ch.epfl.rigel.astronomy;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

class MyStarCatalogueTest {
    
    private StarCatalogue init() {
        
    try(InputStream in = getClass().getResourceAsStream("/asterisms.txt")){
        StarCatalogue.Builder b = new StarCatalogue.Builder();
        StarCatalogue clg = b.loadFrom(in, AsterismLoader.INSTANCE).build();
        return clg;
        }
        catch (Exception e) {
            return null;
        }
    }

    @Test
    void getteurs() {
        try{
            boolean b = false;
            for (Asterism asterism : init().asterisms()) {
                b = init().stars().containsAll(asterism.stars());
                if(b == false) { throw new Error();}
            }
            assertFalse(false);
        }
        catch(Exception e){
            assertFalse(true);
        }
    }
        
        @Test
        void indexBetelgeuse() throws IOException {
            MyHygDatabaseLoaderTest hygL = new MyHygDatabaseLoaderTest();
           assertEquals(hygL.betelgeuse().hipparcosId(), 27989); 
        }
        
        @Test
        void indexRigel() throws IOException {
            MyHygDatabaseLoaderTest hygL = new MyHygDatabaseLoaderTest();
           assertEquals(hygL.rigel().hipparcosId(), 24436); 
        }
        
    

}

