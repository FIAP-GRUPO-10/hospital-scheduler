package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.entity;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.enums.StatusConsulta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    private Usuario medico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Usuario paciente;

    private LocalDateTime dataHora;

    private String observacoes;

    @Enumerated(EnumType.STRING)
    private StatusConsulta status;
}
