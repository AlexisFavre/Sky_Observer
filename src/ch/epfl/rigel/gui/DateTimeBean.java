package ch.epfl.rigel.gui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Collector.Characteristics;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * a JavaFx Bean which contains an Observation Instant (date, time, zoneId)
 * with getter and setter for each of the 3 properties
 * @author Alexis FAVRE (310552)
 */
public final class DateTimeBean {

    private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>(null); 
    private ObjectProperty<LocalTime> time = new SimpleObjectProperty<>(null);
    private ObjectProperty<ZoneId>    zone = new SimpleObjectProperty<>(null);
    
    //date =================================================================
    /**
     * @return the date 
     */
    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }
    
    /**
     * @return the date
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
    
    //time =================================================================
    /**
     * @return the time
     */
    public ObjectProperty<LocalTime> timeProperty() {
        return time;
    }
    
    /**
     * @return the time
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
    
    //zone =================================================================
    /**
     * @return the zone
     */
    public ObjectProperty<ZoneId> zoneProperty() {
        return zone;
    }
    
    /**
     * @return the zone
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
     * @return a {@code ZonedDateTime} with the currents characteristics of {@code this}
     */
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.of(getDate(), getTime(), getZone());
    }
    
    /**
     * set the characteristics of {@code zdt} to {@code this}
     * @param zdt 
     */
    public void setZonedDateTime(ZonedDateTime zdt) {
        setDate(zdt.toLocalDate());
        setTime(zdt.toLocalTime());
        setZone(zdt.getZone());
    }

}
