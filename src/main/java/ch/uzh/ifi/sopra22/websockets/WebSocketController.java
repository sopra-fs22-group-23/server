package ch.uzh.ifi.sopra22.websockets;

import org.hibernate.result.Output;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Date;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketController implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/notification").setAllowedOrigins("*");//TODO could be cool to make notifications when one user is logged in
        registry.addEndpoint("/notification").setAllowedOrigins("*").withSockJS();
    }


    /**
     * When comes in message into chat, send it to all subscribers of topic messages
     * @param m Message
     * @return Outputmessage - standardized message
     */
    @MessageMapping("/notification")
    @SendTo("/topic/messages")
    public OutputMessage send(WebSocketMessage m){
        String time = new Date().toString();

        return new OutputMessage(WebsocketType.NOTIFICATION, m.getText(), time );
    }
}



