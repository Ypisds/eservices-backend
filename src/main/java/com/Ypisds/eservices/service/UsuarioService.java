package com.Ypisds.eservices.service;

import com.Ypisds.eservices.dto.request.UsuarioLoginDTO;
import com.Ypisds.eservices.dto.request.UsuarioRequestDTO;
import com.Ypisds.eservices.dto.response.LoginResponseDTO;
import com.Ypisds.eservices.dto.response.UsuarioResponseDTO;
import com.Ypisds.eservices.enums.UsuarioRoles;
import com.Ypisds.eservices.exception.BadArgumentException;
import com.Ypisds.eservices.exception.UserAlreadyExistsException;
import com.Ypisds.eservices.mapper.UsuarioMapper;
import com.Ypisds.eservices.model.Usuario;
import com.Ypisds.eservices.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {


    private final UsuarioRepository repository;
    private final AuthenticationManager manager;
    private final TokenService tokenService;
    private final PasswordEncoder encoder;
    private final UsuarioMapper mapper;

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
        var userAuth = new UsernamePasswordAuthenticationToken(dto.login(), dto.password());
        var auth = manager.authenticate(userAuth);
        var usuario = (Usuario) auth.getPrincipal();
        String token = tokenService.createToken(usuario.getUsername());
        return new LoginResponseDTO(token);
    }


}
