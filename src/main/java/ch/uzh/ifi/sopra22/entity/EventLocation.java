package ch.uzh.ifi.sopra22.entity;

import java.io.Serializable;


public class EventLocation implements Serializable {

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
