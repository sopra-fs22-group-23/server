package ch.uzh.ifi.sopra22.entity;

import ch.uzh.ifi.sopra22.constants.EventStatus;
import ch.uzh.ifi.sopra22.constants.EventType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "EVENT")
public class Event implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue
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
    private Location location;

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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


}
