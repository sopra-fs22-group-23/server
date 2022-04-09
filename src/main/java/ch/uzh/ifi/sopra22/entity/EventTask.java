package ch.uzh.ifi.sopra22.entity;

import javax.persistence.*;

@Entity
@Table(name="EVENTTASK")
public class EventTask {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String description;

    @OneToOne
    private User user;

    @OneToOne
    private Event event;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
