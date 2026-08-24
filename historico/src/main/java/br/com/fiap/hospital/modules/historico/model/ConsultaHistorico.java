package br.com.fiap.hospital.modules.historico.model;

import java.time.LocalDateTime;

public record ConsultaHistorico(
        Long id,
        String pacienteId,
        String medicoId,
        String enfermeiroId,
        LocalDateTime dataHora,
        String motivo,
        String status
) {
}
