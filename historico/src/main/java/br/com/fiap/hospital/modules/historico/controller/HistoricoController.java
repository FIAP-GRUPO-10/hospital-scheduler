package br.com.fiap.hospital.modules.historico.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/historico")
public class HistoricoController {

    @GetMapping
    public Map<String, String> listar() {
        return Map.of("status", "ok");
    }
}
