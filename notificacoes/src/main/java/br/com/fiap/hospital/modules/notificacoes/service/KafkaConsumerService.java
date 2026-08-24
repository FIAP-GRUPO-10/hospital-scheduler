package br.com.fiap.hospital.modules.notificacoes.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kafka Consumer for Notificacoes module.
 * Responsible for listening and processing messages from Kafka topics.
 */
@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    /**
     * Listen for messages from agendamento topic
     *
     * @param message the received message
     */
    @KafkaListener(topics = "agendamento-events", groupId = "notificacoes-group")
    public void listenAgendamentoEvents(String message) {
        logger.info("Received message from agendamento-events topic: {}", message);
        processNotification(message);
    }

    /**
     * Listen for messages from notificacoes topic
     *
     * @param message the received message
     */
    @KafkaListener(topics = "notificacao-topic", groupId = "notificacoes-group")
    public void listenNotificacoes(String message) {
        logger.info("Received message from notificacao-topic: {}", message);
        processNotification(message);
    }

    /**
     * Process the notification message
     *
     * @param message the notification message
     */
    private void processNotification(String message) {
        try {
            // Add your business logic here
            logger.info("Processing notification: {}", message);
            // Example: Send email, SMS, or push notification
        } catch (Exception e) {
            logger.error("Error processing notification: {}", message, e);
        }
    }
}

