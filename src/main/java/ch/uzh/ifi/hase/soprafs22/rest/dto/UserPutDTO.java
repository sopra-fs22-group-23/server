package ch.uzh.ifi.hase.soprafs22.rest.dto;


import java.util.Date;

public class UserPutDTO {

   //id is sent as parametr -> no need to update
    //also receive a token, so I know, that I can actually update that user

    private Date birthday;
    private boolean logged_in;
    private String username;
    private String token;

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public boolean isLogged_in() {
        return logged_in;
    }

    public void setLogged_in(boolean logged_in) {
        this.logged_in = logged_in;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
