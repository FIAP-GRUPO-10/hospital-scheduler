package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.controller;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.request.CriarConsultaRequest;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.response.ConsultaResponse;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.service.ConsultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/consulta")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService consultaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ENFERMEIRO')")
    public ResponseEntity<ConsultaResponse> criarConsulta(@RequestBody CriarConsultaRequest request) {
        ConsultaResponse response = consultaService.criarConsulta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
