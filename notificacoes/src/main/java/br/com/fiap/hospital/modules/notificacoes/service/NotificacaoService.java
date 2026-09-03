package br.com.fiap.hospital.modules.notificacoes.service;

import br.com.fiap.hospital.modules.notificacoes.constants.NotificacaoConstants;
import br.com.fiap.hospital.modules.notificacoes.model.Lembrete;
import br.com.fiap.hospital.modules.notificacoes.model.LembreteRequest;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class NotificacaoService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);

    private final Map<Long, Lembrete> lembretes = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1L);

    // 👇 Consumidor Kafka para o tópico de notificações
    @KafkaListener(topics = "notificacao-topic", groupId = "notificacoes-group")
    public void consumirEventoNotificacao(Map<String, Object> evento) {
        try {
            String tipo = (String) evento.get("tipo");
            String pacienteId = (String) evento.get("pacienteId");
            String medicoId = (String) evento.get("medicoId");
            LocalDateTime dataHora = (LocalDateTime) evento.get("dataHora");
            String motivo = (String) evento.get("motivo");

            String mensagem = "Consulta " + tipo + " - Motivo: " + motivo;

            criarNotificacaoAutomatica(pacienteId, medicoId, dataHora, mensagem);
            logger.info("Evento Kafka consumido e lembrete criado para paciente {}", pacienteId);
        } catch (Exception e) {
            logger.error("Erro ao processar evento de notificação: {}", evento, e);
        }
    }

    public List<Lembrete> listarLembretes(Authentication authentication, String pacienteId) {
        if (ehPaciente(authentication)) {
            String pacienteAutenticado = authentication.getName();
            if (pacienteId != null && !pacienteId.isBlank() && !pacienteAutenticado.equalsIgnoreCase(pacienteId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Pacientes só podem visualizar os próprios lembretes.");
            }
            return lembretes.values().stream()
                    .filter(lembrete -> pacienteAutenticado.equalsIgnoreCase(lembrete.pacienteId()))
                    .toList();
        }

        if (pacienteId != null && !pacienteId.isBlank()) {
            return lembretes.values().stream()
                    .filter(lembrete -> pacienteId.equalsIgnoreCase(lembrete.pacienteId()))
                    .toList();
        }

        return new ArrayList<>(lembretes.values());
    }

    public Lembrete criarLembrete(LembreteRequest request, Authentication authentication) {
        validarPerfil(authentication);
        validarRequest(request);

        Lembrete lembrete = new Lembrete(
                sequence.getAndIncrement(),
                request.pacienteId(),
                request.medicoId(),
                request.dataConsulta(),
                request.mensagem(),
                true
        );

        lembretes.put(lembrete.id(), lembrete);
        logger.info("Lembrete criado manualmente: ID={}, Paciente={}", lembrete.id(), request.pacienteId());
        return lembrete;
    }

    public void criarNotificacaoAutomatica(String pacienteId, String medicoId, LocalDateTime dataConsulta, String mensagem) {
        Lembrete lembrete = new Lembrete(
                sequence.getAndIncrement(),
                pacienteId,
                medicoId,
                dataConsulta,
                mensagem,
                true
        );

        lembretes.put(lembrete.id(), lembrete);
        logger.info("Notificação automática criada: ID={}, Paciente={}, Mensagem={}", lembrete.id(), pacienteId, mensagem);

        enviarNotificacao(lembrete);
    }

    private void enviarNotificacao(Lembrete lembrete) {
        try {
            logger.info("=== ENVIANDO NOTIFICAÇÃO ===");
            logger.info("Para: {}", lembrete.pacienteId());
            logger.info("Médico: {}", lembrete.medicoId());
            logger.info("Data da Consulta: {}", lembrete.dataConsulta());
            logger.info("Mensagem: {}", lembrete.mensagem());
            logger.info("===========================");
        } catch (Exception e) {
            logger.error("Erro ao enviar notificação para paciente: {}", lembrete.pacienteId(), e);
        }
    }

    private void validarPerfil(Authentication authentication) {
        if (authentication == null || (!authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(NotificacaoConstants.ROLE_MEDICO) ||
                        authority.getAuthority().equals(NotificacaoConstants.ROLE_ENFERMEIRO)))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    NotificacaoConstants.ERRO_PERFIL_INVALIDO);
        }
    }

    private void validarRequest(LembreteRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O lembrete é obrigatório.");
        }
        if (request.pacienteId() == null || request.pacienteId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NotificacaoConstants.ERRO_PACIENTE_OBRIGATORIO);
        }
        if (request.medicoId() == null || request.medicoId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NotificacaoConstants.ERRO_MEDICO_OBRIGATORIO);
        }
        if (request.dataConsulta() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NotificacaoConstants.ERRO_DATA_OBRIGATORIA);
        }
        if (request.mensagem() == null || request.mensagem().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, NotificacaoConstants.ERRO_MSG_OBRIGATORIA);
        }
    }

    private boolean ehPaciente(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(NotificacaoConstants.ROLE_PACIENTE));
    }

    private void registrarLembreteSeeded(Lembrete lembrete) {
        lembretes.put(lembrete.id(), lembrete);
        sequence.updateAndGet(current -> Math.max(current, lembrete.id() + 1));
    }
}
