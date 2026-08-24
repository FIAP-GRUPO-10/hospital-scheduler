package br.com.fiap.hospital.modules.notificacoes.controller;

import br.com.fiap.hospital.modules.notificacoes.model.Lembrete;
import br.com.fiap.hospital.modules.notificacoes.model.LembreteRequest;
import br.com.fiap.hospital.modules.notificacoes.service.NotificacaoService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notificacoes")
public class NotificacoesController {

    private final NotificacaoService notificacaoService;

    public NotificacoesController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping
    public Map<String, String> status() {
        return Map.of("status", "ok");
    }

    @GetMapping("/lembretes")
    public List<Lembrete> listarLembretes(
            Authentication authentication,
            @RequestHeader(value = "X-Paciente-Id", required = false) String pacienteId) {
        return notificacaoService.listarLembretes(authentication, pacienteId);
    }

    @PostMapping("/lembretes")
    public Lembrete criarLembrete(
            @RequestBody LembreteRequest request,
            Authentication authentication) {
        return notificacaoService.criarLembrete(request, authentication);
    }
}
