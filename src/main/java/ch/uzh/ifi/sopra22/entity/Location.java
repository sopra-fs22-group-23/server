package ch.uzh.ifi.sopra22.entity;

import ch.uzh.ifi.sopra22.constants.EventStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


public class Location implements Serializable {

    private Long eventId;

    private String location;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
