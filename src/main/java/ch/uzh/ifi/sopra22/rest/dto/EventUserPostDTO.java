package ch.uzh.ifi.sopra22.rest.dto;

import ch.uzh.ifi.sopra22.constants.EventUser.EventUserRole;
import ch.uzh.ifi.sopra22.constants.EventUser.EventUserStatus;

import java.util.Date;

public class EventUserPostDTO {
    private Long id;
    private String name;
    private String username;
    private String password;
    private Date birthday;
    private String biography;
    private String email;
    private EventUserRole eventUserRole;
    private EventUserStatus eventUserStatus;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
