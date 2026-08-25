package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.repository;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.entity.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    boolean existsByPacienteIdAndMedicoIdAndDataHora(Long pacienteId, Long medicoId, LocalDateTime dataHora);

    boolean existsByMedicoIdAndDataHora(Long medicoId, LocalDateTime dataHora);
}
