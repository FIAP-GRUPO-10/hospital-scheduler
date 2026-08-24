package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.response;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.entity.Usuario;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.coyote.Response;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    private String role;

    public static UsuarioResponse fromEntity(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .role(usuario.getRole().name())
                .build();
    }
}
