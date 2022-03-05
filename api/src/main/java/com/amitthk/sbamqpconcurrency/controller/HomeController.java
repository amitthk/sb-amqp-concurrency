package com.amitthk.sbamqpconcurrency.controller;

import com.amitthk.sbamqpconcurrency.model.NotificationMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class HomeController {

    @MessageMapping("/user-all")
    @SendTo("/queue/sb_amqp_concurrency_topic")
    public NotificationMessage sendToAll(@Payload NotificationMessage message) {
        return message;
    }

}