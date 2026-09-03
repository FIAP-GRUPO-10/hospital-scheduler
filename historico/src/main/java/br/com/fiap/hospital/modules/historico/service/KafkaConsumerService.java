package br.com.fiap.hospital.modules.historico.service;

import br.com.fiap.hospital.modules.historico.model.ConsultaHistorico;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
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

    public KafkaConsumerService(HistoricoService historicoService, ObjectMapper kafkaObjectMapper) {
        this.historicoService = historicoService;
        this.objectMapper = kafkaObjectMapper;
    }

    /**
     * Listen for messages from agendamento-events topic
     */
    @KafkaListener(topics = "agendamento-events", groupId = "historico-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenAgendamentoEvents(String message) {
        logger.info("Received message from agendamento-events topic: {}", message);
        saveToHistory(message);
    }

    /**
     * Listen for messages from notificacao-topic
     */
    @KafkaListener(topics = "notificacao-topic", groupId = "historico-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenNotificacoes(String message) {
        logger.info("Received message from notificacao-topic: {}", message);
        saveToHistory(message);
    }

    /**
     * Save message to history database
     */
    private void saveToHistory(String messageJson) {
        try {
            Map<String, Object> evento = objectMapper.readValue(messageJson, Map.class);
            String tipo = (String) evento.get("tipo");
            
            if (tipo != null && tipo.startsWith("CONSULTA_")) {
                ConsultaHistorico consulta = criarConsultaHistorico(evento);
                historicoService.armazenarConsulta(consulta);
                logger.info("Consulta armazenada no histórico: ID={}, Tipo={}", evento.get("consultaId"), tipo);
            }
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem Kafka: {}", messageJson, e);
        }
    }

    private ConsultaHistorico criarConsultaHistorico(Map<String, Object> evento) {
         Long consultaId = extractLong(evento.get("consultaId"));
         String pacienteId = extractString(evento.get("pacienteId"));
         String medicoId = extractString(evento.get("medicoId"));
         String enfermeiroId = extractString(evento.get("enfermeiroId"));
         String motivo = extractString(evento.get("motivo"));
         String status = extractString(evento.get("status"));
         String tipo = extractString(evento.get("tipo"));

         LocalDateTime dataHora = extractLocalDateTime(evento.get("dataHora"));
         LocalDateTime timestamp = extractLocalDateTime(evento.get("timestamp"));

         return new ConsultaHistorico(
             consultaId,
             pacienteId,
             "Paciente-" + pacienteId,
             pacienteId + "@hospital.com",
             medicoId,
             "Médico-" + medicoId,
             "Geral",
             enfermeiroId,
             dataHora,
             "Consulta " + tipo,
             motivo,
             "Presencial",
             status,
             timestamp,
             timestamp
         );
     }

    /**
     * Extrai String de um objeto que pode ser String ou List
     */
    private String extractString(Object obj) {
         if (obj == null) {
             return "";
         }
         if (obj instanceof String) {
             return (String) obj;
         }
         if (obj instanceof List) {
             List<?> list = (List<?>) obj;
             if (!list.isEmpty()) {
                 return list.get(0).toString();
             }
             return "";
         }
         return obj.toString();
     }

    /**
     * Extrai Long de um objeto
     */
    private Long extractLong(Object obj) {
         if (obj == null) {
             return 0L;
         }
         if (obj instanceof Number) {
             return ((Number) obj).longValue();
         }
         if (obj instanceof String) {
             try {
                 return Long.parseLong((String) obj);
             } catch (NumberFormatException e) {
                 return 0L;
             }
         }
         return 0L;
     }

    /**
     * Extrai LocalDateTime de um objeto que pode ser String ou List<Integer>
     */
    private LocalDateTime extractLocalDateTime(Object obj) {
         if (obj == null) {
             return LocalDateTime.now();
         }
         if (obj instanceof LocalDateTime) {
             return (LocalDateTime) obj;
         }
         if (obj instanceof String) {
             try {
                 return LocalDateTime.parse((String) obj);
             } catch (Exception e) {
                 logger.warn("Could not parse datetime string: {}", obj);
                 return LocalDateTime.now();
             }
         }
         if (obj instanceof List) {
             List<?> list = (List<?>) obj;
             if (list.size() >= 5) {
                 try {
                     int year = ((Number) list.get(0)).intValue();
                     int month = ((Number) list.get(1)).intValue();
                     int day = ((Number) list.get(2)).intValue();
                     int hour = ((Number) list.get(3)).intValue();
                     int minute = ((Number) list.get(4)).intValue();
                     return LocalDateTime.of(year, month, day, hour, minute);
                 } catch (Exception e) {
                     logger.warn("Could not parse datetime list: {}", obj);
                     return LocalDateTime.now();
                 }
             }
         }
         return LocalDateTime.now();
     }
}


