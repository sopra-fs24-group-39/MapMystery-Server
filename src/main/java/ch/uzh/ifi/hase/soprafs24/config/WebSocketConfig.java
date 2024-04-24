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
        // Configure the endpoint and enable CORS
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:5500").withSockJS();
    }
}
