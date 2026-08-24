package br.com.fiap.hospital.modules.notificacoes.model;

import java.time.LocalDateTime;

public record Lembrete(
        Long id,
        String pacienteId,
        String medicoId,
        LocalDateTime dataConsulta,
        String mensagem,
        boolean enviado
) {
}
