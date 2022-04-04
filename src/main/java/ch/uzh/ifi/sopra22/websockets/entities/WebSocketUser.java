package ch.uzh.ifi.sopra22.websockets.entities;

import java.security.Principal;

public class WebSocketUser implements Principal {

    private String name;

    public WebSocketUser(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
