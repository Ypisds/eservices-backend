package com.Ypisds.eservices.service;

import com.Ypisds.eservices.dto.request.UsuarioLoginDTO;
import com.Ypisds.eservices.dto.request.UsuarioRequestDTO;
import com.Ypisds.eservices.exception.BadArgumentException;
import com.Ypisds.eservices.exception.UserAlreadyExistsException;
import com.Ypisds.eservices.model.Usuario;
import com.Ypisds.eservices.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {


    private final UsuarioRepository repository;
    private final AuthenticationManager manager;
    private final TokenService tokenService;
    private final PasswordEncoder encoder;

    public void createUsuario(UsuarioRequestDTO dto){
        Optional<Usuario> usuario = repository.findByLoginOrEmail(dto.login(), dto.email());
        if(usuario.isPresent()) throw new UserAlreadyExistsException("Usuário com essas credenciais já existe");
        Usuario user = new Usuario(dto);
        user.setPassword(encoder.encode(dto.password()));
        repository.save(user);
    }

    public String login(UsuarioLoginDTO dto){
        var userAuth = new UsernamePasswordAuthenticationToken(dto.login(), dto.password());
        var auth = manager.authenticate(userAuth);
        var usuario = (Usuario) auth.getPrincipal();
        return tokenService.createToken(usuario.getUsername());
    }

    private UUID uuidValidator(String id){
        try{
            return UUID.fromString(id);
        }catch(IllegalArgumentException e){
            throw new BadArgumentException("Bad UUID");
        }
    }
}
