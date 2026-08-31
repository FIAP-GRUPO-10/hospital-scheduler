package br.com.fiap.hospital.modules.historico.service;

import br.com.fiap.hospital.modules.historico.model.ConsultaHistorico;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Kafka Consumer for Historico module.
 * Responsible for listening and processing messages from Kafka topics.
 */
@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    
    private final HistoricoService historicoService;
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(HistoricoService historicoService, ObjectMapper objectMapper) {
        this.historicoService = historicoService;
        this.objectMapper = objectMapper;
    }

    /**
     * Listen for messages from agendamento-events topic
     *
     * @param message the received message
     */
    @KafkaListener(topics = "agendamento-events", groupId = "historico-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenAgendamentoEvents(String message) {
        logger.info("Received message from agendamento-events topic: {}", message);
        saveToHistory(message);
    }

    /**
     * Listen for messages from notificacoes topic
     *
     * @param message the received message
     */
    @KafkaListener(topics = "notificacao-topic", groupId = "historico-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenNotificacoes(String message) {
        logger.info("Received message from notificacao-topic: {}", message);
        saveToHistory(message);
    }

    /**
     * Save message to history database
     *
     * @param messageJson the message to save
     */
    private void saveToHistory(String messageJson) {
        try {
            Map<String, Object> evento = objectMapper.readValue(messageJson, Map.class);
            String tipo = (String) evento.get("tipo");
            
            if (tipo != null && tipo.startsWith("CONSULTA_")) {
                // Criar um registro de histórico a partir do evento
                ConsultaHistorico consulta = criarConsultaHistorico(evento);
                historicoService.armazenarConsulta(consulta);
                logger.info("Consulta armazenada no histórico: ID={}, Tipo={}", evento.get("consultaId"), tipo);
            }
        } catch (Exception e) {
            logger.error("Error saving to history: {}", messageJson, e);
        }
    }

    private ConsultaHistorico criarConsultaHistorico(Map<String, Object> evento) {
        Long consultaId = ((Number) evento.get("consultaId")).longValue();
        String pacienteId = (String) evento.get("pacienteId");
        String medicoId = (String) evento.get("medicoId");
        String enfermeiroId = (String) evento.get("enfermeiroId");
        
        // Parse dataHora
        LocalDateTime dataHora = LocalDateTime.now();
        Object dataHoraObj = evento.get("dataHora");
        if (dataHoraObj instanceof String) {
            try {
                dataHora = LocalDateTime.parse((String) dataHoraObj);
            } catch (Exception e) {
                logger.warn("Could not parse dataHora from event");
            }
        }
        
        String motivo = (String) evento.get("motivo");
        String status = (String) evento.get("status");
        String tipo = (String) evento.get("tipo");
        
        return new ConsultaHistorico(
            consultaId,
            pacienteId,
            "Paciente-" + pacienteId,  // Placeholder para nome
            pacienteId + "@hospital.com", // Placeholder para email
            medicoId,
            "Médico-" + medicoId,  // Placeholder para nome
            "Geral",  // Placeholder para especialidade
            enfermeiroId,
            dataHora,
            "Consulta " + tipo,
            motivo,
            "Presencial",
            status,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}


