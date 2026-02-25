package com.Ypisds.eservices.repository.specification;


import com.Ypisds.eservices.dto.response.ServicoResponseDTO;
import com.Ypisds.eservices.enums.CategoriaServico;
import com.Ypisds.eservices.enums.Status;
import com.Ypisds.eservices.model.Servico;
import com.Ypisds.eservices.model.Usuario;
import com.Ypisds.eservices.repository.ServicoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@DataJpaTest
public class ServiceSpecificationTest {

    @Autowired
    ServicoRepository repository;

    @Autowired
    TestEntityManager entityManager;

    Servico servico1;
    Servico servico2;


    @BeforeEach
    void setup(){
        Usuario anunciante = new Usuario();
        anunciante.setLogin("login");
        anunciante.setPassword("password");
        anunciante.setEmail("email@gmail.com");

        entityManager.persist(anunciante);

        servico1 = new Servico();
        servico2 = new Servico();

        servico1.setTitulo("OVNI");
        servico1.setPreco(BigDecimal.TEN);
        servico1.setDescricao("TUDO SOBRE OVNIS");
        servico1.setStatus(Status.DISPONIVEL);
        servico1.setAnunciante(anunciante);

        servico2.setTitulo("Politica");
        servico2.setPreco(BigDecimal.valueOf(100));
        servico2.setDescricao("Esquerda vs Direita");
        servico2.setStatus(Status.INDISPONIVEL);
        servico2.setAnunciante(anunciante);

        repository.save(servico1);
        repository.save(servico2);


    }

    @AfterEach
    void tearDown(){
        repository.delete(servico1);
        repository.delete(servico2);
    }

    @Test
    void deveRetornarUmaPaginaComOTituloIgual(){
        Specification<Servico> specs = Specification.where(ServiceSpecification.hasTituloLike("OVNI"));

        Page<ServicoResponseDTO> pages = repository.findBy(specs, q-> q.as(Servico.class)
                .project("categoria", "anunciante")
                .page(PageRequest.of(0, 10))
                .map(f -> new ServicoResponseDTO(
                        f.getId(),
                        f.getTitulo(),
                        f.getDescricao(),
                        f.getPreco(),
                        f.getCategoria(),
                        f.getStatus(),
                        f.getAnunciante().getId()
                )));

        assertEquals(1, pages.getTotalElements());
        assertEquals("OVNI", pages.getContent().getFirst().titulo());

    }

    @Test
    void deveRetornarUmaPaginaComOTituloParecido(){
        Specification<Servico> specs = Specification.where(ServiceSpecification.hasTituloLike("OVN"));

        Page<ServicoResponseDTO> pages = repository.findBy(specs, q-> q.as(Servico.class)
                .project("categoria", "anunciante")
                .page(PageRequest.of(0, 10))
                .map(f -> new ServicoResponseDTO(
                        f.getId(),
                        f.getTitulo(),
                        f.getDescricao(),
                        f.getPreco(),
                        f.getCategoria(),
                        f.getStatus(),
                        f.getAnunciante().getId()
                )));

        assertEquals(1, pages.getTotalElements());
        assertEquals("OVNI", pages.getContent().getFirst().titulo());
    }

    @Test
    void deveRetornarUmaPaginaComConteudosDeStatusIndisponivel(){
        Specification<Servico> specs = Specification.where(ServiceSpecification.hasStatusEquals(Status.INDISPONIVEL));

        Page<ServicoResponseDTO> pages = repository.findBy(specs, q-> q.as(Servico.class)
                .project("categoria", "anunciante")
                .page(PageRequest.of(0, 10))
                .map(f -> new ServicoResponseDTO(
                        f.getId(),
                        f.getTitulo(),
                        f.getDescricao(),
                        f.getPreco(),
                        f.getCategoria(),
                        f.getStatus(),
                        f.getAnunciante().getId()
                )));

        assertEquals(1, pages.getTotalElements());
        assertEquals("Politica", pages.getContent().getFirst().titulo());
        assertEquals(Status.INDISPONIVEL, pages.getContent().getFirst().status());
    }



}
