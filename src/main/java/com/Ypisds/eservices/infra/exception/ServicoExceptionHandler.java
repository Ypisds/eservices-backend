package com.Ypisds.eservices.infra.exception;

import com.Ypisds.eservices.controller.ServicoController;
import com.Ypisds.eservices.dto.response.ArgumentErrorResponseDTO;
import com.Ypisds.eservices.exception.SameServiceExistsException;
import com.Ypisds.eservices.exception.ServiceNotExistsException;
import com.Ypisds.eservices.exception.UnauthorizedServiceOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@ControllerAdvice(assignableTypes = ServicoController.class)
public class ServicoExceptionHandler {

    @ExceptionHandler(SameServiceExistsException.class)
    public ResponseEntity<ArgumentErrorResponseDTO> handleSameServiceExistsException(SameServiceExistsException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ArgumentErrorResponseDTO(e.getMessage(), Collections.emptyList()));
    }

    @ExceptionHandler(ServiceNotExistsException.class)
    public ResponseEntity<ArgumentErrorResponseDTO> handleServiceNotExistsException(ServiceNotExistsException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArgumentErrorResponseDTO(e.getMessage(), Collections.emptyList()));
    }

    @ExceptionHandler(UnauthorizedServiceOperationException.class)
    public ResponseEntity<ArgumentErrorResponseDTO> handleUnauthorizedServiceOperationException(UnauthorizedServiceOperationException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArgumentErrorResponseDTO(e.getMessage(), Collections.emptyList()));
    }
}
