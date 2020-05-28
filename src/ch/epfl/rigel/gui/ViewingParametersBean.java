package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * A JavaFx Bean which contains an observation center (observer look)
 * (date, time, zoneId) with getter and setter for each of the 3 properties
 *
 * @author Augustin ALLARD (299918)
 */
public final class ViewingParametersBean {
    
    private final DoubleProperty fieldOfViewDeg = new SimpleDoubleProperty();
    private final ObjectProperty<HorizontalCoordinates> center = new SimpleObjectProperty<>(null);

    public ViewingParametersBean() {}

    /**
     * @param center of the projection
     * @param fieldOfViewDeg field of view of the observer in degrees
     */
    public ViewingParametersBean(HorizontalCoordinates center, double fieldOfViewDeg) {
        setCenter(center);
        setFieldOfViewDeg(fieldOfViewDeg);
    }

    //Center =================================================================
    /**
     * @return the property of the center that can be visualized
     */
    public ObjectProperty<HorizontalCoordinates> centerProperty() {
        return center;
    }

    /**
     * @return the center stocked in the property
     */
    public HorizontalCoordinates getCenter() {
        return center.getValue();
    }

    /**
     * @param center the center to set
     */
    public void setCenter(HorizontalCoordinates center) {
        this.center.setValue(center);
    }

    //Field of view =================================================================
    /**
     * @return the property of the field of view that can be visualized
     */
    public DoubleProperty fieldOfViewDegProperty() {
        return fieldOfViewDeg;
    }

    /**
     * @return the field of view of the observer in degrees stocked in the property
     */
    public double getFieldOfViewDeg() {
        return fieldOfViewDeg.getValue();
    }

    /**
     * @param fieldOfViewDeg the field of view in degrees to set
     */
    public void setFieldOfViewDeg(double fieldOfViewDeg) {
        this.fieldOfViewDeg.setValue(fieldOfViewDeg);
    }
}
