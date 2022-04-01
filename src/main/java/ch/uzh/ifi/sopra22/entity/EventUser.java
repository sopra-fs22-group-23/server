package ch.uzh.ifi.sopra22.entity;

import ch.uzh.ifi.sopra22.constants.EventStatus;
import ch.uzh.ifi.sopra22.constants.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUserStatus;
import ch.uzh.ifi.sopra22.entity.EventUserId;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;



@Entity
@Table(name = "EVENTUSER")
@IdClass(EventUserId.class)
public class EventUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long userId;

    @Id
    private Long eventId;

    @Column(nullable = false)
    private EventUserRole role;

    @Column(nullable = false)
    private EventUserStatus status;

    @Column()
    @Temporal(TemporalType.DATE)
    private Date creationDate;

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

    public EventUserRole getRole() {
        return role;
    }

    public void setRole(EventUserRole role) {
        this.role = role;
    }

    public EventUserStatus getStatus() {
        return status;
    }

    public void setStatus(EventUserStatus status) {
        this.status = status;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

}
