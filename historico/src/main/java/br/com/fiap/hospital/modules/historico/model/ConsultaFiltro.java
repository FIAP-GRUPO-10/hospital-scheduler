package br.com.fiap.hospital.modules.historico.model;

public record ConsultaFiltro(
    String pacienteId,
    String medicoId,
    String status,
    String dataInicio,
    String dataFim,
    String especialidade
) {}

