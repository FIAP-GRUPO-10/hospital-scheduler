package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.response;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.enums.StatusConsulta;

import java.time.LocalDateTime;

public record ConsultaResponse(
        Long id,
        Long pacienteId,
        Long medicoId,
        LocalDateTime dataHora,
        String observacoes,
        StatusConsulta status
) {}