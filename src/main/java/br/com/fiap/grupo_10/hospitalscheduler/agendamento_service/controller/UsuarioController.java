package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.controller;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    public final UsuarioService usuarioService;
}
