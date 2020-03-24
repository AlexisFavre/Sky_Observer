package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.io.*;

public enum HygDatabaseLoader implements StarCatalogue.Loader {

    INSTANCE;

    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String currentLine;
            int i = 0;
            while((currentLine = reader.readLine()) != null) {

                String[] starInfo = currentLine.split(",", -1);

                if(i != 0) {
                    int hip = (!(starInfo[Id.HIP.ordinal()]).equals("")) ? Integer.parseInt(starInfo[Id.HIP.ordinal()]) : 0;
                    String name = (!(starInfo[Id.PROPER.ordinal()]).equals("")) ?
                            starInfo[Id.PROPER.ordinal()] :
                            starInfo[Id.BAYER.ordinal()] + " " + starInfo[Id.CON.ordinal()];
                    float magnitude = (!(starInfo[Id.MAG.ordinal()]).equals("")) ?
                            (float) Double.parseDouble(starInfo[Id.MAG.ordinal()]) : 0;
                    float colorIndex = (!(starInfo[Id.CI.ordinal()]).equals("")) ?
                            (float) Double.parseDouble(starInfo[Id.CI.ordinal()]) : 0;

                    builder.addStar(new Star(hip, name, EquatorialCoordinates.of(Double.parseDouble(starInfo[Id.RARAD.ordinal()]),
                                    Double.parseDouble(starInfo[Id.DECRAD.ordinal()])), magnitude, colorIndex));
                }
                ++i;
            }
        }
    }


    //================================================================================================

    private enum Id {
        ID, HIP, HD, HR, GL, BF, PROPER, RA, DEC, DIST, PMRA, PMDEC,
        RV, MAG, ABSMAG, SPECT, CI, X, Y, Z, VX, VY, VZ,
        RARAD, DECRAD, PMRARAD, PMDECRAD, BAYER, FLAM, CON,
        COMP, COMP_PRIMARY, BASE, LUM, VAR, VAR_MIN, VAR_MAX;
    }
}
