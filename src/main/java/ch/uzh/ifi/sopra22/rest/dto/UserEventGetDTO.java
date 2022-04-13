package ch.uzh.ifi.sopra22.rest.dto;

import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserStatus;

import java.util.Date;

public class UserEventGetDTO {
    private Long id;
    private String title;
    private EventType type;
    private String description;
    private Date eventDate;
    private EventStatus status;
    private String locationName;
    private float longitude;
    private float latitude;
    private EventUserStatus eventUserStatus;
    private EventUserRole eventUserRole;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public EventUserStatus getEventUserStatus() {
        return eventUserStatus;
    }

    public void setEventUserStatus(EventUserStatus eventUserStatus) {
        this.eventUserStatus = eventUserStatus;
    }

    public EventUserRole getEventUserRole() {
        return eventUserRole;
    }

    public void setEventUserRole(EventUserRole eventUserRole) {
        this.eventUserRole = eventUserRole;
    }
}
