package br.com.fiap.hospital.modules.agendamento.service;

import br.com.fiap.hospital.modules.agendamento.model.Consulta;
import br.com.fiap.hospital.modules.agendamento.model.ConsultaRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AgendamentoService {

    private final Map<Long, Consulta> consultas = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1L);
    private final KafkaProducerService kafkaProducerService;

    public AgendamentoService(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
        registrarConsultaSeeded(new Consulta(1L, "PAC-1001", "MED-2001", "ENF-3001",
                LocalDateTime.now().plusDays(1).withHour(9).withMinute(0), 60,
                "Consulta cardiológica", "AGENDADA"));
        registrarConsultaSeeded(new Consulta(2L, "PAC-1002", "MED-2002", "ENF-3002",
                LocalDateTime.now().plusDays(2).withHour(14).withMinute(0), 45,
                "Acompanhamento clínico", "AGENDADA"));
    }

    public Consulta buscarPorId(Long id, Authentication authentication) {
        Consulta consulta = consultas.get(id);
        if (consulta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada.");
        }
        validarAcessoPaciente(authentication, consulta.pacienteId());
        return consulta;
    }

    public List<Consulta> listarConsultas(Authentication authentication, String pacienteId) {
        if (ehPaciente(authentication)) {
            String pacienteAutenticado = authentication.getName();
            if (pacienteId != null && !pacienteId.isBlank() && !pacienteAutenticado.equalsIgnoreCase(pacienteId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Pacientes só podem visualizar as próprias consultas.");
            }
            return consultas.values().stream()
                    .filter(consulta -> pacienteAutenticado.equalsIgnoreCase(consulta.pacienteId()))
                    .toList();
        }

        if (pacienteId != null && !pacienteId.isBlank()) {
            return consultas.values().stream()
                    .filter(consulta -> pacienteId.equalsIgnoreCase(consulta.pacienteId()))
                    .toList();
        }

        return new ArrayList<>(consultas.values());
    }

    public Consulta criarConsulta(ConsultaRequest request, Authentication authentication) {
        validarPerfilResponsavel(authentication);
        validarRequisicao(request);

        Consulta novaConsulta = new Consulta(
                sequence.getAndIncrement(),
                request.pacienteId(),
                request.medicoId(),
                request.enfermeiroId(),
                request.dataHora(),
                request.duracaoMinutos(),
                request.motivo(),
                "AGENDADA"
        );

        if (consultas.values().stream().anyMatch(consulta -> consulta.conflitaCom(novaConsulta))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Já existe conflito de agenda para o médico, enfermeiro ou paciente informado.");
        }

        consultas.put(novaConsulta.id(), novaConsulta);

        try {
            // Publicar evento de consulta criada
            kafkaProducerService.enviarConsultaCriada(novaConsulta);
        } catch (Exception e) {
            // Logar o erro e não deixar estourar 500
            System.err.println("Erro ao enviar evento para Kafka: " + e.getMessage());
            // Opcional: lançar uma exceção mais amigável
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Consulta criada, mas falha ao notificar sistema externo.");
        }

        return novaConsulta;
    }


    public Consulta atualizarConsulta(Long id, ConsultaRequest request, Authentication authentication) {
        validarPerfilResponsavel(authentication);
        validarRequisicao(request);

        Consulta atual = consultas.get(id);
        if (atual == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada.");
        }

        Consulta novaVersao = new Consulta(
                atual.id(),
                request.pacienteId(),
                request.medicoId(),
                request.enfermeiroId(),
                request.dataHora(),
                request.duracaoMinutos(),
                request.motivo(),
                atual.status()
        );

        boolean existeConflito = consultas.values().stream()
                .filter(consulta -> !Objects.equals(consulta.id(), id))
                .anyMatch(consulta -> consulta.conflitaCom(novaVersao));

        if (existeConflito) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A atualização gera conflito de agenda para a mesma equipe ou paciente.");
        }

        consultas.put(id, novaVersao);
        
        // Publicar evento de consulta atualizada
        kafkaProducerService.enviarConsultaAtualizada(novaVersao);
        
        return novaVersao;
    }

    public void deletarConsulta(Long id, Authentication authentication) {
        validarPerfilResponsavel(authentication);
        
        Consulta consulta = consultas.get(id);
        if (consulta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada.");
        }

        consultas.remove(id);
        
        // Publicar evento de consulta deletada
        kafkaProducerService.enviarConsultaDeletada(id, consulta);
    }

    public void cancelarConsulta(Long id, Authentication authentication) {
        validarPerfilResponsavel(authentication);
        
        Consulta consulta = consultas.get(id);
        if (consulta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada.");
        }

        Consulta consultaCancelada = new Consulta(
                consulta.id(),
                consulta.pacienteId(),
                consulta.medicoId(),
                consulta.enfermeiroId(),
                consulta.dataHora(),
                consulta.duracaoMinutos(),
                consulta.motivo(),
                "CANCELADA"
        );

        consultas.put(id, consultaCancelada);
        
        // Publicar evento de consulta cancelada
        kafkaProducerService.enviarConsultaCancelada(consultaCancelada);
    }

    public Consulta confirmarConsulta(Long id, Authentication authentication) {
        validarPerfilResponsavel(authentication);
        
        Consulta consulta = consultas.get(id);
        if (consulta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada.");
        }

        Consulta consultaConfirmada = new Consulta(
                consulta.id(),
                consulta.pacienteId(),
                consulta.medicoId(),
                consulta.enfermeiroId(),
                consulta.dataHora(),
                consulta.duracaoMinutos(),
                consulta.motivo(),
                "CONFIRMADA"
        );

        consultas.put(id, consultaConfirmada);
        
        // Publicar evento de consulta confirmada
        kafkaProducerService.enviarConsultaConfirmada(consultaConfirmada);
        
        return consultaConfirmada;
    }

    private void validarRequisicao(ConsultaRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A consulta é obrigatória.");
        }
        if (request.pacienteId() == null || request.pacienteId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paciente obrigatório.");
        }
        if (request.medicoId() == null || request.medicoId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Médico obrigatório.");
        }
        if (request.enfermeiroId() == null || request.enfermeiroId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enfermeiro obrigatório.");
        }
        if (request.dataHora() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data e hora da consulta são obrigatórias.");
        }
        if (request.duracaoMinutos() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A duração deve ser superior a zero.");
        }
        if (request.dataHora().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A consulta deve ser agendada para uma data futura.");
        }

        int hora = request.dataHora().getHour();
        if (hora < 8 || hora >= 18) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "As consultas devem ocorrer entre 08:00 e 18:00.");
        }
    }

    private void validarPerfilResponsavel(Authentication authentication) {
        if (authentication == null || (!authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MEDICO") ||
                        authority.getAuthority().equals("ROLE_ENFERMEIRO")))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Apenas médicos e enfermeiros podem registrar ou editar consultas.");
        }
    }

    private boolean ehPaciente(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_PACIENTE"));
    }

    private void validarAcessoPaciente(Authentication authentication, String pacienteId) {
        if (ehPaciente(authentication)) {
            String pacienteAutenticado = authentication.getName();
            if (!pacienteAutenticado.equalsIgnoreCase(pacienteId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Pacientes só podem visualizar as próprias consultas.");
            }
        }
    }

    private void registrarConsultaSeeded(Consulta consulta) {
        consultas.put(consulta.id(), consulta);
        sequence.updateAndGet(current -> Math.max(current, consulta.id() + 1));
    }
}
