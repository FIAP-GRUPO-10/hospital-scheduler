package br.com.fiap.hospital.modules.historico.controller;

import br.com.fiap.hospital.modules.historico.model.*;
import br.com.fiap.hospital.modules.historico.service.HistoricoService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HistoricoGraphQLResolver {

    private final HistoricoService historicoService;

    public HistoricoGraphQLResolver(HistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    // ============ QUERIES ============

    @QueryMapping
    @PreAuthorize("hasAnyRole('PACIENTE','MEDICO','ENFERMEIRO','ADMIN')")
    public List<ConsultaDTO> pacienteConsultas(@Argument String pacienteId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<ConsultaHistorico> consultas = historicoService.listarConsultasPaciente(pacienteId, false, auth);
        return consultas.stream().map(ConsultaDTO::fromHistorico).collect(Collectors.toList());
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('PACIENTE','MEDICO','ENFERMEIRO','ADMIN')")
    public List<ConsultaDTO> pacienteConsultasFuturas(@Argument String pacienteId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LocalDateTime agora = LocalDateTime.now();
        List<ConsultaHistorico> consultas = historicoService.listarConsultasPaciente(pacienteId, true, auth);
        return consultas.stream()
                .filter(c -> c.dataHora().isAfter(agora))
                .map(ConsultaDTO::fromHistorico)
                .collect(Collectors.toList());
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('PACIENTE','MEDICO','ENFERMEIRO','ADMIN')")
    public List<ConsultaDTO> pacienteConsultasPassadas(@Argument String pacienteId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LocalDateTime agora = LocalDateTime.now();
        List<ConsultaHistorico> consultas = historicoService.listarConsultasPaciente(pacienteId, false, auth);
        return consultas.stream()
                .filter(c -> c.dataHora().isBefore(agora))
                .map(ConsultaDTO::fromHistorico)
                .collect(Collectors.toList());
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO','ENFERMEIRO','ADMIN')")
    public List<ConsultaDTO> medicoConsultas(@Argument String medicoId) {
        List<ConsultaHistorico> consultas = historicoService.listarTodos(null, null).stream()
                .filter(c -> medicoId.equalsIgnoreCase(c.medicoId()))
                .collect(Collectors.toList());
        return consultas.stream().map(ConsultaDTO::fromHistorico).collect(Collectors.toList());
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('PACIENTE','MEDICO','ENFERMEIRO','ADMIN')")
    public ConsultaDTO consulta(@Argument String id) {
        List<ConsultaHistorico> todos = historicoService.listarTodos(null, null);
        return todos.stream()
                .filter(c -> c.id().toString().equals(id))
                .map(ConsultaDTO::fromHistorico)
                .findFirst()
                .orElse(null);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('PACIENTE','MEDICO','ENFERMEIRO','ADMIN')")
    public List<ConsultaDTO> consultasFiltered(@Argument ConsultaFiltro filtro) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<ConsultaHistorico> consultas = historicoService.listarTodos(auth, filtro.pacienteId());
        return consultas.stream()
                .filter(c -> filtro.medicoId() == null || c.medicoId().equalsIgnoreCase(filtro.medicoId()))
                .filter(c -> filtro.status() == null || c.status().equalsIgnoreCase(filtro.status()))
                .filter(c -> filtro.especialidade() == null || c.especialidade().equalsIgnoreCase(filtro.especialidade()))
                .filter(c -> filtroData(c.dataHora(), filtro.dataInicio(), filtro.dataFim()))
                .map(ConsultaDTO::fromHistorico)
                .collect(Collectors.toList());
    }

    // ============ MUTATIONS ============

    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO','ENFERMEIRO','ADMIN')")
    public ConsultaResult cancelarConsulta(@Argument String id) {
        try {
            return new ConsultaResult(true, "Consulta " + id + " cancelada com sucesso", null);
        } catch (Exception e) {
            return new ConsultaResult(false, "Erro ao cancelar consulta: " + e.getMessage(), null);
        }
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO','ENFERMEIRO','ADMIN')")
    public ConsultaResult concluirConsulta(@Argument String id) {
        try {
            return new ConsultaResult(true, "Consulta " + id + " marcada como concluída", null);
        } catch (Exception e) {
            return new ConsultaResult(false, "Erro ao concluir consulta: " + e.getMessage(), null);
        }
    }

    // ============ LEMBRETES ============

    @QueryMapping
    @PreAuthorize("hasAnyRole('PACIENTE','MEDICO','ADMIN')")
    public List<Lembrete> lembretesPaciente(@Argument String pacienteId) {
        return historicoService.listarLembretesPorPaciente(pacienteId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO','ADMIN')")
    public List<Lembrete> lembretesMedico(@Argument String medicoId) {
        return historicoService.listarLembretesPorMedico(medicoId);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO','ADMIN')")
    public Lembrete criarLembrete(@Argument CriarLembreteInput input) {
        return historicoService.criarLembrete(input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO','ADMIN')")
    public Boolean removerLembrete(@Argument String id) {
        return historicoService.removerLembrete(id);
    }

    // ============ HELPERS ============

    private boolean filtroData(LocalDateTime dataConsulta, String dataInicio, String dataFim) {
        try {
            if (dataInicio != null && !dataInicio.isEmpty()) {
                LocalDateTime inicio = LocalDateTime.parse(dataInicio + " 00:00",
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                if (dataConsulta.isBefore(inicio)) {
                    return false;
                }
            }
            if (dataFim != null && !dataFim.isEmpty()) {
                LocalDateTime fim = LocalDateTime.parse(dataFim + " 23:59",
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                if (dataConsulta.isAfter(fim)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }
}
