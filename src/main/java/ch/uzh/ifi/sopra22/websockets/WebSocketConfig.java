package ch.uzh.ifi.sopra22.websockets;

import org.hibernate.result.Output;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Date;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");// /topic/smth are topic the clients can subscribe
        config.setApplicationDestinationPrefixes("/app");
        //all stuff sent to /app/something will be catched
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websockets").setAllowedOrigins("*");//provides endpoint where the handshake is done

//        registry.addEndpoint("/websockets").setAllowedOrigins("*").withSockJS();
    }

}



