package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableObjectValue;

/**
 * A javaFx bean which contains the position of the observer as its longitude and its latitude in degrees,
 * or a combination of both of them as {@code GeographicCoordinates}
 * and getters for all and setters for latitude and longitude (the {@code GeographicCoordinates}
 * will be automatically change
 * 
 * @author Alexis FAVRE (310552)
 */
public final class ObserverLocationBean {
    
    private final DoubleProperty lonDeg;
    private final DoubleProperty latDeg;
    private final ObjectBinding<GeographicCoordinates> coordinates;
    
    public ObserverLocationBean() {
        this.latDeg = new SimpleDoubleProperty();
        this.lonDeg = new SimpleDoubleProperty();
        this.coordinates = Bindings.createObjectBinding(
                () -> GeographicCoordinates.ofDeg(lonDeg.get(), latDeg.get()) , latDeg, lonDeg);
    }
    
    public ObserverLocationBean(GeographicCoordinates coordinates) {
        this();
        setLonDeg(coordinates.lonDeg());
        setLatDeg(coordinates.latDeg());
    }
    //lonDeg ================================================================
    /**
     * @return the property lonDeg that can be visualized
     */
    public DoubleProperty lonDegProperty() {
        return lonDeg;
    }
    
    /**
     * @return the lonDeg stocked in the property
     */
    public Double getLonDeg() {
        return lonDeg.getValue();
    }

    /**
     * @param lonDeg the lonDeg to set
     */
    public void setLonDeg(Double lonDeg) {
        this.lonDeg.setValue(lonDeg);
    }
    
    //latDeg ================================================================
    /**
     * @return the property latDeg that can be visualized
     */
    public DoubleProperty latDegProperty() {
        return latDeg;
    }
    
    /**
     * @return the latDeg stocked in the property
     */
    public Double getLatDeg() {
        return latDeg.getValue();
    }

    /**
     * @param latDeg the latDeg to set
     */
    public void setLatDeg(Double latDeg) {
        this.latDeg.setValue(latDeg);
    }
    
    //coordinates ===========================================================
    /**
     * @return the property coordinates that can be visualized
     */
    public ObservableObjectValue<GeographicCoordinates> coordinatesProperty() {
        return coordinates;
    }
    
    /**
     * @return the coordinates stocked in the property
     */
    public GeographicCoordinates getCoordinates() {
        return coordinates.getValue();
    }
    
    /**
     * @param latDeg the latDeg to set
     */
    public void setCoordinates(GeographicCoordinates coordinates) {
        setLonDeg(coordinates.lonDeg());
        setLatDeg(coordinates.latDeg());
    }
}
