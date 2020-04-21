package ch.epfl.rigel.gui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Collector.Characteristics;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * A JavaFx Bean which contains an Observation moment (date, time, zoneId)
 * with getter and setter for each of the 3 properties
 *
 * @author Alexis FAVRE (310552)
 */
public final class DateTimeBean {

    private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>(null); 
    private ObjectProperty<LocalTime> time = new SimpleObjectProperty<>(null);
    private ObjectProperty<ZoneId>    zone = new SimpleObjectProperty<>(null);

    public DateTimeBean() {}

    public DateTimeBean(ZonedDateTime initial) {
        setZonedDateTime(initial);
    }
    
    //Date =================================================================
    /**
     * @return the property of the date that can be visualized
     */
    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }
    
    /**
     * @return the date stocked in the property
     */
    public LocalDate getDate() {
        return date.getValue();
    }
    
    /**
     * @param date the date to set
     */
    public void setDate(LocalDate date) {
        this.date.setValue(date);
    }
    
    //Time =================================================================
    /**
     * @return the property of the time that can be visualized
     */
    public ObjectProperty<LocalTime> timeProperty() {
        return time;
    }
    
    /**
     * @return the time stocked in the property
     */
    public LocalTime getTime() {
        return time.getValue();
    }
    
    /**
     * @param time the time to set
     */
    public void setTime(LocalTime time) {
        this.time.setValue(time);
    }
    
    //Zone =================================================================
    /**
     * @return the property of the zone that can be visualized
     */
    public ObjectProperty<ZoneId> zoneProperty() {
        return zone;
    }
    
    /**
     * @return the zone stocked in the property
     */
    public ZoneId getZone() {
        return zone.getValue();
    }
    
    /**
     * @param zone the zone to set
     */
    public void setZone(ZoneId zone) {
        this.zone.setValue(zone);
    }
    
    //ZoneDateTime ========================================================
    /**
     *
     * @return a {@code ZonedDateTime} with the currents characteristics of {@code this}
     */
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.of(getDate(), getTime(), getZone());
    }
    
    /**
     * Set the characteristics of {@code zdt} to {@code this}
     *
     * @param zdt the {@code ZoneDateTime} to be added to the fields of {@code this}
     */
    public void setZonedDateTime(ZonedDateTime zdt) {
        setDate(zdt.toLocalDate());
        setTime(zdt.toLocalTime());
        setZone(zdt.getZone());
    }
}
