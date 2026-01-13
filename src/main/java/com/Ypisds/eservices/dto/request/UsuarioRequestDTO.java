package com.Ypisds.eservices.dto.request;


import jakarta.validation.constraints.NotBlank;

public record UsuarioRequestDTO(@NotBlank String login,
                                @NotBlank String password,
                                @NotBlank String email) {

}
