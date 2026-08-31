package br.com.fiap.hospital.modules.historico.model;

public record ConsultaResult(
    boolean sucesso,
    String mensagem,
    ConsultaDTO consulta
) {}

