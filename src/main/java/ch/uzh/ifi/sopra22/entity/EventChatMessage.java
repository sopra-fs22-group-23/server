package ch.uzh.ifi.sopra22.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="EVENTCHATMESSAGE")
public class EventChatMessage {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String text;

    @OneToOne
    private User user;

    @OneToOne
    private Event event;

    @Column()
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }
}



