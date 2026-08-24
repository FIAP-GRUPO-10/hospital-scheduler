package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.service;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.request.CriarConsultaRequest;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.dto.response.ConsultaResponse;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.entity.Consulta;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.entity.Usuario;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.enums.Role;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.enums.StatusConsulta;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.exceptions.AcaoProibidaException;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.exceptions.ConsultaJaExistenteException;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.exceptions.UsuarioNaoEncontradoException;
import br.com.fiap.grupo_10.hospitalscheduler.shared_events.ConsultaCriadaEvent;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.repository.ConsultaRepository;
import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final UsuarioRepository usuarioRepository;
    private final KafkaTemplate<String, ConsultaCriadaEvent> kafkaTemplate;

    public ConsultaResponse criarConsulta(CriarConsultaRequest request) {

        if (consultaRepository.existsByPacienteIdAndMedicoIdAndDataHora(request.pacienteId(), request.medicoId(), request.dataHora())) {
            throw new ConsultaJaExistenteException("Paciente já possuí uma consulta na data escolhida");
        }

        Usuario medico = buscarUsuarioPorId(request.medicoId());
        Usuario paciente = buscarUsuarioPorId(request.pacienteId());

        this.validarConsulta(request, medico, paciente);

        Consulta consulta = new Consulta();
        consulta.setMedico(medico);
        consulta.setPaciente(paciente);
        consulta.setDataHora(request.dataHora());
        consulta.setStatus(StatusConsulta.AGENDADA);

        if (request.observacoes() != null) {
            consulta.setObservacoes(request.observacoes());
        }

        Consulta save = consultaRepository.save(consulta);

        ConsultaCriadaEvent event = new ConsultaCriadaEvent(
                save.getId(),
                save.getPaciente().getId(),
                save.getPaciente().getEmail(),
                save.getMedico().getId(),
                save.getDataHora(),
                save.getObservacoes()
        );

        kafkaTemplate.send("consulta-topic", event);
        return ConsultaResponse.fromEntity(save);

    }

    private Usuario buscarUsuarioPorId(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario não encontrado"));
    }

    private void validarConsulta(CriarConsultaRequest request, Usuario medico, Usuario paciente) {
        if (request.dataHora().isBefore(LocalDateTime.now())) {
            throw new AcaoProibidaException("Data da consulta não pode ser no passado");
        }

        if (!medico.getRole().equals(Role.MEDICO)) {
            throw new AcaoProibidaException("Medico não encontrado");
        }

        if (!paciente.getRole().equals(Role.PACIENTE)) {
            throw new AcaoProibidaException("Paciente não encontrado");
        }

        if (consultaRepository.existsByMedicoIdAndDataHora(medico.getId(), request.dataHora())) {
            throw new AcaoProibidaException("Médico indisponível no momento");
        }
    }
}
