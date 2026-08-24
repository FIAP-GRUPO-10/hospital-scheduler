package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<StandardError> usuarioNaoEncontradoExceptionHandler(UsuarioNaoEncontradoException e,
                                                                              HttpServletRequest request) {
        String error = e.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        StandardError err = StandardError
                .builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(error)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ConsultaJaExistenteException.class)
    public ResponseEntity<StandardError> consultaJaExistenteException(ConsultaJaExistenteException e,
                                                                      HttpServletRequest request) {
        String error = e.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        StandardError err = StandardError
                .builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(error)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(err);
    }
}
