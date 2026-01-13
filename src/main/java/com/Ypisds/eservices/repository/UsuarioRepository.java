package com.Ypisds.eservices.repository;

import com.Ypisds.eservices.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByLoginOrEmail(String login, String email);
    Usuario findByLogin(String login);
}
