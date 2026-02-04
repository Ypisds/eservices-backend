package com.Ypisds.eservices.controller;

import com.Ypisds.eservices.config.TokenFilter;
import com.Ypisds.eservices.dto.request.UsuarioRequestDTO;
import com.Ypisds.eservices.dto.response.LoginResponseDTO;
import com.Ypisds.eservices.dto.response.UsuarioResponseDTO;
import com.Ypisds.eservices.enums.UsuarioRoles;
import com.Ypisds.eservices.infra.exception.GlobalExceptionHandler;
import com.Ypisds.eservices.repository.UsuarioRepository;
import com.Ypisds.eservices.service.TokenService;
import com.Ypisds.eservices.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(value = {UsuarioController.class, GlobalExceptionHandler.class},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = TokenFilter.class
        )
)

class UsuarioControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    UsuarioService service;


    UsuarioRequestDTO requestDTO;
    UsuarioResponseDTO responseDTO;

    @BeforeEach
    void setup(){
        requestDTO = new UsuarioRequestDTO("usuario", "senha", "email@gmail.com");
        responseDTO = new UsuarioResponseDTO(UUID.randomUUID(), "usuario", "email@gmail.com", Set.of(UsuarioRoles.USER), LocalDateTime.now());
    }

    @Test
    void deveRegistrarComSucesso() throws Exception{
        String json = """
                {
                    "login": "usuario",
                    "password": "senha",
                    "email": "email@gmail.com"
                }
                """;

        when(service.createUsuario(requestDTO)).thenReturn(responseDTO);

        mvc.perform(
                MockMvcRequestBuilders
                        .post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value(responseDTO.login()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(responseDTO.email()));


    }

    @ParameterizedTest
    @CsvSource("""
            usuario,,email@gmail.com
            ,senha,email@gmail.com
            usuario,senha,
            usuario,senha,emailinvalido
            """)
    void deveDarErroAoColocarInformaçõesNulasAoRegistrarOuComEmailInválido(String login, String email, String password) throws Exception{
        String json = """
                {
                    "login": %s,
                    "password": %s,
                    "email": %s
                }
                """.formatted(login == null ? null : "\"" + login + "\"",
                password == null ? null : "\"" + password + "\"",
                email == null ? null : "\"" + login + "\"");

        mvc.perform(
                MockMvcRequestBuilders
                        .post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation error"));
    }

    @Test
    void deveRealizarLoginComSucesso() throws Exception{
        when(service.login(any())).thenReturn(new LoginResponseDTO("asoidhuichb1230898xyuc98zihxjsj12e98hrb813nf09mxz0i9h90123"));
        String json = """
                {
                    "login": "thigas",
                    "password": "thigas123"
                }
                """;
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());


    }

    @ParameterizedTest
    @CsvSource("""
            ,senha
            usuario,
            """)
    void deveFalharOLoginComArgumentosNulos(String login, String password) throws Exception{
        String json = """
                {
                    "login": %s,
                    "password": %s
                }
                """.formatted(login == null ? null : "\"" + login + "\"",
                password == null ? null : "\"" + password + "\"");
        mvc.perform(
                MockMvcRequestBuilders
                        .post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Validation error"));
    }
}

