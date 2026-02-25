package com.Ypisds.eservices.controller;

import com.Ypisds.eservices.dto.request.ServicoPatchRequestDTO;
import com.Ypisds.eservices.dto.request.ServicoRequestDTO;
import com.Ypisds.eservices.dto.response.ServicoResponseDTO;
import com.Ypisds.eservices.enums.CategoriaServico;
import com.Ypisds.eservices.enums.Status;
import com.Ypisds.eservices.service.ServicoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("servico")
@AllArgsConstructor
public class ServicoController {

    private final ServicoService service;

    @PostMapping
    public ResponseEntity<ServicoResponseDTO> createServico(@Valid @RequestBody ServicoRequestDTO dto){
        ServicoResponseDTO response = service.createServico(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("{id}")
    public ResponseEntity<ServicoResponseDTO> patchServico(@PathVariable String id, @Valid @RequestBody ServicoPatchRequestDTO dto){
        ServicoResponseDTO response = service.patchServico(UUID.fromString(id), dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<ServicoResponseDTO> getServicoById(@PathVariable String id){
        ServicoResponseDTO response = service.getServicoById(UUID.fromString(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ServicoResponseDTO>> getAllServiceByQueryParams(@RequestParam(required = false) String titulo,
                                                                               @RequestParam(required = false) BigDecimal preco,
                                                                               @RequestParam(required = false) CategoriaServico categoria,
                                                                               @RequestParam(required = false) Status status,
                                                                               @RequestParam(required = false) Integer ano,
                                                                               @RequestParam(required = false) Integer pageNumber
                                                                                )
    {
        Page<ServicoResponseDTO> response = service.getServicoByQuery(
                titulo,
                preco,
                categoria,
                status,
                ano,
                pageNumber != null ? pageNumber : 0
        );

        return ResponseEntity.ok(response);
    }
}
