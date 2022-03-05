package com.amitthk.sbamqpconcurrency.listeners;

import com.amitthk.sbamqpconcurrency.model.NotificationMessage;
import com.amitthk.sbamqpconcurrency.model.NotificationType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebsocketChatEventListener {
    private static final Logger log= LoggerFactory.getLogger(WebsocketChatEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event){
        log.info("===========");
        log.info("new web socket connection");
        log.info("===========");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor= StompHeaderAccessor.wrap(event.getMessage());
        String username=(String)headerAccessor.getSessionAttributes().get("username");
        if(username != null) {
            NotificationMessage chatMessage = new NotificationMessage(username,"Closed the session!",NotificationType.SIMPLE_MESSAGE);
            messagingTemplate.convertAndSend("/queue/sbamqpapp.queue", chatMessage);
        }
    }
}
