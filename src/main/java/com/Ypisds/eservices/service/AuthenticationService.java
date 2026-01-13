package com.Ypisds.eservices.service;

import com.Ypisds.eservices.model.Usuario;
import com.Ypisds.eservices.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private final UsuarioRepository repository;

    public Usuario getUsuarioAuthenticated(){
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null) return null;
        Usuario usuario = (Usuario) auth.getPrincipal();
        return usuario;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByLogin(username);
    }
}
