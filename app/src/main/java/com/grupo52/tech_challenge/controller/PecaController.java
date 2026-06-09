package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.dto.request.CreatePecaRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreatePecaResponseDTO;
import com.grupo52.tech_challenge.dto.response.FindPecaResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreatePecaGateway;
import com.grupo52.tech_challenge.gateway.FindPecaGateway;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/pecas")
@Validated
public class PecaController {

    @Autowired
    private CreatePecaGateway createPecaGateway;

    @Autowired
    private FindPecaGateway findPecaGateway;

    @PostMapping
    public ResponseEntity<CreatePecaResponseDTO> createPeca(@RequestBody @Valid CreatePecaRequestDTO createPecaRequestDTO) throws GatewayException {
        Peca peca = createPecaGateway.execute(createPecaRequestDTO.toDomain());

        return ResponseEntity.created(buildLocationUri(peca)).body(CreatePecaResponseDTO.fromDomain(peca));
    }

    @GetMapping("/{pecaId}")
    public ResponseEntity<FindPecaResponseDTO> findPeca(@PathVariable Long pecaId) throws GatewayException {
        Peca peca = findPecaGateway.execute(pecaId);

        return ResponseEntity.ok().body(FindPecaResponseDTO.fromDomain(peca));
    }

    private URI buildLocationUri(Peca peca) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(peca.getId())
                .toUri();
    }
}
