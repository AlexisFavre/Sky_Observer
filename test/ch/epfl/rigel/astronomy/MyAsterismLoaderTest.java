package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class MyAsterismLoaderTest {

    private MyStarCatalogueTest CATALOG_TEST = new MyStarCatalogueTest();

    @Test
    void load() throws IOException {

        Queue<Asterism> a = new ArrayDeque<>();
        Star betelgeuse = null;
        for (Asterism ast : CATALOG_TEST.CATALOG.asterisms()) {
            for (Star s : ast.stars()) {
                if (s.name().equalsIgnoreCase("Rigel")) {
                    a.add(ast);
                }
            }
        }
        int astCount = 0;
        for (Asterism ast : a) {
            ++astCount;
            for (Star s : ast.stars()) {
                if (s.name().equalsIgnoreCase("Betelgeuse")) {
                    betelgeuse = s;
                }
            }
        }
        assertNotNull(betelgeuse);
        assertEquals(2, astCount);
    }
}