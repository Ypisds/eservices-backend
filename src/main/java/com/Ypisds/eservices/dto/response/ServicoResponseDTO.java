package com.Ypisds.eservices.dto.response;

import com.Ypisds.eservices.enums.CategoriaServico;
import com.Ypisds.eservices.enums.Status;

import java.math.BigDecimal;
import java.util.UUID;

public record ServicoResponseDTO(UUID id,
                                 String titulo,
                                 String descricao,
                                 BigDecimal preco,
                                 CategoriaServico categoria,
                                 Status status,
                                 UUID idAnunciante) {
}
