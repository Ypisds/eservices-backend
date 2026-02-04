package com.Ypisds.eservices.infra.exception;

import com.Ypisds.eservices.dto.response.ArgumentErrorResponseDTO;
import com.Ypisds.eservices.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class UsuarioExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ArgumentErrorResponseDTO> handleUserAlreadyExistsException(UserAlreadyExistsException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ArgumentErrorResponseDTO("Credentials already used", Collections.emptyList()));
    }


}
