package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.response;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.enums.Role;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Role role
) {}
