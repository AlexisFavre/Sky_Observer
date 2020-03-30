package ch.epfl.rigel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import javafx.scene.paint.Color;

public class JavaFXInstallationTest {

    @Test
    void JavaFXIsCorrectlyInstalled() {
        Color c = Color.RED;
        assertEquals(1.0, c.getRed(), 1.0e-10);
    }
}
