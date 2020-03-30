package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static ch.epfl.rigel.Preconditions.checkArgument;

/**
 * Represents a set of {@code CelestialObjects} projected on a plan
 * by a {@code StereographicProjection}
 * This is like a picture of the sky at a given time and place of observation
 *
 * @author Augustin Allard (299918)
 */
public class ObservedSky {

    private Sun sun;
    private CartesianCoordinates sunPoint;

    private Moon moon;
    private CartesianCoordinates moonPoint;

    private List<Planet> planets = new ArrayList<>();
    private double[] planetPointsRefs;

    private StarCatalogue catalog;
    private double[] starPointsRefs;

    /**
     *
     * @param obsTime the time of the observation
     * @param obsPlace the coordinates of the observer
     * @param projection the projection used
     * @param catalog containing the observed stars and asterisms
     */
    public ObservedSky(ZonedDateTime obsTime, GeographicCoordinates obsPlace,
                       StereographicProjection projection, StarCatalogue catalog) {

        EclipticToEquatorialConversion eclToEqu = new EclipticToEquatorialConversion(obsTime);
        double moment = Epoch.J2010.daysUntil(obsTime);
        sun = SunModel.SUN.at(moment, eclToEqu);
        moon = MoonModel.MOON.at(moment, eclToEqu);
        for(PlanetModel p: PlanetModel.ALL) {
            planets.add(p.at(moment, eclToEqu));
        }

        EquatorialToHorizontalConversion equToHor = new EquatorialToHorizontalConversion(obsTime, obsPlace);
        sunPoint = projection.apply(equToHor.apply(sun.equatorialPos()));
        moonPoint = projection.apply(equToHor.apply(moon.equatorialPos()));
        List<Double> pprefs = new ArrayList<>();
        List<Double> sprefs = new ArrayList<>();
        for(Planet p: planets) {
            CartesianCoordinates pPoint = projection.apply(equToHor.apply(p.equatorialPos()));
            pprefs.add(pPoint.x());
            pprefs.add(pPoint.y());
        }
        for(Star s: catalog.stars()) {
            CartesianCoordinates sPoint = projection.apply(equToHor.apply(s.equatorialPos()));
            sprefs.add(sPoint.x());
            sprefs.add(sPoint.y());
        }
        planetPointsRefs = toArray(pprefs);
        starPointsRefs = toArray(sprefs);
    }

    private double[] toArray(List<Double> list) {
        double[] array = new double[list.size()];
        for(int i = 0; i < list.size(); ++i) {
            array[i] = list.get(i);
        }
        return array;
    }

    private List<CelestialObject> allSkyObjects() {
        List<CelestialObject> skyObjects = new ArrayList<>();
        skyObjects.addAll(planets);
        skyObjects.addAll(stars());
        skyObjects.add(sun);
        skyObjects.add(sun);
        return
    }

    /**
     * Gives the closest object to the given plan coordinates
     * if there exists one that is closer to the given maximal distance
     */
    public CelestialObject objectClosestTo(CartesianCoordinates point, double maximalDistance) {
        f
    }

    /**
     *
     * @return the point of the {@code StereographicProjection} plan
     * corresponding to the sun
     */
    public CartesianCoordinates sunPoint() {
        return sunPoint;
    }

    /**
     *
     * @return the point of the {@code StereographicProjection} plan
     * corresponding to the moon
     */
    public CartesianCoordinates moonPoint() {
        return moonPoint;
    }

    /**
     *
     * @return the planets points (abscissa and ordinates) of the {@code StereographicProjection} plan
     * corresponding to the 7 extraterrestrial planets
     */
    public double[] planetPointsRefs() {
        return planetPointsRefs;
    }

    /**
     *
     * @return the planets points (abscissa and ordinates) of the {@code StereographicProjection} plan
     * corresponding to the 7 extraterrestrial planets
     */
    public double[] starPointsRefs() {
        return starPointsRefs;
    }

    /**
     * Gives the star indexes forming the given asterism
     *
     * @param asterism of which we want the indexes
     * @return {@code List} of the star indexes
     * @throws IllegalArgumentException if the given asterism does not belongs to {@code this}
     */
    public List<Integer> asterismIndices(Asterism asterism) throws IllegalArgumentException {
        return catalog.asterismIndices(asterism);
    }

    /**
     *
     * @return the asterisms of the sky
     */
    public Set<Asterism> asterisms() {
        return catalog.asterisms();
    }

    /**
     *
     * @return the sun in its state corresponding to the observation moment
     */
    public Sun sun() {
        return sun;
    }

    /**
     *
     * @return the moon in its state corresponding to the observation moment
     */
    public Moon moon() {
        return moon;
    }

    /**
     *
     * @return the 7 planets in their state corresponding to the observation moment
     */
    public List<Planet> planets() {
        return planets;
    }

    /**
     *
     * @return the stars of the sky {@code this}
     */
    public List<Star> stars() {
        return catalog.stars();
    }
}
