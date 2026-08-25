package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.request;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.enums.Role;

public record CriarUsuarioRequest(
        String nome,
        String email,
        String senha,
        Role role
) {
}