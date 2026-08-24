package br.com.fiap.grupo_10.hospitalscheduler.shared_events;

import java.time.LocalDateTime;

public record ConsultaCriadaEvent(
        Long consultaId,
        Long pacienteId,
        String pacienteEmail,
        Long medicoId,
        LocalDateTime dataHora,
        String observacoes
) {
}