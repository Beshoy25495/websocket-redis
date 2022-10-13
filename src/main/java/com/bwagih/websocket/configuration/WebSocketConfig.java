package com.bwagih.websocket.configuration;

import com.bwagih.websocket.handler.UserHandshakeHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker   // enable WS for an App in the Browser to listen to the Notifications
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker (final MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); //enable simple Broker with destination prefixes which is going to listen (all of the destinations to which the websocket is going to be using for communication)
        registry.setApplicationDestinationPrefixes("/ws");   // app destination prefixes
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/our-websocket")   // add an endpoint for the websocket, using it on the frontEnd to subscribe some things and stuff (when you're connecting to the stock.js)
                .setHandshakeHandler(new UserHandshakeHandler())  // custom handler to determineUser that open the page
                .withSockJS();
    }
}
