package ch.uzh.ifi.sopra22.websockets;

public class OutputMessage {
    private WebsocketType type;
    private String text;
    private String time;

    //text is JSON array with additional parameters


    public OutputMessage(WebsocketType type, String text, String time) {
        this.type = type;
        this.text = text;
        this.time = time;
    }
}
