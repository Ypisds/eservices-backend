package com.Ypisds.eservices.dto.response;

import com.Ypisds.eservices.enums.UsuarioRoles;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UsuarioResponseDTO(
        UUID id,
        String login,
        String name,
        String email,
        Set<UsuarioRoles> roles,
        LocalDateTime createdAt
) {
}
