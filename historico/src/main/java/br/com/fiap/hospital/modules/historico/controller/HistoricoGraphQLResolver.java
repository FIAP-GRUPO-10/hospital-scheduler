package br.com.fiap.hospital.modules.historico.controller;

import br.com.fiap.hospital.modules.historico.model.ConsultaDTO;
import br.com.fiap.hospital.modules.historico.model.ConsultaFiltro;
import br.com.fiap.hospital.modules.historico.model.ConsultaHistorico;
import br.com.fiap.hospital.modules.historico.model.ConsultaResult;
import br.com.fiap.hospital.modules.historico.service.HistoricoService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HistoricoGraphQLResolver {

    private final HistoricoService historicoService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public HistoricoGraphQLResolver(HistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    // ============ QUERIES ============

    /**
     * Query: Retorna todas as consultas de um paciente
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('PACIENTE', 'MEDICO', 'ENFERMEIRO', 'ADMIN')")
    public List<ConsultaDTO> pacienteConsultas(@Argument String pacienteId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<ConsultaHistorico> consultas = historicoService.listarConsultasPaciente(pacienteId, false, auth);
        return consultas.stream()
                .map(ConsultaDTO::fromHistorico)
                .collect(Collectors.toList());
    }

    /**
     * Query: Retorna apenas consultas futuras de um paciente
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('PACIENTE', 'MEDICO', 'ENFERMEIRO', 'ADMIN')")
    public List<ConsultaDTO> pacienteConsultasFuturas(@Argument String pacienteId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LocalDateTime agora = LocalDateTime.now();
        List<ConsultaHistorico> consultas = historicoService.listarConsultasPaciente(pacienteId, true, auth);
        return consultas.stream()
                .filter(c -> c.dataHora().isAfter(agora))
                .map(ConsultaDTO::fromHistorico)
                .collect(Collectors.toList());
    }

    /**
     * Query: Retorna apenas consultas passadas de um paciente
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('PACIENTE', 'MEDICO', 'ENFERMEIRO', 'ADMIN')")
    public List<ConsultaDTO> pacienteConsultasPassadas(@Argument String pacienteId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LocalDateTime agora = LocalDateTime.now();
        List<ConsultaHistorico> consultas = historicoService.listarConsultasPaciente(pacienteId, false, auth);
        return consultas.stream()
                .filter(c -> c.dataHora().isBefore(agora))
                .map(ConsultaDTO::fromHistorico)
                .collect(Collectors.toList());
    }

    /**
     * Query: Busca consultas por médico
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'ADMIN')")
    public List<ConsultaDTO> medicoConsultas(@Argument String medicoId) {
        List<ConsultaHistorico> consultas = historicoService.listarTodos(null, null).stream()
                .filter(c -> medicoId.equalsIgnoreCase(c.medicoId()))
                .collect(Collectors.toList());
        return consultas.stream()
                .map(ConsultaDTO::fromHistorico)
                .collect(Collectors.toList());
    }

    /**
     * Query: Busca consulta específica pelo ID
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('PACIENTE', 'MEDICO', 'ENFERMEIRO', 'ADMIN')")
    public ConsultaDTO consulta(@Argument String id) {
        List<ConsultaHistorico> todos = historicoService.listarTodos(null, null);
        return todos.stream()
                .filter(c -> c.id().toString().equals(id))
                .map(ConsultaDTO::fromHistorico)
                .findFirst()
                .orElse(null);
    }

    /**
     * Query: Busca consultas com filtros avançados
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('PACIENTE', 'MEDICO', 'ENFERMEIRO', 'ADMIN')")
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

    /**
     * Mutation: Cancela uma consulta
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'ADMIN')")
    public ConsultaResult cancelarConsulta(@Argument String id) {
        try {
            // Em uma implementação real, você atualizaria o status no banco de dados
            // e publicaria um evento de cancelamento no Kafka
            return new ConsultaResult(
                    true,
                    "Consulta " + id + " cancelada com sucesso",
                    null
            );
        } catch (Exception e) {
            return new ConsultaResult(
                    false,
                    "Erro ao cancelar consulta: " + e.getMessage(),
                    null
            );
        }
    }

    /**
     * Mutation: Marca consulta como concluída
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'ADMIN')")
    public ConsultaResult concluirConsulta(@Argument String id) {
        try {
            // Em uma implementação real, você atualizaria o status no banco de dados
            // e publicaria um evento de conclusão no Kafka
            return new ConsultaResult(
                    true,
                    "Consulta " + id + " marcada como concluída",
                    null
            );
        } catch (Exception e) {
            return new ConsultaResult(
                    false,
                    "Erro ao concluir consulta: " + e.getMessage(),
                    null
            );
        }
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

