package br.com.fiap.hospital.modules.historico.model;

import java.time.LocalDateTime;

public record ConsultaHistorico(
        Long id,
        String pacienteId,
        String nomePaciente,
        String emailPaciente,
        String medicoId,
        String nomeMedico,
        String especialidade,
        String enfermeiroId,
        LocalDateTime dataHora,
        String descricao,
        String motivo,
        String tipoConsulta,
        String status,
        LocalDateTime criadaEm,
        LocalDateTime atualizadaEm
) {
}
