package com.tpapad.rabbitzip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class QueueReceiver {

    @RabbitListener(queues = MessagingRabbitmqApplication.QUEUE_NAME, messageConverter = "compressedMessageConverter")
    public void receivedMessage(String data) {
        if (data.length() > 30) {
            log.info("Received Message From RabbitMQ: " + data.substring(0, 30) + "...");
        } else {
            log.info("Received Message From RabbitMQ: " + data);
        }
    }
}
