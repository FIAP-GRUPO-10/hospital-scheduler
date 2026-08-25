package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.exceptions;

public class ConsultaJaExistenteException extends RuntimeException {
    public ConsultaJaExistenteException(String message) {
        super(message);
    }
}
