package ch.uzh.ifi.sopra22.websockets;

import ch.uzh.ifi.sopra22.websockets.entities.NotificationMessage;
import ch.uzh.ifi.sopra22.websockets.entities.OutputMessage;
import ch.uzh.ifi.sopra22.websockets.entities.TaskMessage;
import ch.uzh.ifi.sopra22.websockets.entities.enums.WebsocketType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class SessionSchedulerController {

    //send to /app/sessionScheduler with {taskID, String(user), ACTION}


    /**
     * This is just redirection of an message to all other
     */
    @MessageMapping("/sessionScheduler/{eventHash}")
    @SendTo("/topic/sessionScheduler/{eventHash}")
    public TaskMessage send(@DestinationVariable("eventHash") String eventHash, final TaskMessage message){
        return new TaskMessage(message.getTaskID(), message.getUserID(), message.getColumnID(), message.getAction());
//        .concat("With hash:").concat(eventHash)
    }


    @MessageMapping("/sessionScheduler/{eventHash}/chat")
    @SendTo("/topic/sessionScheduler/{eventHash}/chat")
    public String send(@DestinationVariable("eventHash") String eventHash, final String message){
        return message;
    }
    //it is only resending the message back to the client, so one can send json whit whatever structure one wants
}
