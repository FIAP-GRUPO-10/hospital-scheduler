package br.com.fiap.hospital.modules.agendamento.model;

import java.time.LocalDateTime;

public record Consulta(
        Long id,
        String pacienteId,
        String medicoId,
        String enfermeiroId,
        LocalDateTime dataHora,
        int duracaoMinutos,
        String motivo,
        String status
) {

    public boolean conflitaCom(Consulta outra) {
        if (outra == null) {
            return false;
        }

        boolean mesmoPaciente = pacienteId != null && pacienteId.equalsIgnoreCase(outra.pacienteId());
        boolean mesmoMedico = medicoId != null && medicoId.equalsIgnoreCase(outra.medicoId());
        boolean mesmoEnfermeiro = enfermeiroId != null && outra.enfermeiroId() != null
                && enfermeiroId.equalsIgnoreCase(outra.enfermeiroId());

        if (!mesmoPaciente && !mesmoMedico && !mesmoEnfermeiro) {
            return false;
        }

        LocalDateTime inicioAtual = dataHora;
        LocalDateTime fimAtual = dataHora.plusMinutes(duracaoMinutos);
        LocalDateTime inicioOutra = outra.dataHora();
        LocalDateTime fimOutra = outra.dataHora().plusMinutes(outra.duracaoMinutos());

        return inicioAtual.isBefore(fimOutra) && fimAtual.isAfter(inicioOutra);
    }
}
