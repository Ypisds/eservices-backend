package com.Ypisds.eservices.service;

import com.Ypisds.eservices.dto.request.ServicoPatchRequestDTO;
import com.Ypisds.eservices.dto.request.ServicoRequestDTO;
import com.Ypisds.eservices.dto.response.ServicoResponseDTO;
import com.Ypisds.eservices.enums.CategoriaServico;
import com.Ypisds.eservices.enums.Status;
import com.Ypisds.eservices.exception.SameServiceExistsException;
import com.Ypisds.eservices.exception.ServiceNotExistsException;
import com.Ypisds.eservices.exception.UnauthorizedServiceOperationException;
import com.Ypisds.eservices.mapper.ServicoMapper;
import com.Ypisds.eservices.model.Servico;
import com.Ypisds.eservices.model.Usuario;
import com.Ypisds.eservices.repository.ServicoRepository;
import com.Ypisds.eservices.repository.specification.ServiceSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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

    public ServicoResponseDTO getServicoById(UUID id){
        Optional<Servico> servicoOptional = repository.findById(id);
        if(servicoOptional.isEmpty()) throw new ServiceNotExistsException("Serviço não existe");

        return mapper.toResponseDTO(servicoOptional.get());
    }

    public Page<ServicoResponseDTO> getServicoByQuery(String titulo, BigDecimal preco, CategoriaServico categoria, Status status, Integer ano, int pageNumber){
        if(pageNumber <= 0 ) throw new RuntimeException();

        Specification<Servico> specs = Specification.unrestricted();

        if(titulo != null){
            specs = specs.and(ServiceSpecification.hasTituloLike(titulo));
        }
        if(preco != null){
            specs = specs.and(ServiceSpecification.precoLessThanOrEqualTo(preco));
        }
        if(status != null){
            specs = specs.and(ServiceSpecification.hasStatusEquals(status));
        }
        if(ano != null){
            specs = specs.and(ServiceSpecification.createdInThisAno(ano));
        }
        if(categoria != null){
            specs = specs.and(ServiceSpecification.hasThisCategoria(categoria));
        }


        Page<ServicoResponseDTO> servicos = repository.findBy(specs, q-> q.as(Servico.class)
                .project("categoria", "anunciante")
                .page(PageRequest.of(pageNumber, 10))
                .map(f -> new ServicoResponseDTO(
                        f.getId(),
                        f.getTitulo(),
                        f.getDescricao(),
                        f.getPreco(),
                        f.getCategoria(),
                        f.getStatus(),
                        f.getAnunciante().getId()
                )));

        return servicos;
    }

}
