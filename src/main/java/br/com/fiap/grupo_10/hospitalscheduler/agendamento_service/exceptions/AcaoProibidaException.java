package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.exceptions;

public class AcaoProibidaException extends RuntimeException {
    public AcaoProibidaException(String message) {
        super(message);
    }
}
