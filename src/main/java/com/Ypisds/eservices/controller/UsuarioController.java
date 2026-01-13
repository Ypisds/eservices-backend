package com.Ypisds.eservices.controller;

import com.Ypisds.eservices.dto.request.UsuarioLoginDTO;
import com.Ypisds.eservices.dto.request.UsuarioRequestDTO;
import com.Ypisds.eservices.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public void createUsuario(@Valid @RequestBody UsuarioRequestDTO dto){
        service.createUsuario(dto);
    }

    @PostMapping("login")
    @ResponseStatus(HttpStatus.OK)
    public String login(@Valid @RequestBody UsuarioLoginDTO dto){
        return "Token: " + service.login(dto);
    }

}
