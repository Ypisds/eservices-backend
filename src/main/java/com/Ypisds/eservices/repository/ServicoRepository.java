package com.Ypisds.eservices.repository;

import com.Ypisds.eservices.model.Servico;
import com.Ypisds.eservices.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ServicoRepository extends JpaRepository<Servico, UUID>, JpaSpecificationExecutor<Servico> {
    Optional<Servico> findByAnuncianteAndTitulo(Usuario anunciante, String titulo);
}
