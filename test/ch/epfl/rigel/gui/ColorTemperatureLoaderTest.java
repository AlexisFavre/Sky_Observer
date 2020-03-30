package ch.epfl.rigel.gui;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.UncheckedIOException;

import org.junit.jupiter.api.Test;

class ColorTemperatureLoaderTest {
    
    

    @Test
    void loadThrowUncheckedIOException() {
        assertThrows(UncheckedIOException.class, () -> { ColorTemperatureLoader.Instance.load();});
    }
    
    @Test
    void checkSizeOfTheListReturnedByLoad() throws Exception {
        
    }

}
