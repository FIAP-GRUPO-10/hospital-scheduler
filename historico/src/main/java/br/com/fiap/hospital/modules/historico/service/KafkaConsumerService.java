package br.com.fiap.hospital.modules.historico.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kafka Consumer for Historico module.
 * Responsible for listening and processing messages from Kafka topics.
 */
@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    /**
     * Listen for messages from agendamento-events topic
     *
     * @param message the received message
     */
    @KafkaListener(topics = "agendamento-events", groupId = "historico-group")
    public void listenAgendamentoEvents(String message) {
        logger.info("Received message from agendamento-events topic: {}", message);
        saveToHistory(message);
    }

    /**
     * Listen for messages from notificacoes topic
     *
     * @param message the received message
     */
    @KafkaListener(topics = "notificacao-topic", groupId = "historico-group")
    public void listenNotificacoes(String message) {
        logger.info("Received message from notificacao-topic: {}", message);
        saveToHistory(message);
    }

    /**
     * Save message to history database
     *
     * @param message the message to save
     */
    private void saveToHistory(String message) {
        try {
            // Add your business logic here to save to database
            logger.info("Saving to history: {}", message);
            // Example: Save to database using repository
        } catch (Exception e) {
            logger.error("Error saving to history: {}", message, e);
        }
    }
}

