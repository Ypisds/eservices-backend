package com.Ypisds.eservices.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequestDTO(@NotBlank(message = "Login required") String login,
                                @NotBlank(message = "Password required") String password,
                                @NotBlank(message = "Name required") String name,
                                @NotBlank(message = "Email required") @Email(message = "Invalid email format") String email) {

}
