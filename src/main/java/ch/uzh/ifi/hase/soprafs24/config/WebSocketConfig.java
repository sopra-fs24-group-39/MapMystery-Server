package ch.uzh.ifi.hase.soprafs24.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  
    private static final String ORIGIN_LOCALHOST = "http://localhost:5500";
    private static final String ORIGIN_8080 = "http://localhost:8080";
    private static final String ORIGIN_PROD = "https://sopra-fs24-group-39-client.oa.r.appspot.com";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        WebSocketMessageBrokerConfigurer.super.configureWebSocketTransport(registration);
    }

    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Configure the endpoint 6 enable CORS
        // registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:5500","http://localhost:3000","http://sopra-fs24-group-39-client.oa.r.appspot.com:5500","http://sopra-fs24-group-39-client.oa.r.appspot.com:3000","http://sopra-fs24-group-39-client.oa.r.appspot.com:443").withSockJS();
        registry.addEndpoint("/ws").setAllowedOrigins(ORIGIN_LOCALHOST, ORIGIN_PROD,ORIGIN_8080).withSockJS();


    }
}
