//package ch.uzh.ifi.sopra22.websockets;
//
//import ch.uzh.ifi.sopra22.websockets.entities.MessageToUser;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//import java.util.Objects;
//
//@Controller
//public class MessageToUserController {
//    @Autowired
//    private SimpMessagingTemplate simpMessagingTemplate;
//
//    @MessageMapping("/messageToUser")
//    public void send(SimpMessageHeaderAccessor sha, @Payload MessageToUser m) {//Payload is the body of the request, now I am sending only the hash of the user
//        String message = "Hello from " + Objects.requireNonNull(sha.getUser()).getName();//will actually return a token
//
//        //TODO add user hash when I want to send direct message
//        simpMessagingTemplate.convertAndSendToUser("abcdef", "/topic/messages", m);
//    }
//}
