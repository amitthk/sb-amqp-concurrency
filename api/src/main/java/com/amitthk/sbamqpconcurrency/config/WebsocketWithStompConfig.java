package com.amitthk.sbamqpconcurrency.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketWithStompConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebsocketWithStompConfig.class);
    @Value("${spring.rabbitmq.username}")
    private String userName;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.relay-port}")
    private int relayPort;
    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

//    @Value("${app.rabbitmq.exchange}")
//    private String exchange;
//    @Value("${app.rabbitmq.queue}")
//    private String queue;

    @Value("${app.websocket.endpoint}")
    private String appWebsocketEndpoint;
    @Value("${app.rabbitmq.destination.prefix}")
    private String destinationPrefix;
    @Value("${stomp.broker.relay}")
    private String stompBrokerRelay;

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes(destinationPrefix);
        config.enableStompBrokerRelay(stompBrokerRelay)
                .setRelayHost(host)
                .setRelayPort(relayPort)
                .setClientLogin(userName)
                .setClientPasscode(password)
                .setVirtualHost(virtualHost)
                .setAutoStartup(true);
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint(appWebsocketEndpoint).setAllowedOriginPatterns("*").setHandshakeHandler(new DefaultHandshakeHandler())
                .withSockJS();
    }
}

