package br.com.fiap.hospital.modules.agendamento.controller;

import br.com.fiap.hospital.modules.agendamento.model.Consulta;
import br.com.fiap.hospital.modules.agendamento.model.ConsultaRequest;
import br.com.fiap.hospital.modules.agendamento.service.AgendamentoService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/agenda")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @GetMapping
    public Map<String, String> status() {
        return Map.of("status", "ok");
    }

    @GetMapping("/consultas")
    public List<Consulta> listarConsultas(
            Authentication authentication,
            @RequestHeader(value = "X-Paciente-Id", required = false) String pacienteId) {
        return agendamentoService.listarConsultas(authentication, pacienteId);
    }

    @GetMapping("/consultas/{id}")
    public Consulta consultarPorId(@PathVariable Long id, Authentication authentication) {
        return agendamentoService.buscarPorId(id, authentication);
    }

    @PostMapping("/consultas")
    public Consulta criarConsulta(
            @RequestBody ConsultaRequest request,
            Authentication authentication) {
        return agendamentoService.criarConsulta(request, authentication);
    }

    @PutMapping("/consultas/{id}")
    public Consulta atualizarConsulta(
            @PathVariable Long id,
            @RequestBody ConsultaRequest request,
            Authentication authentication) {
        return agendamentoService.atualizarConsulta(id, request, authentication);
    }
}
