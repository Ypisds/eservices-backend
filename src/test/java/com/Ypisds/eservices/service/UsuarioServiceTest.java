package com.Ypisds.eservices.service;

import com.Ypisds.eservices.dto.request.UsuarioRequestDTO;
import com.Ypisds.eservices.dto.response.UsuarioResponseDTO;
import com.Ypisds.eservices.enums.UsuarioRoles;
import com.Ypisds.eservices.exception.UserAlreadyExistsException;
import com.Ypisds.eservices.mapper.UsuarioMapper;
import com.Ypisds.eservices.model.Usuario;
import com.Ypisds.eservices.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @InjectMocks
    UsuarioService usuarioService;

    @Mock
    PasswordEncoder encoder;

    @Mock
    UsuarioMapper mapper;

    @Mock
    UsuarioRepository repository;

    @Mock
    LoginService loginService;

    UsuarioMapper mapperSetup;
    Usuario usuarioBase;
    UsuarioRequestDTO dtoRequest;
    PasswordEncoder encoderSetup;



    @BeforeEach
    void setup(){
        mapperSetup = new UsuarioMapper();
        encoderSetup = new BCryptPasswordEncoder();
        dtoRequest = new UsuarioRequestDTO("Usuario", "senha", "email@gmail.com");
        usuarioBase = mapperSetup.toEntity(dtoRequest);
    }

    @Test
    void deveCriarUmUsuarioComSucesso(){
        Usuario usuarioRetornado = usuarioBase;
        usuarioRetornado.setId(UUID.randomUUID());
        usuarioRetornado.setPassword(encoder.encode(dtoRequest.password()));
        usuarioRetornado.setRole(Set.of(UsuarioRoles.USER));


        when(repository.findByLoginOrEmail(dtoRequest.login(), dtoRequest.email())).thenReturn(Optional.empty());
        when(mapper.toEntity(dtoRequest)).thenReturn(usuarioBase);
        when(encoder.encode(any(String.class))).thenReturn(encoderSetup.encode(dtoRequest.password()));
        when(repository.save(any(Usuario.class))).thenReturn(usuarioRetornado);
        when(mapper.entityToResponseDTO(usuarioRetornado)).thenReturn(mapperSetup.entityToResponseDTO(usuarioRetornado));

        UsuarioResponseDTO resposta = usuarioService.createUsuario(dtoRequest);

        assertEquals(resposta.id(), usuarioRetornado.getId());
        assertEquals(resposta.email(), usuarioRetornado.getEmail());
        assertEquals(resposta.login(), usuarioRetornado.getLogin());
        assertEquals(resposta.roles(), usuarioRetornado.getRole());

    }

    @Test
    void deveDarErroQuandoExistirUsuarioComLoginOuEmailExistentes(){
        when(repository.findByLoginOrEmail(dtoRequest.login(), dtoRequest.email())).thenReturn(Optional.of(new Usuario()));

        String message = assertThrows(UserAlreadyExistsException.class, ()->{
            usuarioService.createUsuario(dtoRequest);
            fail("Deveria ter dado uma exception");
        }).getMessage();

        assertEquals("Usuário com essas credenciais já existe", message);
    }


}
