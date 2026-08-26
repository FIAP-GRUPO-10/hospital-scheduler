package br.com.fiap.hospital.modules.notificacoes.model;

import java.time.LocalDateTime;

public record LembreteRequest(
        String pacienteId,
        String medicoId,
        LocalDateTime dataConsulta,
        String mensagem
) {
}
