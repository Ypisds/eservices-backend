package com.Ypisds.eservices.controller;

import com.Ypisds.eservices.dto.request.UsuarioLoginDTO;
import com.Ypisds.eservices.dto.request.UsuarioRequestDTO;
import com.Ypisds.eservices.dto.response.LoginResponseDTO;
import com.Ypisds.eservices.dto.response.UsuarioResponseDTO;
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
    public ResponseEntity<UsuarioResponseDTO> createUsuario(@Valid @RequestBody UsuarioRequestDTO dto){
        UsuarioResponseDTO response = service.createUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody UsuarioLoginDTO dto){
        LoginResponseDTO loginResponseDTO = service.login(dto);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDTO);
    }

}
