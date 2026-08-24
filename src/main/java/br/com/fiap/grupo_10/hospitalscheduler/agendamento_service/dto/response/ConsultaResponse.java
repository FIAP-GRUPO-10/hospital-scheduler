package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.response;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.entity.Consulta;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.entity.Usuario;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.enums.StatusConsulta;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConsultaResponse {
    private Long id;
    private UsuarioResponse paciente;
    private UsuarioResponse medico;
    LocalDateTime dataHora;
    String observacoes;
    String status;

    public static ConsultaResponse fromEntity(Consulta consulta) {
        ConsultaResponse build = ConsultaResponse.builder()
                .id(consulta.getId())
                .paciente(UsuarioResponse.fromEntity(consulta.getPaciente()))
                .medico(UsuarioResponse.fromEntity(consulta.getMedico()))
                .dataHora(consulta.getDataHora())
                .status(consulta.getStatus().name())
                .build();

        if (consulta.getObservacoes() != null) {
            build.setObservacoes(consulta.getObservacoes());
        }

        return build;
    }
}