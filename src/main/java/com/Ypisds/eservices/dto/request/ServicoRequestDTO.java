package com.Ypisds.eservices.dto.request;

import com.Ypisds.eservices.enums.CategoriaServico;
import com.Ypisds.eservices.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Set;

public record ServicoRequestDTO(@NotBlank(message = "titulo é obrigatório") String titulo,
                                @NotBlank(message = "descricao é obrigatório") String descricao,
                                @NotNull(message = "preco é obrigatório") @Positive(message = "preço não pode ser negativo") BigDecimal preco,
                                @NotEmpty(message = "categoria é obrigatória") Set<CategoriaServico> categorias,
                                @NotNull(message = "status é obrigatório") Status status) {
}
