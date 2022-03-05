package com.amitthk.sbamqpconcurrency.service;

import com.amitthk.sbamqpconcurrency.model.NotificationMessage;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqService {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routingkey}")
    private String routingkey;
    String kafkaTopic = "sb_amqp_concurrency_topic";

    public void send(NotificationMessage company) {
        amqpTemplate.convertAndSend(exchange, routingkey, company);
        System.out.println("Send msg = " + company);
    }
}