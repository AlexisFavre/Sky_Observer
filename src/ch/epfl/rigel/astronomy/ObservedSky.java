package ch.epfl.rigel.astronomy;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialToHorizontalConversion;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;

/**
 * Represents a set of {@code CelestialObjects} projected on a plan
 * by a {@code StereographicProjection}
 * This is like a picture of the sky at a given time and place of observation
 *
 * @author Augustin Allard (299918)
 */
public final class ObservedSky {
    
    private final StarCatalogue catalog;
    private final StereographicProjection projection;

    private final Sun sun;
    private final Moon moon;
    private final List<Planet> planets;
    private final CartesianCoordinates sunPoint;
    private final CartesianCoordinates moonPoint;
    private final double[] planetPointsRefs;
    private final double[] starPointsRefs;

    private final Map<CelestialObject, CartesianCoordinates> skyObjects;

    /**
     * @param obsTime the time of the observation
     * @param obsPlace the coordinates of the observer
     * @param projection that will be used
     * @param catalog containing the observed stars and asterisms
     */
    public ObservedSky(ZonedDateTime obsTime, GeographicCoordinates obsPlace,
                StereographicProjection projection, StarCatalogue catalog) {

        this.catalog    = catalog;
        this.skyObjects = new HashMap<>();
        this.planets    = new ArrayList<>();
        this.projection = projection;
        double moment   = Epoch.J2010.daysUntil(obsTime);
        
        // create coordinates converters
        EclipticToEquatorialConversion eclToEqu   = new EclipticToEquatorialConversion(obsTime);
        EquatorialToHorizontalConversion equToHor = new EquatorialToHorizontalConversion(obsTime, obsPlace);
        
        List<PlanetModel> extraterrestrialModels = new ArrayList<>();
        extraterrestrialModels.addAll(PlanetModel.ALL);
        extraterrestrialModels.remove(PlanetModel.EARTH);

        sun      = SunModel.SUN.at(moment, eclToEqu);
        sunPoint = projection.apply(equToHor.apply(sun.equatorialPos()));
        skyObjects.put(sun, sunPoint);
        
        moon      = MoonModel.MOON.at(moment, eclToEqu);
        moonPoint = projection.apply(equToHor.apply(moon.equatorialPos()));
        skyObjects.put(moon, moonPoint);
        
        // to construct planetPointsRefs and starPointsRefs, each celestialObject take 2 cases, 
        // the first for the x coordinate and the follower for y coordinate
        planetPointsRefs = new double[extraterrestrialModels.size()*2];
        starPointsRefs   = new double[catalog.stars().size()*2];
        CartesianCoordinates point; //use to fulfill the lists and tabs with coordinates of the celestialObjects
        int indexTab     = 0;
        
        // construct planetPointsRefs and planets
        for(PlanetModel planetModel: extraterrestrialModels) {
            
            Planet planet = planetModel.at(moment, eclToEqu);
            point         = projection.apply(equToHor.apply(planet.equatorialPos()));
            
            skyObjects.put(planet, point);
            planets.add(planet);
            planetPointsRefs[indexTab++] = point.x();
            planetPointsRefs[indexTab++] = point.y();
        }
        indexTab = 0;
        
        //construct starPointsRefs
        for(Star star: catalog.stars()) {
            
            point = projection.apply(equToHor.apply(star.equatorialPos()));
            
            skyObjects.put(star, point);
            starPointsRefs[indexTab++] = point.x();
            starPointsRefs[indexTab++] = point.y();
        }
    }

    /**
     * Gives the closest sky object from the place corresponding to the given plan point
     * if there exists one that is closer to the given maximal distance
     *
     * @param point the point from which we want the closest object
     * @param maximalDistance distance on the map corresponding to the radius of search
     * @return an {@code Optional} containing the closest object if there exist one in the maximal distance disc 
     * and {@code Optional.empty} if no enough closed objects have been found
     */
    public Optional<CelestialObject> objectClosestTo(CartesianCoordinates point, double maximalDistance) {
        CelestialObject closestObject = null;
        double actualBestDist         = maximalDistance;
        
        for(CelestialObject p : skyObjects.keySet()) {
            CartesianCoordinates c = skyObjects.get(p);
            
            if(Math.abs(c.x() - point.x()) < actualBestDist   //make preliminary selection
            && Math.abs(c.y() - point.y()) < actualBestDist) {
                
                double distanceFromCurrentCelestialObject = point.distance(c);
                
                if(distanceFromCurrentCelestialObject < actualBestDist) {
                    
                    actualBestDist = distanceFromCurrentCelestialObject;
                    closestObject  = p;
                }
            }
        }
        return Optional.ofNullable(closestObject);
    }
    
    //getters====================================================================================

    /**
     * @return the projection used for observation
     */
    public StereographicProjection projection() {
        return projection; 
    }

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
        return Collections.unmodifiableList(planets);
    }

    /**
     * @return the stars of the sky {@code this}
     */
    public List<Star> stars() {
        return catalog.stars();
    }
}
