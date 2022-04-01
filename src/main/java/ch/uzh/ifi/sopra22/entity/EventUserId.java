package ch.uzh.ifi.sopra22.entity;

import java.io.Serializable;


public class EventUserId implements Serializable {

    private Long userId;

    private Long eventId;

    public EventUserId(Long userId, Long eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

}
