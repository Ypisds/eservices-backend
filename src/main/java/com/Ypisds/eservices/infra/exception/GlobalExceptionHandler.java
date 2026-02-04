package com.Ypisds.eservices.infra.exception;

import com.Ypisds.eservices.dto.response.ArgumentErrorResponseDTO;
import com.Ypisds.eservices.dto.response.ArgumentFieldErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ArgumentErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        List<ArgumentFieldErrorDTO> errors = e.getFieldErrors()
                .stream()
                .map(fe -> new ArgumentFieldErrorDTO(fe.getField(), fe.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest().body(new ArgumentErrorResponseDTO("Validation error", errors));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ArgumentErrorResponseDTO> handleAuthenticationException(AuthenticationException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArgumentErrorResponseDTO("Authentication error", Collections.emptyList()));
    }
}
