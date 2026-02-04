package com.Ypisds.eservices.service;

import com.Ypisds.eservices.dto.request.UsuarioLoginDTO;
import com.Ypisds.eservices.model.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginService {

    private final AuthenticationManager manager;
    private final TokenService tokenService;

    public String authenticateUser(UsuarioLoginDTO dto){
        var userAuth = new UsernamePasswordAuthenticationToken(dto.login(), dto.password());
        var auth = manager.authenticate(userAuth);
        var usuario = (Usuario) auth.getPrincipal();
        String token = tokenService.createToken(usuario.getUsername());
        return token;
    }
}
