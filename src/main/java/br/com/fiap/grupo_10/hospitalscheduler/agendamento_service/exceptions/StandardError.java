package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class StandardError {

    private Instant timestamp;
    private Integer status;
    private String error;
    private String path;
    private String mensagem;
    private Map<String, String> validationErrors;
}