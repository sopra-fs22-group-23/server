package ch.uzh.ifi.sopra22.websockets;

import ch.uzh.ifi.sopra22.websockets.entities.UserInterceptor;
import org.hibernate.result.Output;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Date;

/**
 * Workig endpoints now:
 * /websockets - first handshake with {username: TOKEN} where token is user token from localstorage
 *
 * SEND TO
 * /app/sessionScheduler/{eventHash} - for event changes
 * /app/notifications  - notification that I send is send to all users
 * /app/message  - message that is send only to one user
 *
 * SUBSCRIBE
 * /topic/sessionScheduler/{eventHash} - all changes from each event
 * /topic/notifications - all notification that I send from event
 * /user/topic/messages - messages for one user
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");// /topic/smth are topic the clients can subscribe
        config.setApplicationDestinationPrefixes("/app"); //all stuff sent to /app/something will be catched
        config.setUserDestinationPrefix("/users"); //adds specific prefix, if I want to send smth only to certain user
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websockets").setAllowedOrigins("*");//provides endpoint where the handshake is done

//        registry.addEndpoint("/websockets").setAllowedOrigins("*").withSockJS();
    }



    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new UserInterceptor());//to hande the login of user to websocket and be able to sent it both ways also to certain user
    }

}

/** when I want to send a message from server to clients
 * @Autowired
 *     private SimpMessagingTemplate simpMessagingTemplate;
 *
 *     public loginUser(User u ){
 *
 *         OutputMessage m = new OutputMessage('Server', 'User {username} in now active!', new Date().toString(), WebsocketType.NOTIFICATION);
 *         simpMessagingTemplate.convertAndSend("/topic/notifications", m);
 *     }
 */



