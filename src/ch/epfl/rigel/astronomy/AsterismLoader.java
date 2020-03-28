package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to load {@code Asterism} objects from a hyg_data stream to a {@code StarCatalogue.Builder}
 *
 * @author Augustin ALLARD (299918)
 *
 */
public enum AsterismLoader implements StarCatalogue.Loader {

    INSTANCE;

    private Star starOf(int hipId, StarCatalogue.Builder builder) {
        for(Star s : builder.stars()) {
            if(s.hipparcosId() == hipId) {
                return s;
            }
        }
        return null;
    }

    /**
     * Load {@code Asterism} objects created using the the stream content
     * and add them to the given {@code StarCatalogue.Builder}
     *
     * @param inputStream the stream containing data to create the objects
     * @param builder receiving the objects
     * @throws IOException if I/O error occurs
     */
    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] lineHips = currentLine.split(",", -1);
                List<Star> lineStarsContainedInBuilder = new ArrayList<>();
                boolean builderContainsAllAsterismStars = true;

                // containing verification
                for (String h : lineHips) {
                    Star s;
                    if ((s = starOf(Integer.parseInt(h), builder)) != null) {
                        lineStarsContainedInBuilder.add(s);
                    } else {
                        builderContainsAllAsterismStars = false;
                    }
                }
                if (builderContainsAllAsterismStars) {
                    builder.addAsterism(new Asterism(lineStarsContainedInBuilder));
                }
            }
        }
    }
}
