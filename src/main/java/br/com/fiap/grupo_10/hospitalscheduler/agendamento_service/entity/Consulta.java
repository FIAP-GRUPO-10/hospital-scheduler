package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.entity;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.enums.StatusConsulta;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pacienteId;

    private Long medicoId;

    private LocalDateTime dataHora;

    private String observacoes;

    @Enumerated(EnumType.STRING)
    private StatusConsulta status;
}
