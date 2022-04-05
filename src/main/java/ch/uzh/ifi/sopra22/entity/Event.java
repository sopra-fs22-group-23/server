package ch.uzh.ifi.sopra22.entity;


import ch.uzh.ifi.sopra22.constants.Event.EventStatus;
import ch.uzh.ifi.sopra22.constants.Event.EventType;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "EVENT")
public class Event implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private EventType type;

    @Column()
    private String picture; // filename for /files/{filename} endpoint

    @Column()
    private String description;

    @Column(nullable = false)
    private EventStatus status;

    @Column()
    @Temporal(TemporalType.DATE)
    private Date eventDate;

    @Column()
    private EventLocation eventLocation;

    @OneToMany(mappedBy = "event")
    private List<EventUser> eventUsers = new ArrayList<>(); //Set<EventUser> eventUsers = new HashSet<>();

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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public EventLocation getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(EventLocation eventLocation) {
        this.eventLocation = eventLocation;
    }

    public List<EventUser> getEventUsers() { return eventUsers; }

    public void setEventUsers(List<EventUser> eventUsers) { this.eventUsers = eventUsers; }

    public void addEventUsers(EventUser eventUser) { this.eventUsers.add(eventUser); }
}
