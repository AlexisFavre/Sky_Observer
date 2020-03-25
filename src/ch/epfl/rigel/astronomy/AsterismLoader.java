package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public enum AsterismLoader implements StarCatalogue.Loader {

    INSTANCE;

    // return the corresponding star if contained in the builder else return null
    private Star starOf(int hipId, StarCatalogue.Builder builder) {
        for(Star s : builder.stars()) {
            if(s.hipparcosId() == hipId) {
                return s;
            }
        }
        return null;
    }

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
