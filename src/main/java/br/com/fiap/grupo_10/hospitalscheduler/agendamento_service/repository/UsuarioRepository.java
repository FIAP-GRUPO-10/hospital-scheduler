package br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.repository;

import br.com.fiap.grupo_10.hospitalscheduler.agendamento_service.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
