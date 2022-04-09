package ch.uzh.ifi.sopra22.entity;

import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserStatus;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



@Entity
@Table(name = "EVENTUSER")
public class EventUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long eventUserId;

    /**
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long eventId;*/


    @ManyToOne
    private User user;
    
    @ManyToOne
    private Event event;


    @Column(nullable = false)
    private EventUserRole role;

    @Column(nullable = false)
    private EventUserStatus status;

    @Column()
    @Temporal(TemporalType.DATE)
    private Date creationDate;

    public Long getEventUserId() {
        return eventUserId;
    }

    public void setEventUserId(Long eventUserId) {
        this.eventUserId = eventUserId;
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
    
    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public Event getEvent() { return event; }

    public void setEvent(Event event) { this.event = event; }

}
