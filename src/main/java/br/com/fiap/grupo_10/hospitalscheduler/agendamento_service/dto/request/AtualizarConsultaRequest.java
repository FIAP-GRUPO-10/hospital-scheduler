package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.request;

import java.time.LocalDateTime;

public record AtualizarConsultaRequest(
        LocalDateTime dataHora,
        String observacoes
) {}