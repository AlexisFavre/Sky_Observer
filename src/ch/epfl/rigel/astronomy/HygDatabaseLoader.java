package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.io.*;

/**
 * to load the stars and their characteristics from the hygdata_v3
 * @author Augustin ALLARD (299918)
 */
public enum HygDatabaseLoader implements StarCatalogue.Loader {

    INSTANCE;

    @Override
    /**
     * to load the stars
     * @throws IOException if the loading does not run correctly
     */
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String currentLine;
            int i = 0;
            while((currentLine = reader.readLine()) != null) {

                String[] lineInfo = currentLine.split(",", -1);

                if(i != 0) {
                    int hip = (!(lineInfo[Id.HIP.ordinal()]).equals("")) ? Integer.parseInt(lineInfo[Id.HIP.ordinal()]) : 0;
                    String name = (!(lineInfo[Id.PROPER.ordinal()]).equals("")) ?
                            lineInfo[Id.PROPER.ordinal()] :
                            ( (!(lineInfo[Id.BAYER.ordinal()]).equals("")) ? lineInfo[Id.BAYER.ordinal()] : "?")
                                    + " " + lineInfo[Id.CON.ordinal()];
                    float magnitude = (!(lineInfo[Id.MAG.ordinal()]).equals("")) ?
                            (float) Double.parseDouble(lineInfo[Id.MAG.ordinal()]) : 0;
                    float colorIndex = (!(lineInfo[Id.CI.ordinal()]).equals("")) ?
                            (float) Double.parseDouble(lineInfo[Id.CI.ordinal()]) : 0;

                    builder.addStar(new Star(hip, name, EquatorialCoordinates.of(Double.parseDouble(lineInfo[Id.RARAD.ordinal()]),
                                    Double.parseDouble(lineInfo[Id.DECRAD.ordinal()])), magnitude, colorIndex));
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
