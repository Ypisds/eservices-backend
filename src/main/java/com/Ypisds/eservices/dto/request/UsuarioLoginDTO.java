package com.Ypisds.eservices.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UsuarioLoginDTO(@NotBlank(message = "Login required") String login,
                              @NotBlank(message = "Password required") String password) {
}
