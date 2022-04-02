package ch.uzh.ifi.sopra22.entity;

import java.io.Serializable;
import java.util.Objects;


public class EventUserId implements Serializable {

    private Long userId;

    private Long eventId;

    public EventUserId(Long userId, Long eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    public boolean equals(Object eventUserId) {
        if (this == eventUserId) return true;
        if (!(eventUserId instanceof EventUserId)) return false;
        return Objects.equals(getEventId(), ((EventUserId) eventUserId).getEventId()) &&
                Objects.equals(getUserId(), ((EventUserId) eventUserId).getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.eventId.toString() + "-" + this.userId.toString());
    }
}
