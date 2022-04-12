package ch.uzh.ifi.sopra22.rest.dto;

import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserStatus;
import ch.uzh.ifi.sopra22.constants.UserStatus;

import java.util.Date;

public class EventUserGetDTO {

    private Long id;
    private String name;
    private String username;
    private UserStatus status;
    private Date birthday;
    private String biography;
    private EventUserRole eventUserRole;
    private EventUserStatus eventUserStatus;
    private Long eventId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public EventUserRole getEventUserRole() {
        return eventUserRole;
    }

    public void setEventUserRole(EventUserRole eventUserRole) {
        this.eventUserRole = eventUserRole;
    }

    public EventUserStatus getEventUserStatus() {
        return eventUserStatus;
    }

    public void setEventUserStatus(EventUserStatus eventUserStatus) {
        this.eventUserStatus = eventUserStatus;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
