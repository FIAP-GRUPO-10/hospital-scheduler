package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.request;

import java.time.LocalDateTime;

public record CriarConsultaRequest(
        Long pacienteId,
        Long medicoId,
        LocalDateTime dataHora,
        String observacoes
) {}
