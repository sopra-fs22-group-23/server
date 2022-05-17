//package ch.uzh.ifi.sopra22.websockets;
//
//import ch.uzh.ifi.sopra22.websockets.entities.NotificationMessage;
//import ch.uzh.ifi.sopra22.websockets.entities.OutputMessage;
//import ch.uzh.ifi.sopra22.websockets.entities.enums.WebsocketType;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//@Controller
//public class NotificationController {
//
//    @MessageMapping("/notifications")
//    @SendTo("/topic/notifications")
//    public OutputMessage send(final NotificationMessage message) {
//        System.out.println(message.toString());
//        final String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
//        return new OutputMessage(message.getFrom(), message.getText(), time, WebsocketType.NOTIFICATION);
//    }
//
//}