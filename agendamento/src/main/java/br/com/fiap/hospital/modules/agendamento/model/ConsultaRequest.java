package br.com.fiap.hospital.modules.agendamento.model;

import java.time.LocalDateTime;

public record ConsultaRequest(
        String pacienteId,
        String medicoId,
        String enfermeiroId,
        LocalDateTime dataHora,
        int duracaoMinutos,
        String motivo
) {
}
