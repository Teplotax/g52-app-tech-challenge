package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.dto.request.CreatePecaRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreatePecaResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreatePecaGateway;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/pecas")
@Validated
public class PecaController {

    @Autowired
    private CreatePecaGateway createPecaGateway;

    @PostMapping
    public ResponseEntity<CreatePecaResponseDTO> createPeca(@RequestBody @Valid CreatePecaRequestDTO createPecaRequestDTO) throws GatewayException {
        Peca peca = createPecaGateway.execute(createPecaRequestDTO.toDomain());

        return ResponseEntity.created(buildLocationUri(peca)).body(CreatePecaResponseDTO.fromDomain(peca));
    }

//    @GetMapping("/{clienteId}")
//    public ResponseEntity<FindClienteResponseDTO> findPeca(@PathVariable Long pecaId) throws GatewayException {
//        Peca peca = findPecaGateway.execute(pecaId);
//
//        return ResponseEntity.ok().body(FindPecaResponseDTO.fromDomain(peca));
//    }

    private URI buildLocationUri(Peca peca) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(peca.getId())
                .toUri();
    }
}
