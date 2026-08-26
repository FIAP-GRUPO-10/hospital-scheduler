package br.com.fiap.hospital.modules.notificacoes.service;

import br.com.fiap.hospital.modules.notificacoes.model.Lembrete;
import br.com.fiap.hospital.modules.notificacoes.model.LembreteRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class NotificacaoService {

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
        return lembrete;
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
