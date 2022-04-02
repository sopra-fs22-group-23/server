package ch.uzh.ifi.sopra22.websockets.entities;

public class NotificationMessage {

    private String from;
    private String text;

    public String getText() {
        return text;
    }

    public String getFrom() {
        return from;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from='" + from + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
