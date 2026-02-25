package com.Ypisds.eservices.mapper;

import com.Ypisds.eservices.dto.request.UsuarioRequestDTO;
import com.Ypisds.eservices.dto.response.UsuarioResponseDTO;
import com.Ypisds.eservices.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequestDTO dto){
        Usuario usuario = new Usuario();
        usuario.setLogin(dto.login());
        usuario.setEmail(dto.email());
        usuario.setName(dto.name());
        return usuario;
    }

    public UsuarioResponseDTO entityToResponseDTO(Usuario usuario){
        return new UsuarioResponseDTO(usuario.getId(),
                usuario.getLogin(),
                usuario.getName(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getCreatedDate());
    }
}
