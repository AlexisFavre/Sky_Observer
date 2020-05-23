package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Used to load {@code Asterism} objects from a hyg_data stream to a {@code StarCatalogue.Builder}
 *
 * @author Augustin ALLARD (299918)
 */
public enum AsterismLoader implements StarCatalogue.Loader {

    INSTANCE;

    private final HashMap<Integer, Star> hipparcosIdOfStar = new HashMap<>();

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
        
        for (Star star : builder.stars()) {
            hipparcosIdOfStar.put(star.hipparcosId(), star);
        }

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] lineHips = currentLine.split(",", -1);
                List<Star> lineStarsContainedInBuilder = new LinkedList<>();
                boolean builderContainsAllAsterismStars = true;

                // containing verification
                for (String h : lineHips) {
                    Star star = hipparcosIdOfStar.get(Integer.parseInt(h));
                        if(star != null) {
                        lineStarsContainedInBuilder.add(star);
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
