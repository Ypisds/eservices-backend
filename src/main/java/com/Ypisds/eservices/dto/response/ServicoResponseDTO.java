package com.Ypisds.eservices.dto.response;

import com.Ypisds.eservices.enums.CategoriaServico;
import com.Ypisds.eservices.enums.Status;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record ServicoResponseDTO(UUID id,
                                 String titulo,
                                 String descricao,
                                 BigDecimal preco,
                                 Set<CategoriaServico> categorias,
                                 Status status,
                                 UUID idAnunciante) {
}
