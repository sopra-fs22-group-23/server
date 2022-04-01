package ch.uzh.ifi.sopra22.entity;

import ch.uzh.ifi.sopra22.constants.EventStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


public class EventUserId implements Serializable {

    private Long userId;

    private Long eventId;

    public EventUserId(Long userId, Long eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

}
