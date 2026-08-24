package br.com.fiap.hospital.modules.historico.controller;

import br.com.fiap.hospital.modules.historico.model.ConsultaHistorico;
import br.com.fiap.hospital.modules.historico.service.HistoricoService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/historico")
public class HistoricoController {

    private final HistoricoService historicoService;

    public HistoricoController(HistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    @GetMapping
    public Map<String, String> status() {
        return Map.of("status", "ok");
    }

    @GetMapping("/consultas")
    public List<ConsultaHistorico> listarTodos(
            Authentication authentication,
            @RequestHeader(value = "X-Paciente-Id", required = false) String pacienteId) {
        return historicoService.listarTodos(authentication, pacienteId);
    }

    @GetMapping("/pacientes/{pacienteId}/consultas")
    public List<ConsultaHistorico> listarConsultasPaciente(
            @PathVariable String pacienteId,
            Authentication authentication,
            @RequestParam(defaultValue = "false") boolean futuras) {
        return historicoService.listarConsultasPaciente(pacienteId, futuras, authentication);
    }
}
