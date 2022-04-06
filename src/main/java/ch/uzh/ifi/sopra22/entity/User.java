package ch.uzh.ifi.sopra22.entity;

import ch.uzh.ifi.sopra22.constants.UserStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "USER")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column()
    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column()
    private String password;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private UserStatus status;

    @Column()
    @Temporal(TemporalType.DATE)
    private Date birthday;

    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "user")
    private List<EventUser> eventUsers = new ArrayList<>(); //private Set<EventUser> eventUsers = new HashSet<>();

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public List<EventUser> getEventUsers() { return eventUsers; }

    public void setEventUsers(List<EventUser> eventUsers) { this.eventUsers = eventUsers; }

    public void addEventUsers(EventUser eventUser) { this.eventUsers.add(eventUser); }

}
