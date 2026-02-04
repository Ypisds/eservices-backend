package com.Ypisds.eservices.service;

import com.Ypisds.eservices.dto.request.ServicoPatchRequestDTO;
import com.Ypisds.eservices.dto.request.ServicoRequestDTO;
import com.Ypisds.eservices.dto.response.ServicoResponseDTO;
import com.Ypisds.eservices.exception.SameServiceExistsException;
import com.Ypisds.eservices.exception.ServiceNotExistsException;
import com.Ypisds.eservices.exception.UnauthorizedServiceOperationException;
import com.Ypisds.eservices.mapper.ServicoMapper;
import com.Ypisds.eservices.model.Servico;
import com.Ypisds.eservices.model.Usuario;
import com.Ypisds.eservices.repository.ServicoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ServicoService {

    private final ServicoRepository repository;
    private final ServicoMapper mapper;
    private final AuthenticationService authenticationService;

    public ServicoResponseDTO createServico(ServicoRequestDTO dto){
        Usuario usuarioAutenticado = authenticationService.getUsuarioAuthenticated();

        Optional<Servico> servicoOptional = repository.findByAnuncianteAndTitulo(usuarioAutenticado, dto.titulo());
        if(servicoOptional.isPresent()) throw new SameServiceExistsException("Serviço igual já existente para o usuário");

        Servico servico = mapper.toEntity(dto);
        servico.setAnunciante(usuarioAutenticado);
        servico = repository.save(servico);

        return mapper.toResponseDTO(servico);
    }

    public ServicoResponseDTO patchServico(UUID id, ServicoPatchRequestDTO dto){
        Optional<Servico> servicoOptional = repository.findById(id);
        if(servicoOptional.isEmpty()) throw new ServiceNotExistsException("Serviço não existe");

        Servico servico = servicoOptional.get();
        Usuario usuario = authenticationService.getUsuarioAuthenticated();
        if(!servico.getAnunciante().equals(usuario)) throw new UnauthorizedServiceOperationException("Usuário não autorizado a alterar o serviço");

        Servico servicoPatched = mapper.toEntity(servico, dto);
        servicoPatched = repository.save(servicoPatched);

        return mapper.toResponseDTO(servicoPatched);
    }

}
