package ch.epfl.rigel.astronomy;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.epfl.rigel.coordinates.*;

/**
 * Represents a set of {@code CelestialObjects} projected on a plan
 * by a {@code StereographicProjection}
 * This is like a picture of the sky at a given time and place of observation
 *
 * @author Augustin Allard (299918)
 */
public final class ObservedSky { //TODO should be final ?

    private final StarCatalogue catalog;
    private final Map<CartesianCoordinates, CelestialObject> skyObjects;
    private final StereographicProjection projection;

    private Sun sun;
    private Moon moon;
    private List<Planet> planets = new ArrayList<>();
    private CartesianCoordinates sunPoint;
    private CartesianCoordinates moonPoint;
    private double[] planetPointsRefs;
    private double[] starPointsRefs;

    /**
     * @param obsTime the time of the observation
     * @param obsPlace the coordinates of the observer
     * @param observerLook the point where the observer is looking that will be the center of the projection
     * @param catalog containing the observed stars and asterisms
     */
    public ObservedSky(ZonedDateTime obsTime, GeographicCoordinates obsPlace,
                       HorizontalCoordinates observerLook, StarCatalogue catalog) {

        skyObjects    = new HashMap<>();
        this.catalog  = catalog;
        projection = new StereographicProjection(observerLook);
        double moment = Epoch.J2010.daysUntil(obsTime);
        
        // create coordinates converters
        EclipticToEquatorialConversion eclToEqu   = new EclipticToEquatorialConversion(obsTime);
        EquatorialToHorizontalConversion equToHor = new EquatorialToHorizontalConversion(obsTime, obsPlace);
        
        List<PlanetModel> extraterrestrialModels = PlanetModel.ALL;
        extraterrestrialModels.remove(PlanetModel.EARTH); //TODO check that it's effective

        sun  = SunModel.SUN.at(moment, eclToEqu);
        skyObjects.put(sunPoint = projection.apply(equToHor.apply(sun.equatorialPos())), sun);
        moon = MoonModel.MOON.at(moment, eclToEqu);
        skyObjects.put(moonPoint = projection.apply(equToHor.apply(moon.equatorialPos())), moon);
        
        // to construct planetPointsRefs and starPointsRefs
        List<Double> pprefs = new LinkedList<>(); // points coordinates of the planets
        List<Double> sprefs = new LinkedList<>(); // points coordinates of the stars
        CartesianCoordinates pPoint;
        CartesianCoordinates sPoint;
        
        // construct planetPointsRefs
        for(PlanetModel planetModel: extraterrestrialModels) {
            Planet planet = planetModel.at(moment, eclToEqu);
            skyObjects.put(pPoint = projection.apply(equToHor.apply(planet.equatorialPos())), planet);
            planets.add(planet);
            pprefs.add(pPoint.x());
            pprefs.add(pPoint.y());
        }
        
        //construct starPointsRefs
        for(Star star: catalog.stars()) {
            skyObjects.put(sPoint = projection.apply(equToHor.apply(star.equatorialPos())), star);
            sprefs.add(sPoint.x());
            sprefs.add(sPoint.y());
        }
        planetPointsRefs = toArray(pprefs);
        starPointsRefs = toArray(sprefs);
    }

    /**
     * Gives the closest sky object from the place corresponding to the given plan point
     * if there exists one that is closer to the given maximal distance
     *
     * @param point the point from which we want the closest object
     * @param maximalDistance distance on the map corresponding to the radius of search
     * @return the closest object if there exist one in the maximal distance circle 
     * and {@code null} if no objects were found
     */
    public CelestialObject objectClosestTo(CartesianCoordinates point, double maximalDistance) {
        CartesianCoordinates closestObjectPoint = null;
        double sideOfSquare = Math.max(Math.abs(point.x()), Math.abs(point.y())) + 2*maximalDistance;
        for(CartesianCoordinates p: skyObjects.keySet()) {
            if(Math.abs(p.x()) < sideOfSquare         //make preliminary selection
                    && Math.abs(p.y()) < sideOfSquare 
                    && point.distance(p) < maximalDistance
                    && point.distance(p) < point.distance(closestObjectPoint)
                    && point.distance(p) > 0) //TODO order modified
                closestObjectPoint = p;
        }
        if(closestObjectPoint == null)
            return null;
        //System.out.println(sunPoint().distance(moonPoint()));
        //System.out.println(point.distance(closestObjectPoint));
        return skyObjects.get(closestObjectPoint);
    }

    /**
     *
     * @return the projection used for observation
     */
    public StereographicProjection projection() { return projection; }

    /**
     * @return the point of the {@code StereographicProjection} plan
     * corresponding to the sun
     */
    public CartesianCoordinates sunPoint() {
        return sunPoint;
    }

    /**
     * @return the point of the {@code StereographicProjection} plan
     * corresponding to the moon
     */
    public CartesianCoordinates moonPoint() {
        return moonPoint;
    }

    /**
     * @return the planets points (abscissa and ordinates) of the {@code StereographicProjection} plan
     * corresponding to the 7 extraterrestrial planets
     */
    public double[] planetPointsRefs() {
        return planetPointsRefs.clone();
    }

    /**
     * @return the planets points (abscissa and just after it ordinates) of the {@code StereographicProjection} plan
     * corresponding to the 7 extraterrestrial planets
     */
    public double[] starPointsRefs() {
        return starPointsRefs.clone();
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
     * @return the asterisms of the sky
     */
    public Set<Asterism> asterisms() {
        return catalog.asterisms();
    }

    /**
     * @return the sun in its state corresponding to the observation moment
     */
    public Sun sun() {
        return sun;
    }

    /**
     * @return the moon in its state corresponding to the observation moment
     */
    public Moon moon() {
        return moon;
    }

    /**
     * @return the 7 extraterrestrials planets of the SolarSystem in their state corresponding to the observation moment
     */
    public List<Planet> planets() {
        return Collections.unmodifiableList(planets);  //TODO need immutable ?
    }

    /**
     * @return the stars of the sky {@code this}
     */
    public List<Star> stars() {
        return catalog.stars();
    }


    // implementation stuff
    private double[] toArray(List<Double> list) {
        double[] array = new double[list.size()];
        int j = 0; // TODO check linked list <-> array list
        for (Iterator<Double> iterator = list.iterator(); iterator.hasNext();) {
            array[j] = (Double) iterator.next();
            ++j;
        }
        return array;
    }
}
