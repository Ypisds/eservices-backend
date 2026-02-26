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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

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
@ExtendWith(RestDocumentationExtension.class)
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
    void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation){
        requestDTO = new ServicoRequestDTO("titulo", "descricao", BigDecimal.TEN, CategoriaServico.OUTROS, Status.DISPONIVEL);
        patchRequestDTO = new ServicoPatchRequestDTO("titulo novo", "descricao nova", BigDecimal.ONE, CategoriaServico.AULAS, Status.PAUSADO);
        responseDTO = new ServicoResponseDTO(UUID.randomUUID(), "titulo", "descricao", BigDecimal.TEN, CategoriaServico.OUTROS, Status.DISPONIVEL, usuarioId);
        usuarioCriador = new Usuario("email@gmail.com", "usuario", "senha");
        usuarioCriador.setId(usuarioId);
        patchResponseDTO = new ServicoResponseDTO(patchId, "titulo novo", "descricao nova", BigDecimal.ONE, CategoriaServico.AULAS, Status.PAUSADO, usuarioId);

        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
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
                        .header("Authorization", "Bearer eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJlc2VydmljZXMtYXBpIiwic3ViIjoidGhpZ2FzIiwiZXhwIjoxNzcyMDcyMjU2fQ.0bJS84VqnHl-kj9txVPEUuUbRbiAgzNglmAQHHZjWx6ewEgJWE-DmhIVT-jRcM17")
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.titulo").value(responseDTO.titulo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.descricao").value(responseDTO.descricao()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preco").value(responseDTO.preco()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoria").value(responseDTO.categoria().name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(responseDTO.status().name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.idAnunciante").value(responseDTO.idAnunciante().toString()))
                .andDo(document("servico/create-servico",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token JWT obtido no login")
                        ),
                        requestFields(
                                fieldWithPath("titulo").description("Titulo do serviço"),
                                fieldWithPath("descricao").description("Descrição do serviço"),
                                fieldWithPath("preco").description("Preço do serviço"),
                                fieldWithPath("categoria").description("Categoria do serviço"),
                                fieldWithPath("status").description("Status do serviço")
                        ),
                        responseFields(
                                fieldWithPath("id").description("ID do serviço"),
                                fieldWithPath("titulo").description("Titulo do serviço"),
                                fieldWithPath("descricao").description("Descrição do serviço"),
                                fieldWithPath("preco").description("Preço do serviço"),
                                fieldWithPath("categoria").description("Categoria do serviço"),
                                fieldWithPath("status").description("Status do serviço"),
                                fieldWithPath("idAnunciante").description("ID do usuário criador do serviço")
                        )
                        ));

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
                        .patch("/servico/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJlc2VydmljZXMtYXBpIiwic3ViIjoidGhpZ2FzIiwiZXhwIjoxNzcyMDcyMjU2fQ.0bJS84VqnHl-kj9txVPEUuUbRbiAgzNglmAQHHZjWx6ewEgJWE-DmhIVT-jRcM17")
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.titulo").value(patchResponseDTO.titulo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.descricao").value(patchResponseDTO.descricao()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preco").value(patchResponseDTO.preco()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoria").value(CategoriaServico.AULAS.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(patchResponseDTO.status().name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(patchResponseDTO.id().toString()))
                .andDo(document("servico/patch-servico",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token JWT obtido no login")
                        ),
                        pathParameters(
                                parameterWithName("id").description("Id do serviço")
                        ),
                        requestFields(
                                fieldWithPath("titulo").description("Titulo do serviço").optional(),
                                fieldWithPath("descricao").description("Descrição do serviço").optional(),
                                fieldWithPath("preco").description("Preço do serviço").optional(),
                                fieldWithPath("categoria").description("Categoria do serviço").optional(),
                                fieldWithPath("status").description("Status do serviço").optional()
                        ),
                        responseFields(
                                fieldWithPath("id").description("ID do serviço"),
                                fieldWithPath("titulo").description("Titulo do serviço"),
                                fieldWithPath("descricao").description("Descrição do serviço"),
                                fieldWithPath("preco").description("Preço do serviço"),
                                fieldWithPath("categoria").description("Categoria do serviço"),
                                fieldWithPath("status").description("Status do serviço"),
                                fieldWithPath("idAnunciante").description("ID do usuário criador do serviço")
                        )));
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
                        .get("/servico/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJlc2VydmljZXMtYXBpIiwic3ViIjoidGhpZ2FzIiwiZXhwIjoxNzcyMDcyMjU2fQ.0bJS84VqnHl-kj9txVPEUuUbRbiAgzNglmAQHHZjWx6ewEgJWE-DmhIVT-jRcM17")

        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.titulo").value(responseDTO.titulo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.descricao").value(responseDTO.descricao()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preco").value(responseDTO.preco()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categoria").value(responseDTO.categoria().name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(responseDTO.status().name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.idAnunciante").value(responseDTO.idAnunciante().toString()))
                .andDo(document("servico/get-servico-por-id",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token JWT obtido no login")
                        ),
                        pathParameters(
                                parameterWithName("id").description("Id do serviço")
                        ),
                        responseFields(
                                fieldWithPath("id").description("ID do serviço"),
                                fieldWithPath("titulo").description("Titulo do serviço"),
                                fieldWithPath("descricao").description("Descrição do serviço"),
                                fieldWithPath("preco").description("Preço do serviço"),
                                fieldWithPath("categoria").description("Categoria do serviço"),
                                fieldWithPath("status").description("Status do serviço"),
                                fieldWithPath("idAnunciante").description("ID do usuário criador do serviço")
                        )
                        ));

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
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJlc2VydmljZXMtYXBpIiwic3ViIjoidGhpZ2FzIiwiZXhwIjoxNzcyMDcyMjU2fQ.0bJS84VqnHl-kj9txVPEUuUbRbiAgzNglmAQHHZjWx6ewEgJWE-DmhIVT-jRcM17")

        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].titulo").value(response.getContent().getFirst().titulo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].titulo").value(response.getContent().getLast().titulo()))
                .andDo(document("servico/get-servico-com-query-params",
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer token JWT obtido no login")
                        ),
                        queryParameters(
                                parameterWithName("titulo").description("Título da obra").optional(),
                                parameterWithName("preco").description("Preço máximo do produto").optional(),
                                parameterWithName("categoria").description("Categoria do produto").optional(),
                                parameterWithName("status").description("Status de disponibilidade do produto").optional(),
                                parameterWithName("ano").description("Ano de lançamento de um produto").optional(),
                                parameterWithName("pageNumber").description("Inteiro para especificar uma página").optional()
                        ),
                        responseFields(
                                fieldWithPath("content[]").description("Lista de serviços retornados"),
                                fieldWithPath("content[].id").description("ID do serviço"),
                                fieldWithPath("content[].titulo").description("Titulo do serviço"),
                                fieldWithPath("content[].descricao").description("Descrição do serviço"),
                                fieldWithPath("content[].preco").description("Preço do serviço"),
                                fieldWithPath("content[].categoria").description("Categoria do serviço"),
                                fieldWithPath("content[].status").description("Status do serviço"),
                                fieldWithPath("content[].idAnunciante").description("ID do usuário criador do serviço"),
                                fieldWithPath("pageable").description("Informações sobre a paginação"),
                                fieldWithPath("last").description("Indica se é a última página"),
                                fieldWithPath("totalPages").description("Total de páginas disponíveis"),
                                fieldWithPath("totalElements").description("Total de elementos no banco"),
                                fieldWithPath("size").description("Quantidade de elementos por página"),
                                fieldWithPath("number").description("Número da página atual"),
                                subsectionWithPath("sort").description("Informações sobre a ordenação"),
                                fieldWithPath("first").description("Indica se é a primeira página"),
                                fieldWithPath("numberOfElements").description("Número de elementos na página atual"),
                                fieldWithPath("empty").description("Indica se a página está vazia")
                        )
                        ));
    }



}
