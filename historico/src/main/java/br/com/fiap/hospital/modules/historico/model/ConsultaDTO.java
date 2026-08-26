package br.com.fiap.hospital.modules.historico.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ConsultaDTO(
    String id,
    String pacienteId,
    String nomePaciente,
    String emailPaciente,
    String medicoId,
    String nomeMedico,
    String especialidade,
    LocalDateTime dataHora,
    String descricao,
    String status,
    String tipoConsulta,
    LocalDateTime criadaEm,
    LocalDateTime atualizadaEm
) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public String dataHoraFormatted() {
        return dataHora != null ? dataHora.format(FORMATTER) : null;
    }

    public String criadaEmFormatted() {
        return criadaEm != null ? criadaEm.format(FORMATTER) : null;
    }

    public String atualizadaEmFormatted() {
        return atualizadaEm != null ? atualizadaEm.format(FORMATTER) : null;
    }

    // Construtor a partir de ConsultaHistorico
    public static ConsultaDTO fromHistorico(ConsultaHistorico historico) {
        return new ConsultaDTO(
            String.valueOf(historico.id()),
            historico.pacienteId(),
            historico.nomePaciente(),
            historico.emailPaciente(),
            historico.medicoId(),
            historico.nomeMedico(),
            historico.especialidade(),
            historico.dataHora(),
            historico.descricao(),
            historico.status(),
            historico.tipoConsulta(),
            historico.criadaEm(),
            historico.atualizadaEm()
        );
    }
}

