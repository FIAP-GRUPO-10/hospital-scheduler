package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.exceptions;

public class UsuarioJaCadastradoException extends RuntimeException {
    public UsuarioJaCadastradoException(String message) {
        super(message);
    }
}
