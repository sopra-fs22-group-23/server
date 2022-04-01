package ch.uzh.ifi.sopra22.websockets;

public class WebSocketMessage {
    private String text;
    //should be a JSON array - no idea what will be inside


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
