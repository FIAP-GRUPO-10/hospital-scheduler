package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.service;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.request.CriarUsuarioRequest;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.response.UsuarioResponse;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.entity.Usuario;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.exceptions.UsuarioJaCadastradoException;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioResponse criarUsuario(CriarUsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new UsuarioJaCadastradoException("Usuário já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setNome(request.nome());
        usuario.setRole(request.role());
        usuario.setSenha(passwordEncoder.encode(request.senha()));

        Usuario salvo = usuarioRepository.save(usuario);

        return new UsuarioResponse(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getRole()
        );
    }
}
