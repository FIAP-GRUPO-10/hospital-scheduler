package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.exceptions;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }
}
