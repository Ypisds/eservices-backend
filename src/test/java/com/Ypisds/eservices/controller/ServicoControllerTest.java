package com.Ypisds.eservices.controller;

import com.Ypisds.eservices.config.TokenFilter;
import com.Ypisds.eservices.dto.request.ServicoPatchRequestDTO;
import com.Ypisds.eservices.dto.request.ServicoRequestDTO;
import com.Ypisds.eservices.dto.response.ServicoResponseDTO;
import com.Ypisds.eservices.enums.CategoriaServico;
import com.Ypisds.eservices.enums.Status;
import com.Ypisds.eservices.exception.SameServiceExistsException;
import com.Ypisds.eservices.exception.ServiceNotExistsException;
import com.Ypisds.eservices.exception.UnauthorizedServiceOperationException;
import com.Ypisds.eservices.infra.exception.GlobalExceptionHandler;
import com.Ypisds.eservices.infra.exception.ServicoExceptionHandler;
import com.Ypisds.eservices.model.Servico;
import com.Ypisds.eservices.model.Usuario;
import com.Ypisds.eservices.service.ServicoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@WebMvcTest(value = {ServicoController.class, ServicoExceptionHandler.class, GlobalExceptionHandler.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = TokenFilter.class
        ))
public class ServicoControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ServicoService service;

    ServicoRequestDTO requestDTO;
    ServicoPatchRequestDTO patchRequestDTO;
    ServicoResponseDTO responseDTO;
    ServicoResponseDTO patchResponseDTO;
    Usuario usuarioCriador;
    final UUID usuarioId = UUID.randomUUID();
    final UUID patchId = UUID.randomUUID();

    @BeforeEach
    void setup(){
        requestDTO = new ServicoRequestDTO("titulo", "descricao", BigDecimal.TEN, CategoriaServico.OUTROS, Status.DISPONIVEL);
        patchRequestDTO = new ServicoPatchRequestDTO("titulo novo", "descricao nova", BigDecimal.ONE, CategoriaServico.AULAS, Status.PAUSADO);
        responseDTO = new ServicoResponseDTO(UUID.randomUUID(), "titulo", "descricao", BigDecimal.TEN, CategoriaServico.OUTROS, Status.DISPONIVEL, usuarioId);
        usuarioCriador = new Usuario("email@gmail.com", "usuario", "senha");
        usuarioCriador.setId(usuarioId);
        patchResponseDTO = new ServicoResponseDTO(patchId, "titulo novo", "descricao nova", BigDecimal.ONE, CategoriaServico.AULAS, Status.PAUSADO, usuarioId);

    }

    @Test
    void deveCriarUmServicoComSucesso() throws Exception{
        String json = """
                {
                    "titulo": "titulo",
                    "descricao": "descricao",
                    "preco": 10.00,
                    "categoria": "OUTROS",
                    "status": "PAUSADO"
                }
                """;

        when(service.createServico(any())).thenReturn(responseDTO);


        mvc.perform(
                MockMvcRequestBuilders
                        .post("/servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.titulo").value(responseDTO.titulo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.descricao").value(responseDTO.descricao()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preco").value(responseDTO.preco()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoria").value(responseDTO.categoria().name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(responseDTO.status().name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.idAnunciante").value(responseDTO.idAnunciante().toString()));

    }

    @Test
    void deveFalharAoExistirUmServico() throws Exception{
        String json = """
                {
                    "titulo": "titulo",
                    "descricao": "descricao",
                    "preco": 10.00,
                    "categoria": "OUTROS",
                    "status": "PAUSADO"
                }
                """;

        when(service.createServico(any())).thenThrow(new SameServiceExistsException("Serviço igual já existente para o usuário"));

        mvc.perform(
                MockMvcRequestBuilders
                        .post("/servico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Serviço igual já existente para o usuário"));
    }

    @Test
    void deveFazerPatchComIdErrado() throws Exception{
        String json = """
                {
                    "titulo": "titulo novo",
                    "descricao": "descricao nova",
                    "preco": 1.00,
                    "categoria": "AULAS",
                    "status": "PAUSADO"
                }
                """;
        String badId = "aspdo012zxc";

        mvc.perform(
                MockMvcRequestBuilders
                        .patch("/servico/%s".formatted(badId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").isNotEmpty());
    }

    @Test
    void deveFazerPatchComSucesso() throws Exception{
        String json = """
                {
                    "titulo": "titulo novo",
                    "descricao": "descricao nova",
                    "preco": 1.00,
                    "categoria": "AULAS",
                    "status": "PAUSADO"
                }
                """;
        String id = patchId.toString();
        when(service.patchServico(any(), any())).thenReturn(patchResponseDTO);

        mvc.perform(
                MockMvcRequestBuilders
                        .patch("/servico/%s".formatted(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.titulo").value(patchResponseDTO.titulo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.descricao").value(patchResponseDTO.descricao()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preco").value(patchResponseDTO.preco()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoria").value(CategoriaServico.AULAS.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(patchResponseDTO.status().name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(patchResponseDTO.id().toString()));
    }

    @Test
    void deveFalharAoFazerPatchComServicoInexistente() throws Exception{
        String json = """
                {
                    "titulo": "titulo novo",
                    "descricao": "descricao nova",
                    "preco": 1.00,
                    "categoria": "AULAS",
                    "status": "PAUSADO"
                }
                """;
        String id = patchId.toString();
        when(service.patchServico(any(), any())).thenThrow(new ServiceNotExistsException("Serviço não existe"));

        mvc.perform(
                        MockMvcRequestBuilders
                                .patch("/servico/%s".formatted(id))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").isNotEmpty());
    }

    @Test
    void deveFalharAoFazerPatchComUsuarioNaoAutorizado() throws Exception{
        String json = """
                {
                    "titulo": "titulo novo",
                    "descricao": "descricao nova",
                    "preco": 1.00,
                    "categoria": "AULAS",
                    "status": "PAUSADO"
                }
                """;
        String id = patchId.toString();
        when(service.patchServico(any(), any())).thenThrow(new UnauthorizedServiceOperationException("Usuário não autorizado a alterar o serviço"));

        mvc.perform(
                        MockMvcRequestBuilders
                                .patch("/servico/%s".formatted(id))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                ).andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").isNotEmpty());
    }

    @Test
    void deveRetornarUmServicoPorIdComSucesso() throws Exception{
        String id = patchId.toString();


        when(service.getServicoById(any())).thenReturn(responseDTO);

        mvc.perform(
                MockMvcRequestBuilders
                        .get("/servico/%s".formatted(id))

        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.titulo").value(responseDTO.titulo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.descricao").value(responseDTO.descricao()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preco").value(responseDTO.preco()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoria").value(responseDTO.categoria().name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(responseDTO.status().name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.idAnunciante").value(responseDTO.idAnunciante().toString()));

    }

    @Test
    void deveDarServiceNotFoundExceptionQuandoNaoExisteServico() throws Exception{
        String id = patchId.toString();

        when(service.getServicoById(any())).thenThrow(new ServiceNotExistsException("Serviço não existe"));

        mvc.perform(
                MockMvcRequestBuilders
                        .get("/servico/%s".formatted(id))
        ).andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Serviço não existe"));
    }

    @Test
    void deveFazerUmaBuscaComFiltroComSucesso() throws Exception{
        String tituloLike = "Serv";

        List<Servico> servicos = List.of(new Servico("Servico 1", "descricao", BigDecimal.TEN, CategoriaServico.AULAS, Status.INDISPONIVEL, usuarioCriador),
                new Servico("Servico 2", "descricao", BigDecimal.TEN, CategoriaServico.AULAS, Status.INDISPONIVEL, usuarioCriador),
                new Servico("Alien", "descricao", BigDecimal.TEN, CategoriaServico.AULAS, Status.INDISPONIVEL, usuarioCriador)
                );

        List<Servico> servicoFiltered = servicos.stream()
                .filter(s -> s.getTitulo().startsWith(tituloLike))
                        .toList();

        Page<ServicoResponseDTO> response = new PageImpl<ServicoResponseDTO>(servicoFiltered.stream()
                .map(s -> new ServicoResponseDTO(
                        UUID.randomUUID(),
                        s.getTitulo(),
                        s.getDescricao(),
                        s.getPreco(),
                        s.getCategoria(),
                        s.getStatus(),
                        UUID.randomUUID()
                )).toList()
        );

        when(service.getServicoByQuery(any(), any(), any(), any(), any(), anyInt())).thenReturn(response);

        mvc.perform(
                MockMvcRequestBuilders
                        .get("/servico")
                        .param("titulo", tituloLike)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].titulo").value(response.getContent().getFirst().titulo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].titulo").value(response.getContent().getLast().titulo()));
    }



}
