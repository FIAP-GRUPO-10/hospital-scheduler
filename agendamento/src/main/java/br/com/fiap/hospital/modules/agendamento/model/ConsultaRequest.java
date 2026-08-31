package br.com.fiap.hospital.modules.agendamento.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ConsultaRequest(
        String pacienteId,
        String medicoId,
        String enfermeiroId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dataHora,
        int duracaoMinutos,
        String motivo
) {
}
