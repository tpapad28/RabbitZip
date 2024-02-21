package com.tpapad.rabbitzip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@Slf4j
public class QueueSender {
    private static final String ROUTING_KEY = "foo.bar.Q1";
    private final RabbitTemplate rabbitTemplate;

    private final RabbitTemplate compressedRabbitTemplate;

    public QueueSender(RabbitTemplate rabbitTemplate,
                       @Qualifier("compressed") RabbitTemplate compressedRabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.compressedRabbitTemplate = compressedRabbitTemplate;
    }

    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask() {
        log.info("Sending messages...");

        // Send using default template, i.e. w/o compression
        rabbitTemplate.convertAndSend(MessagingRabbitmqApplication.TOPIC_EXCHANGE_NAME, ROUTING_KEY, "Hello from " + "RabbitMQ!");

        final String jsonPretty = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("64KB.json"))).lines().collect(Collectors.joining());
        // Send using compression-enabled template
        compressedRabbitTemplate.convertAndSend(MessagingRabbitmqApplication.TOPIC_EXCHANGE_NAME, ROUTING_KEY, jsonPretty);

        final String jsonMin = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("64KB-min.json"))).lines().collect(Collectors.joining());
        // Send using compression-enabled template
        compressedRabbitTemplate.convertAndSend(MessagingRabbitmqApplication.TOPIC_EXCHANGE_NAME, ROUTING_KEY, jsonMin);
    }
}
