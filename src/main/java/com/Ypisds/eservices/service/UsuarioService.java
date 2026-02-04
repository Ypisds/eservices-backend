package com.Ypisds.eservices.service;

import com.Ypisds.eservices.dto.request.UsuarioLoginDTO;
import com.Ypisds.eservices.dto.request.UsuarioRequestDTO;
import com.Ypisds.eservices.dto.response.LoginResponseDTO;
import com.Ypisds.eservices.dto.response.UsuarioResponseDTO;
import com.Ypisds.eservices.enums.UsuarioRoles;
import com.Ypisds.eservices.exception.UserAlreadyExistsException;
import com.Ypisds.eservices.mapper.UsuarioMapper;
import com.Ypisds.eservices.model.Usuario;
import com.Ypisds.eservices.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final UsuarioMapper mapper;
    private final LoginService loginService;

    public UsuarioResponseDTO createUsuario(UsuarioRequestDTO dto){
        Optional<Usuario> usuario = repository.findByLoginOrEmail(dto.login(), dto.email());
        if(usuario.isPresent()) throw new UserAlreadyExistsException("Usuário com essas credenciais já existe");
        Usuario user = mapper.toEntity(dto);
        user.setPassword(encoder.encode(dto.password()));
        user.setRole(Set.of(UsuarioRoles.USER));
        user = repository.save(user);
        return mapper.entityToResponseDTO(user);
    }

    public LoginResponseDTO login(UsuarioLoginDTO dto){
        String token = loginService.authenticateUser(dto);
        return new LoginResponseDTO(token);
    }


}
