package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.auth;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.auth.dto.request.LoginRequest;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.auth.dto.response.LoginResponse;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.request.CriarUsuarioRequest;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.response.UsuarioResponse;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.entity.Usuario;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.exceptions.UsuarioJaCadastradoException;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.repository.UsuarioRepository;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow();

        String token = jwtService.generateToken(usuario);
        return new LoginResponse(token);
    }

    public UsuarioResponse criarUsuario(CriarUsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new UsuarioJaCadastradoException("Usuário já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setNome(request.nome());
        usuario.setRole(request.role());
        usuario.setSenha(passwordEncoder.encode(request.senha()));

        Usuario save = usuarioRepository.save(usuario);

        return UsuarioResponse.fromEntity(save);
    }
}