package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.dto.request.CreateInsumoRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreateInsumoResponseDTO;
import com.grupo52.tech_challenge.dto.response.FindInsumoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreateInsumoGateway;
import com.grupo52.tech_challenge.gateway.DeleteProdutoGateway;
import com.grupo52.tech_challenge.gateway.FindInsumoGateway;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/insumos")
@Validated
public class InsumoController {

    @Autowired
    private CreateInsumoGateway createInsumoGateway;

    @Autowired
    private FindInsumoGateway findInsumoGateway;
//
//    @Autowired
//    private UpdateInsumoGateway updateInsumoGateway;

    @Autowired
    private DeleteProdutoGateway deleteProdutoGateway;

    @PostMapping
    public ResponseEntity<CreateInsumoResponseDTO> createInsumo(@RequestBody @Valid CreateInsumoRequestDTO createInsumoRequestDTO) throws GatewayException {
        Insumo insumo = createInsumoGateway.execute(createInsumoRequestDTO.toDomain());

        return ResponseEntity.created(buildLocationUri(insumo)).body(CreateInsumoResponseDTO.fromDomain(insumo));
    }

    @GetMapping("/{insumoId}")
    public ResponseEntity<FindInsumoResponseDTO> findInsumo(@PathVariable Long insumoId) throws GatewayException {
        Insumo insumo = findInsumoGateway.execute(insumoId);

        return ResponseEntity.ok().body(FindInsumoResponseDTO.fromDomain(insumo));
    }
//
//    @PutMapping("/{insumoId}")
//    public ResponseEntity<UpdatePecaResponseDTO> updateInsumo(
//            @PathVariable Long insumoId,
//            @RequestBody @Valid UpdatePecaRequestDTO updatePecaRequestDTO) throws GatewayException {
//        Peca peca = updateInsumoGateway.execute(updatePecaRequestDTO.toDomain(insumoId));
//
//        return ResponseEntity.ok().body(UpdatePecaResponseDTO.fromDomain(peca));
//    }

    @DeleteMapping("/{insumoId}")
    public ResponseEntity<Void> deleteInsumo(@PathVariable Long insumoId) throws GatewayException {
        deleteProdutoGateway.execute(insumoId, TipoProduto.INSUMO);

        return ResponseEntity.noContent().header("Location", buildLocationUri()
        ).build();
    }

    private URI buildLocationUri(Insumo insumo) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(insumo.getId())
                .toUri();
    }

    private String buildLocationUri() {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build().toString();
    }
}
