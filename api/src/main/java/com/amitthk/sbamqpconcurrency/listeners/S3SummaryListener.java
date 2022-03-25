package com.amitthk.sbamqpconcurrency.listeners;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amitthk.sbamqpconcurrency.service.S3SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class S3SummaryListener {

    private static final Logger logger = LoggerFactory.getLogger(S3SummaryListener.class);

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routingkey}")
    private String routingkey;

    @Value("${app.rabbitmq.queue}")
    private String queue;

    @RabbitListener(bindings = @QueueBinding(value = @Queue("sbamqpapp.queue"), exchange = @Exchange(value = "sbamqpapp.exchange"), key = "sbamqpapp.routingkey"))
    public void receiveMessage(final Message<S3ObjectSummary> message) {
        S3ObjectSummary parseS3ObjectSummary = parseS3ObjectSummary(message.getPayload());
        String output = String.format("Bucket path %s size %s!",
                parseS3ObjectSummary.getKey(),parseS3ObjectSummary.getSize());
        // Print to standard output
        logger.info(output);
    }

    private S3ObjectSummary parseS3ObjectSummary(S3ObjectSummary payload) {
        S3ObjectSummary summary= new S3ObjectSummary();
        BeanUtils.copyProperties(payload,summary);
        return summary;
    }

}
