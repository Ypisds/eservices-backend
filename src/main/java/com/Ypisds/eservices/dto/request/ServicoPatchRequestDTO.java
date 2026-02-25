package com.Ypisds.eservices.dto.request;

import com.Ypisds.eservices.enums.CategoriaServico;
import com.Ypisds.eservices.enums.Status;

import java.math.BigDecimal;
import java.util.Set;

public record ServicoPatchRequestDTO(String titulo, String descricao, BigDecimal preco, CategoriaServico categoria, Status status) {
}
