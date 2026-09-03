package br.com.fiap.hospital.modules.notificacoes.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Kafka Consumer for Notificacoes module.
 * Responsible for listening and processing messages from Kafka topics.
 */
@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final NotificacaoService notificacaoService;
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(NotificacaoService notificacaoService, ObjectMapper objectMapper) {
        this.notificacaoService = notificacaoService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "agendamento-events", groupId = "notificacoes-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenAgendamentoEvents(String message) {
        logger.info("Received message from agendamento-events topic: {}", message);
        processNotificationFromEvent(message);
    }

    @KafkaListener(topics = "notificacao-topic", groupId = "notificacoes-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenNotificacoes(String message) {
        logger.info("Received message from notificacao-topic: {}", message);
        processNotificationFromEvent(message);
    }

    private void processNotificationFromEvent(String messageJson) {
        try {
            Map<String, Object> evento = objectMapper.readValue(messageJson, Map.class);
            String tipo = (String) evento.get("tipo");

            switch (tipo) {
                case "CONSULTA_CRIADA":
                    handleConsultaCriada(evento);
                    break;
                case "CONSULTA_ATUALIZADA":
                    handleConsultaAtualizada(evento);
                    break;
                case "CONSULTA_CONFIRMADA":
                    handleConsultaConfirmada(evento);
                    break;
                case "CONSULTA_CANCELADA":
                    handleConsultaCancelada(evento);
                    break;
                case "CONSULTA_DELETADA":
                    handleConsultaDeletada(evento);
                    break;
                default:
                    logger.warn("Unknown event type: {}", tipo);
            }
        } catch (Exception e) {
            logger.error("Error processing notification event: {}", messageJson, e);
        }
    }

    private void handleConsultaCriada(Map<String, Object> evento) {
         try {
             String pacienteId = extractString(evento.get("pacienteId"));
             String medicoId = extractString(evento.get("medicoId"));
             String motivo = extractString(evento.get("motivo"));

             LocalDateTime dataHora = extractDateTime(evento.get("dataHora"));

             String mensagem = String.format(
                     "Sua consulta foi agendada para %s com o médico ID: %s. Motivo: %s",
                     dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                     medicoId,
                     motivo
             );

             notificacaoService.criarNotificacaoAutomatica(pacienteId, medicoId, dataHora, mensagem);
             logger.info("Notificação de consulta criada enviada para: {}", pacienteId);
         } catch (Exception e) {
             logger.error("Error handling CONSULTA_CRIADA event", e);
         }
     }

    private void handleConsultaAtualizada(Map<String, Object> evento) {
         try {
             String pacienteId = extractString(evento.get("pacienteId"));
             String medicoId = extractString(evento.get("medicoId"));

             LocalDateTime dataHora = extractDateTime(evento.get("dataHora"));

             String mensagem = String.format(
                     "Sua consulta foi atualizada para %s com o médico ID: %s",
                     dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                     medicoId
             );

             notificacaoService.criarNotificacaoAutomatica(pacienteId, medicoId, dataHora, mensagem);
             logger.info("Notificação de consulta atualizada enviada para: {}", pacienteId);
         } catch (Exception e) {
             logger.error("Error handling CONSULTA_ATUALIZADA event", e);
         }
     }

    private void handleConsultaConfirmada(Map<String, Object> evento) {
         try {
             String pacienteId = extractString(evento.get("pacienteId"));
             String medicoId = extractString(evento.get("medicoId"));

             LocalDateTime dataHora = extractDateTime(evento.get("dataHora"));

             String mensagem = String.format(
                     "Sua consulta foi confirmada para %s com o médico ID: %s. Por favor, chegue 15 minutos antes.",
                     dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                     medicoId
             );

             notificacaoService.criarNotificacaoAutomatica(pacienteId, medicoId, dataHora, mensagem);
             logger.info("Notificação de consulta confirmada enviada para: {}", pacienteId);
         } catch (Exception e) {
             logger.error("Error handling CONSULTA_CONFIRMADA event", e);
         }
     }

    private void handleConsultaCancelada(Map<String, Object> evento) {
         try {
             String pacienteId = extractString(evento.get("pacienteId"));
             String medicoId = extractString(evento.get("medicoId"));

             String mensagem = String.format(
                     "Sua consulta com o médico ID: %s foi cancelada. Entre em contato para reagendar.",
                     medicoId
             );

             notificacaoService.criarNotificacaoAutomatica(pacienteId, medicoId, LocalDateTime.now(), mensagem);
             logger.info("Notificação de consulta cancelada enviada para: {}", pacienteId);
         } catch (Exception e) {
             logger.error("Error handling CONSULTA_CANCELADA event", e);
         }
     }

    private void handleConsultaDeletada(Map<String, Object> evento) {
         try {
             String pacienteId = extractString(evento.get("pacienteId"));
             String medicoId = extractString(evento.get("medicoId"));

             String mensagem = String.format(
                     "Sua consulta com o médico ID: %s foi deletada do sistema.",
                     medicoId
             );

             notificacaoService.criarNotificacaoAutomatica(pacienteId, medicoId, LocalDateTime.now(), mensagem);
             logger.info("Notificação de consulta deletada enviada para: {}", pacienteId);
         } catch (Exception e) {
             logger.error("Error handling CONSULTA_DELETADA event", e);
         }
     }

    /**
     * Extrai LocalDateTime de um objeto que pode ser String ou List<Integer>
     */
    private LocalDateTime extractDateTime(Object dataHoraObj) {
         if (dataHoraObj == null) {
             return LocalDateTime.now();
         }
         if (dataHoraObj instanceof String) {
             return parseDateTime((String) dataHoraObj);
         }
         if (dataHoraObj instanceof List) {
             @SuppressWarnings("unchecked")
             List<Integer> list = (List<Integer>) dataHoraObj;
             if (list.size() >= 5) {
                 return LocalDateTime.of(
                         list.get(0), // ano
                         list.get(1), // mês
                         list.get(2), // dia
                         list.get(3), // hora
                         list.get(4)  // minuto
                 );
             }
         }
         return LocalDateTime.now();
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

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            } catch (Exception ex) {
                logger.warn("Could not parse datetime: {}", dateTimeStr);
                return LocalDateTime.now();
            }
        }
    }
}
