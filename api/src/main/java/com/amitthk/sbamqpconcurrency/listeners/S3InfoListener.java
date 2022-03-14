package com.amitthk.sbamqpconcurrency.listeners;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class S3InfoListener {
        private static final String POLISH_PHONE_PREFIX = "+48";

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
            System.out.println(output);
        }

    private S3ObjectSummary parseS3ObjectSummary(S3ObjectSummary payload) {
        S3ObjectSummary summary= new S3ObjectSummary();
        BeanUtils.copyProperties(payload,summary);
        return summary;
    }

}
