package br.com.fiap.hospital.modules.agendamento.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kafka Producer for Agendamento module.
 * Responsible for sending messages to Kafka topics.
 */
@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Send a message to a Kafka topic
     *
     * @param topic   the topic name
     * @param message the message to send
     */
    public void sendMessage(String topic, Object message) {
        logger.info("Sending message to topic: {} with payload: {}", topic, message);
        kafkaTemplate.send(topic, message);
    }

    /**
     * Send a message with a key to a Kafka topic
     *
     * @param topic the topic name
     * @param key   the message key
     * @param value the message value
     */
    public void sendMessageWithKey(String topic, String key, Object value) {
        logger.info("Sending message with key: {} to topic: {} with payload: {}", key, topic, value);
        kafkaTemplate.send(topic, key, value);
    }
}

