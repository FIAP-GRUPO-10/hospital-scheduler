package br.com.fiap.hospital.modules.notificacoes.service;

import br.com.fiap.hospital.modules.notificacoes.model.Lembrete;
import br.com.fiap.hospital.modules.notificacoes.model.LembreteRequest;
import org.springframework.http.HttpStatus;
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

    public NotificacaoService() {
        registrarLembreteSeeded(new Lembrete(1L, "PAC-1001", "MED-2001",
                LocalDateTime.now().plusDays(1).withHour(9),
                "Lembrete: sua consulta está agendada para amanhã às 09:00.", true));
        registrarLembreteSeeded(new Lembrete(2L, "PAC-1002", "MED-2002",
                LocalDateTime.now().plusDays(2).withHour(14),
                "Lembrete: sua consulta do dia 2 está confirmada.", false));
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

    /**
     * Cria uma notificação automaticamente baseada em eventos de agendamento
     * Este método é chamado pelo KafkaConsumerService quando eventos chegam
     */
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
        
        // Aqui você pode adicionar lógica para enviar notificações via email, SMS, push, etc.
        enviarNotificacao(lembrete);
    }

    /**
     * Simula o envio de notificação para o paciente
     * Em produção, isso poderia enviar um email, SMS, push notification, etc.
     */
    private void enviarNotificacao(Lembrete lembrete) {
        try {
            logger.info("=== ENVIANDO NOTIFICAÇÃO ===");
            logger.info("Para: {}", lembrete.pacienteId());
            logger.info("Médico: {}", lembrete.medicoId());
            logger.info("Data da Consulta: {}", lembrete.dataConsulta());
            logger.info("Mensagem: {}", lembrete.mensagem());
            logger.info("===========================");
            
            // TODO: Integrar com serviço de email (SendGrid, AWS SES, etc.)
            // TODO: Integrar com serviço de SMS (Twilio, AWS SNS, etc.)
            // TODO: Integrar com push notifications (Firebase Cloud Messaging, etc.)
            
        } catch (Exception e) {
            logger.error("Erro ao enviar notificação para paciente: {}", lembrete.pacienteId(), e);
        }
    }

    private void validarPerfil(Authentication authentication) {
        if (authentication == null || (!authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MEDICO") ||
                        authority.getAuthority().equals("ROLE_ENFERMEIRO")))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Apenas médicos e enfermeiros podem disparar lembretes.");
        }
    }

    private void validarRequest(LembreteRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O lembrete é obrigatório.");
        }
        if (request.pacienteId() == null || request.pacienteId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paciente obrigatório.");
        }
        if (request.medicoId() == null || request.medicoId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Médico obrigatório.");
        }
        if (request.dataConsulta() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data da consulta obrigatória.");
        }
        if (request.mensagem() == null || request.mensagem().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mensagem do lembrete obrigatória.");
        }
    }

    private boolean ehPaciente(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_PACIENTE"));
    }

    private void registrarLembreteSeeded(Lembrete lembrete) {
        lembretes.put(lembrete.id(), lembrete);
        sequence.updateAndGet(current -> Math.max(current, lembrete.id() + 1));
    }
}
