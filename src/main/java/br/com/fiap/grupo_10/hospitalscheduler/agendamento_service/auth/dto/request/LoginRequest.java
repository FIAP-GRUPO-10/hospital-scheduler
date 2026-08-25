package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.auth.dto.request;

public record LoginRequest(
        String email,
        String senha
) {}
