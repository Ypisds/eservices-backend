package com.Ypisds.eservices.mapper;

import com.Ypisds.eservices.dto.request.ServicoPatchRequestDTO;
import com.Ypisds.eservices.dto.request.ServicoRequestDTO;
import com.Ypisds.eservices.dto.response.ServicoResponseDTO;
import com.Ypisds.eservices.model.Servico;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ServicoMapper {

    public Servico toEntity(ServicoRequestDTO dto){
        Servico servico = new Servico();
        servico.setTitulo(dto.titulo());
        servico.setDescricao(dto.descricao());
        servico.setPreco(dto.preco());
        servico.setCategoria(dto.categoria());
        servico.setStatus(dto.status());
        return servico;
    }

    public Servico toEntity(Servico servico, ServicoPatchRequestDTO dto){
        if(dto.titulo() != null && !dto.titulo().isBlank()) servico.setTitulo(dto.titulo());
        if(dto.descricao() != null && !dto.descricao().isBlank()) servico.setDescricao(dto.descricao());
        if(dto.preco() != null && dto.preco().compareTo(BigDecimal.ZERO) > 0) servico.setPreco(dto.preco());
        if(dto.categoria() != null) servico.setCategoria(dto.categoria());
        if(dto.status() != null) servico.setStatus(dto.status());
        return servico;
    }

    public ServicoResponseDTO toResponseDTO(Servico servico){
        return new ServicoResponseDTO(servico.getId(),
                servico.getTitulo(),
                servico.getDescricao(),
                servico.getPreco(),
                servico.getCategoria(),
                servico.getStatus(),
                servico.getAnunciante().getId());
    }
}
