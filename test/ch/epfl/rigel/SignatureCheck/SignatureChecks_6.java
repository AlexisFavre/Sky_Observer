package ch.epfl.rigel.SignatureCheck;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.CelestialObjectModel;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.Moon;
import ch.epfl.rigel.astronomy.MoonModel;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.StarCatalogue;

final class SignatureChecks_6 {
    void checkMoonModel() {
        Enum<MoonModel> m1 = MoonModel.MOON;
        CelestialObjectModel<Moon> m2 = MoonModel.MOON;
    }

    void checkStarCatalogue() throws IOException {
        List<Star> sl = null;
        Star s = null;
        Asterism a = null;
        List<Asterism> al = null;
        Set<Asterism> as = null;
        List<Integer> il;
        StarCatalogue c = new StarCatalogue(sl, al);
        sl = c.stars();
        as = c.asterisms();
        il = c.asterismIndices(a);

        InputStream i = null;
        StarCatalogue.Loader l = null;
        StarCatalogue.Builder b = new StarCatalogue.Builder();
        b = b.addStar(s);
        sl = b.stars();
        b = b.addAsterism(a);
        al = b.asterisms();
        b = b.loadFrom(i, l);
        c = b.build();

        l.load(i, b);
    }

    void checkHygDatabaseLoader() {
        StarCatalogue.Loader l = HygDatabaseLoader.INSTANCE;
    }

    void checkAsterismLoader() {
        StarCatalogue.Loader l = AsterismLoader.INSTANCE;
    }
}
