package com.Ypisds.eservices.service;

import com.Ypisds.eservices.dto.request.ServicoPatchRequestDTO;
import com.Ypisds.eservices.dto.request.ServicoRequestDTO;
import com.Ypisds.eservices.dto.response.ServicoResponseDTO;
import com.Ypisds.eservices.enums.CategoriaServico;
import com.Ypisds.eservices.enums.Status;
import com.Ypisds.eservices.enums.UsuarioRoles;
import com.Ypisds.eservices.exception.SameServiceExistsException;
import com.Ypisds.eservices.exception.ServiceNotExistsException;
import com.Ypisds.eservices.exception.UnauthorizedServiceOperationException;
import com.Ypisds.eservices.mapper.ServicoMapper;
import com.Ypisds.eservices.model.Servico;
import com.Ypisds.eservices.model.Usuario;
import com.Ypisds.eservices.repository.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ServicoServiceTest {

    @InjectMocks
    ServicoService servicoService;

    @Mock
    ServicoRepository servicoRepository;

    @Mock
    AuthenticationService authenticationService;

    @Mock
    ServicoMapper servicoMapperMockado;

    ServicoRequestDTO dtoRequest;
    ServicoPatchRequestDTO patchRequestDTO;
    Usuario usuarioCriador;
    final UUID idUsuario = UUID.randomUUID();
    final ServicoMapper servicoMapper = new ServicoMapper();

    @BeforeEach
    void setup(){
        dtoRequest = new ServicoRequestDTO("titulo", "um produto legal", BigDecimal.valueOf(100.20), CategoriaServico.OUTROS, Status.DISPONIVEL);
        usuarioCriador = new Usuario("email@gmail.com", "usuario", "senha");
        usuarioCriador.setId(idUsuario);
        usuarioCriador.setRole(Set.of(UsuarioRoles.USER));
        patchRequestDTO = new ServicoPatchRequestDTO("titulo novo", "descricao nova", BigDecimal.TEN, CategoriaServico.AULAS, Status.PAUSADO);

    }

    @Test
    void deveCriarServicoComSucesso(){
        Servico servico = servicoMapper.toEntity(dtoRequest);
        servico.setId(UUID.randomUUID());
        servico.setAnunciante(usuarioCriador);

        when(authenticationService.getUsuarioAuthenticated()).thenReturn(usuarioCriador);
        when(servicoRepository.findByAnuncianteAndTitulo(usuarioCriador, dtoRequest.titulo())).thenReturn(Optional.empty());
        when(servicoMapperMockado.toEntity(dtoRequest)).thenReturn(servicoMapper.toEntity(dtoRequest));
        when(servicoRepository.save(any(Servico.class))).thenReturn(servico);
        when(servicoMapperMockado.toResponseDTO(any(Servico.class))).thenReturn(servicoMapper.toResponseDTO(servico));

        ServicoResponseDTO response = servicoService.createServico(dtoRequest);

        assertEquals(servico.getId(), response.id());
        assertEquals(dtoRequest.titulo(), response.titulo());
        assertEquals(dtoRequest.descricao(), response.descricao());
        assertEquals(dtoRequest.preco(), response.preco());
        assertEquals(dtoRequest.status(), response.status());
        assertEquals(dtoRequest.categoria(), response.categoria());
        assertEquals(idUsuario, response.idAnunciante());

    }

    @Test
    void deveFalharAoCriarServicoCasoExistaUmComMesmoTituloParaOMesmoUsuario(){

        when(authenticationService.getUsuarioAuthenticated()).thenReturn(usuarioCriador);
        when(servicoRepository.findByAnuncianteAndTitulo(any(), any())).thenReturn(Optional.of(new Servico()));

        String message = assertThrows(SameServiceExistsException.class, ()->{
            servicoService.createServico(dtoRequest);
            fail("Deveria ter falhado");
        }).getMessage();

        assertEquals("Serviço igual já existente para o usuário", message);
        verify(servicoMapperMockado, never()).toEntity(any());
        verify(servicoMapperMockado, never()).toResponseDTO(any());
        verify(servicoRepository, never()).save(any());
    }

    @Test
    void deveAlterarOServicoComSucesso(){
        Servico servicoRetornado = servicoMapper.toEntity(dtoRequest);
        UUID idServico = UUID.randomUUID();
        servicoRetornado.setId(idServico);
        servicoRetornado.setAnunciante(usuarioCriador);
        Servico servicoPatched = servicoMapper.toEntity(servicoRetornado, patchRequestDTO);

        when(authenticationService.getUsuarioAuthenticated()).thenReturn(usuarioCriador);
        when(servicoRepository.findById(any(UUID.class))).thenReturn(Optional.of(servicoRetornado));
        when(servicoMapperMockado.toEntity(servicoRetornado, patchRequestDTO)).thenReturn(servicoMapper.toEntity(servicoRetornado, patchRequestDTO));
        when(servicoRepository.save(any(Servico.class))).thenReturn(servicoPatched);
        when(servicoMapperMockado.toResponseDTO(servicoPatched)).thenReturn(servicoMapper.toResponseDTO(servicoPatched));

        ServicoResponseDTO response = servicoService.patchServico(idServico, patchRequestDTO);

        assertTrue(patchRequestDTO.titulo() == null || patchRequestDTO.titulo().isBlank() ? !response.titulo().isBlank() : response.titulo().equals(patchRequestDTO.titulo()));
        assertTrue(patchRequestDTO.descricao() == null || patchRequestDTO.descricao().isBlank() ? !response.descricao().isBlank() : response.descricao().equals(patchRequestDTO.descricao()));
        assertTrue(patchRequestDTO.preco() == null ? (response.preco() != null && response.preco().compareTo(BigDecimal.ZERO) > 0) : response.preco().compareTo(patchRequestDTO.preco()) == 0);
        assertTrue(patchRequestDTO.status() == null ? response.status() != null : response.status() == patchRequestDTO.status());
        assertTrue(patchRequestDTO.categoria() == null ? response.categoria() != null : response.categoria().equals(patchRequestDTO.categoria()));

    }

    @Test
    void deveFalharAoAlterarOServicoQuandoEleNaoExiste(){

        when(servicoRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        String message = assertThrows(ServiceNotExistsException.class, () -> {
            servicoService.patchServico(UUID.randomUUID(), patchRequestDTO);
            fail("Deveria ter falhado");
        }).getMessage();

        assertEquals("Serviço não existe", message);
        verify(authenticationService, never()).getUsuarioAuthenticated();
        verify(servicoMapperMockado, never()).toEntity(any(), any());
        verify(servicoMapperMockado, never()).toResponseDTO(any());
        verify(servicoRepository, never()).save(any());

    }

    @Test
    void deveFalharAAlteracaoQuandoOUsuarioAutenticadoNaoTemAcesso(){
        Servico servico = new Servico();
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        servico.setAnunciante(new Usuario());

        when(servicoRepository.findById(any(UUID.class))).thenReturn(Optional.of(servico));
        when(authenticationService.getUsuarioAuthenticated()).thenReturn(usuarioCriador);

        String message = assertThrows(UnauthorizedServiceOperationException.class, ()->{
            servicoService.patchServico(UUID.randomUUID(), patchRequestDTO);
            fail("Deveria ter falhado");
        }).getMessage();

        assertEquals("Usuário não autorizado a alterar o serviço", message);
        verify(servicoMapperMockado, never()).toResponseDTO(any());
        verify(servicoMapperMockado, never()).toEntity(any(), any());
        verify(servicoRepository, never()).save(any());
    }

    @Test
    void deveRetornarUmServicoComSucessoComOGetById(){
        UUID servicoId = UUID.randomUUID();
        Servico servico = new Servico("titulo", "descricao", BigDecimal.ONE,
                CategoriaServico.AULAS, Status.DISPONIVEL, usuarioCriador);
        servico.setId(servicoId);

        when(servicoRepository.findById(any())).thenReturn(Optional.of(servico));
        when(servicoMapperMockado.toResponseDTO(servico)).thenReturn(servicoMapper.toResponseDTO(servico));

        ServicoResponseDTO response = servicoService.getServicoById(servicoId);

        assertEquals(servicoId, response.id());
        assertEquals(servico.getTitulo(), response.titulo());
        assertEquals(servico.getDescricao(), response.descricao());
        assertEquals(servico.getPreco(), response.preco());
        assertEquals(servico.getStatus(), response.status());
        assertEquals(servico.getCategoria(), response.categoria());
        assertEquals(servico.getAnunciante().getId(), response.idAnunciante());
    }

    @Test
    void deveDarServiceNotExistsExceptionQuandoNaoExisteServicoComOIDPassado(){
        UUID servicoId = UUID.randomUUID();
        Servico servico = new Servico("titulo", "descricao", BigDecimal.ONE,
                CategoriaServico.AULAS, Status.DISPONIVEL, usuarioCriador);
        servico.setId(servicoId);

        when(servicoRepository.findById(any())).thenReturn(Optional.empty());

        String message = assertThrows(ServiceNotExistsException.class, ()->{
            servicoService.getServicoById(servicoId);
            fail("Deveria ter falhado");
        }).getMessage();

        assertEquals("Serviço não existe", message);
        verify(servicoMapperMockado, never()).toResponseDTO(any());

    }

    @Test
    void deveRetornarOsServiçosComTitulosCorretos(){

    }

}

