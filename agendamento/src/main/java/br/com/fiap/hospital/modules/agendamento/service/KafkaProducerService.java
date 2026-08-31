package br.com.fiap.hospital.modules.agendamento.service;

import br.com.fiap.hospital.modules.agendamento.model.Consulta;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Producer for Agendamento module.
 * Responsible for sending messages to Kafka topics.
 */
@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    
    private static final String TOPIC_AGENDAMENTO_EVENTS = "agendamento-events";
    private static final String TOPIC_NOTIFICACOES = "notificacao-topic";

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

    /**
     * Send consultation created event
     */
    public void enviarConsultaCriada(Consulta consulta) {
        Map<String, Object> evento = criarEventoConsulta("CONSULTA_CRIADA", consulta);
        sendMessageWithKey(TOPIC_AGENDAMENTO_EVENTS, "consulta_criada_" + consulta.id(), evento);
        
        // Also send to notifications topic
        sendMessageWithKey(TOPIC_NOTIFICACOES, consulta.pacienteId(), evento);
    }

    /**
     * Send consultation updated event
     */
    public void enviarConsultaAtualizada(Consulta consulta) {
        Map<String, Object> evento = criarEventoConsulta("CONSULTA_ATUALIZADA", consulta);
        sendMessageWithKey(TOPIC_AGENDAMENTO_EVENTS, "consulta_atualizada_" + consulta.id(), evento);
        
        // Also send to notifications topic
        sendMessageWithKey(TOPIC_NOTIFICACOES, consulta.pacienteId(), evento);
    }

    /**
     * Send consultation deleted event
     */
    public void enviarConsultaDeletada(Long consultaId, Consulta consulta) {
        Map<String, Object> evento = new HashMap<>();
        evento.put("tipo", "CONSULTA_DELETADA");
        evento.put("consultaId", consultaId);
        evento.put("pacienteId", consulta.pacienteId());
        evento.put("medicoId", consulta.medicoId());
        evento.put("timestamp", LocalDateTime.now());
        
        sendMessageWithKey(TOPIC_AGENDAMENTO_EVENTS, "consulta_deletada_" + consultaId, evento);
        sendMessageWithKey(TOPIC_NOTIFICACOES, consulta.pacienteId(), evento);
    }

    /**
     * Send consultation cancelled event
     */
    public void enviarConsultaCancelada(Consulta consulta) {
        Map<String, Object> evento = criarEventoConsulta("CONSULTA_CANCELADA", consulta);
        sendMessageWithKey(TOPIC_AGENDAMENTO_EVENTS, "consulta_cancelada_" + consulta.id(), evento);
        
        // Also send to notifications topic
        sendMessageWithKey(TOPIC_NOTIFICACOES, consulta.pacienteId(), evento);
    }

    /**
     * Send consultation confirmed event
     */
    public void enviarConsultaConfirmada(Consulta consulta) {
        Map<String, Object> evento = criarEventoConsulta("CONSULTA_CONFIRMADA", consulta);
        sendMessageWithKey(TOPIC_AGENDAMENTO_EVENTS, "consulta_confirmada_" + consulta.id(), evento);
        
        // Also send to notifications topic
        sendMessageWithKey(TOPIC_NOTIFICACOES, consulta.pacienteId(), evento);
    }

    private Map<String, Object> criarEventoConsulta(String tipo, Consulta consulta) {
        Map<String, Object> evento = new HashMap<>();
        evento.put("tipo", tipo);
        evento.put("consultaId", consulta.id());
        evento.put("pacienteId", consulta.pacienteId());
        evento.put("medicoId", consulta.medicoId());
        evento.put("enfermeiroId", consulta.enfermeiroId());
        evento.put("dataHora", consulta.dataHora());
        evento.put("duracaoMinutos", consulta.duracaoMinutos());
        evento.put("motivo", consulta.motivo());
        evento.put("status", consulta.status());
        evento.put("timestamp", LocalDateTime.now());
        return evento;
    }
}


