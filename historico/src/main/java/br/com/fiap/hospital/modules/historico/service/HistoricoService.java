package br.com.fiap.hospital.modules.historico.service;

import br.com.fiap.hospital.modules.historico.model.ConsultaHistorico;
import br.com.fiap.hospital.modules.historico.model.CriarLembreteInput;
import br.com.fiap.hospital.modules.historico.model.Lembrete;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class HistoricoService {

    private final Map<Long, ConsultaHistorico> historico = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1L);

    // Novo: armazenamento de lembretes
    private final Map<String, Lembrete> lembretes = new ConcurrentHashMap<>();

    // ================== CONSULTAS ==================

    public List<ConsultaHistorico> listarTodos(Authentication authentication, String pacienteId) {
        if (ehPaciente(authentication)) {
            String pacienteAutenticado = authentication.getName();
            if (pacienteId != null && !pacienteId.isBlank() && !pacienteAutenticado.equalsIgnoreCase(pacienteId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Pacientes só podem consultar o próprio histórico.");
            }
            return historico.values().stream()
                    .filter(consulta -> pacienteAutenticado.equalsIgnoreCase(consulta.pacienteId()))
                    .toList();
        }

        if (pacienteId != null && !pacienteId.isBlank()) {
            return historico.values().stream()
                    .filter(consulta -> pacienteId.equalsIgnoreCase(consulta.pacienteId()))
                    .toList();
        }

        return new ArrayList<>(historico.values());
    }

    public List<ConsultaHistorico> listarConsultasPaciente(String pacienteId, boolean somenteFuturas,
                                                           Authentication authentication) {
        if (pacienteId == null || pacienteId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paciente obrigatório.");
        }

        if (ehPaciente(authentication)) {
            String pacienteAutenticado = authentication.getName();
            if (!pacienteAutenticado.equalsIgnoreCase(pacienteId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Pacientes só podem consultar o próprio histórico.");
            }
        }

        return historico.values().stream()
                .filter(consulta -> pacienteId.equalsIgnoreCase(consulta.pacienteId()))
                .filter(consulta -> !somenteFuturas || consulta.dataHora().isAfter(LocalDateTime.now()))
                .toList();
    }

    private boolean ehPaciente(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_PACIENTE"));
    }

    private void registrarConsulta(ConsultaHistorico consulta) {
        historico.put(consulta.id(), consulta);
        sequence.updateAndGet(current -> Math.max(current, consulta.id() + 1));
    }

    public void armazenarConsulta(ConsultaHistorico consulta) {
        if (consulta != null && consulta.id() != null && !historico.containsKey(consulta.id())) {
            registrarConsulta(consulta);
        }
    }

    public ConsultaHistorico editarConsulta(Long id, ConsultaHistorico consulta, Authentication authentication) {
        if (id == null || !historico.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta não encontrada.");
        }
        boolean ehMedico = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MEDICO"));
        if (!ehMedico) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente médicos podem editar consultas.");
        }
        ConsultaHistorico existente = historico.get(id);
        ConsultaHistorico atualizado = new ConsultaHistorico(
                id,
                consulta.pacienteId() != null ? consulta.pacienteId() : existente.pacienteId(),
                consulta.nomePaciente() != null ? consulta.nomePaciente() : existente.nomePaciente(),
                consulta.emailPaciente() != null ? consulta.emailPaciente() : existente.emailPaciente(),
                consulta.medicoId() != null ? consulta.medicoId() : existente.medicoId(),
                consulta.nomeMedico() != null ? consulta.nomeMedico() : existente.nomeMedico(),
                consulta.especialidade() != null ? consulta.especialidade() : existente.especialidade(),
                consulta.enfermeiroId() != null ? consulta.enfermeiroId() : existente.enfermeiroId(),
                consulta.dataHora() != null ? consulta.dataHora() : existente.dataHora(),
                consulta.descricao() != null ? consulta.descricao() : existente.descricao(),
                consulta.motivo() != null ? consulta.motivo() : existente.motivo(),
                consulta.tipoConsulta() != null ? consulta.tipoConsulta() : existente.tipoConsulta(),
                consulta.status() != null ? consulta.status() : existente.status(),
                existente.criadaEm(),
                LocalDateTime.now()
        );

        historico.put(id, atualizado);
        return atualizado;
    }

    // ================== LEMBRETES ==================

    public List<Lembrete> listarLembretesPorPaciente(String pacienteId) {
        return lembretes.values().stream()
                .filter(l -> l.getPacienteId().equalsIgnoreCase(pacienteId))
                .toList();
    }

    public List<Lembrete> listarLembretesPorMedico(String medicoId) {
        return lembretes.values().stream()
                .filter(l -> l.getMedicoId().equalsIgnoreCase(medicoId))
                .toList();
    }

    public Lembrete criarLembrete(CriarLembreteInput input) {
        String id = UUID.randomUUID().toString();
        Lembrete lembrete = new Lembrete(
                id,
                input.getPacienteId(),
                input.getMedicoId(),
                input.getDataConsulta(),
                input.getMensagem()
        );
        lembretes.put(id, lembrete);
        return lembrete;
    }

    public Boolean removerLembrete(String id) {
        return lembretes.remove(id) != null;
    }
}
