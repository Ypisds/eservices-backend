package com.Ypisds.eservices.dto.request;

import jakarta.validation.constraints.NotBlank;



public record UsuarioLoginDTO(@NotBlank String login,
                              @NotBlank String password) {
}
