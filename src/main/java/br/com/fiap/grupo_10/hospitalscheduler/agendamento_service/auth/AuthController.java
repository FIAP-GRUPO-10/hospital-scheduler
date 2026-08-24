package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.auth;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.auth.dto.request.LoginRequest;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.auth.dto.response.LoginResponse;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.request.CriarUsuarioRequest;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.response.UsuarioResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponse> cadastrarUsuario(@RequestBody CriarUsuarioRequest request) {
        UsuarioResponse response = authService.criarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
