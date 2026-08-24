package br.com.fiap.hospital.modules.historico.service;

import br.com.fiap.hospital.modules.historico.model.ConsultaHistorico;
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
public class HistoricoService {

    private final Map<Long, ConsultaHistorico> historico = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1L);

    public HistoricoService() {
        registrarConsulta(new ConsultaHistorico(1L, "PAC-1001", "MED-2001", "ENF-3001",
                LocalDateTime.now().plusDays(1).withHour(10), "Consulta cardiológica", "AGENDADA"));
        registrarConsulta(new ConsultaHistorico(2L, "PAC-1001", "MED-2002", "ENF-3002",
                LocalDateTime.now().minusDays(10), "Acompanhamento anterior", "REALIZADA"));
        registrarConsulta(new ConsultaHistorico(3L, "PAC-1002", "MED-2001", "ENF-3001",
                LocalDateTime.now().plusDays(3).withHour(15), "Retorno pós-cirurgia", "AGENDADA"));
    }

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
}
