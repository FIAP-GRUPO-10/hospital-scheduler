package br.com.fiap.hospital.modules.agendamento.controller;

import br.com.fiap.hospital.modules.agendamento.model.Consulta;
import br.com.fiap.hospital.modules.agendamento.model.ConsultaRequest;
import br.com.fiap.hospital.modules.agendamento.service.AgendamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Consulta> criarConsulta(
            @RequestBody ConsultaRequest request, Authentication authentication) {
        Consulta consulta = agendamentoService.criarConsulta(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(consulta);
    }

    @PutMapping("/consultas/{id}")
    public Consulta atualizarConsulta(
            @PathVariable Long id,
            @RequestBody ConsultaRequest request,
            Authentication authentication) {
        return agendamentoService.atualizarConsulta(id, request, authentication);
    }

    @DeleteMapping("/consultas/{id}")
    public ResponseEntity<Map<String, String>> deletarConsulta(
            @PathVariable Long id,
            Authentication authentication) {
        agendamentoService.deletarConsulta(id, authentication);
        return ResponseEntity.ok(Map.of("mensagem", "Consulta deletada com sucesso"));
    }

    @PutMapping("/consultas/{id}/cancelar")
    public ResponseEntity<Map<String, String>> cancelarConsulta(
            @PathVariable Long id,
            Authentication authentication) {
        agendamentoService.cancelarConsulta(id, authentication);
        return ResponseEntity.ok(Map.of("mensagem", "Consulta cancelada com sucesso"));
    }

    @PutMapping("/consultas/{id}/confirmar")
    public ResponseEntity<Consulta> confirmarConsulta(
            @PathVariable Long id,
            Authentication authentication) {
        Consulta consulta = agendamentoService.confirmarConsulta(id, authentication);
        return ResponseEntity.ok(consulta);
    }
}
