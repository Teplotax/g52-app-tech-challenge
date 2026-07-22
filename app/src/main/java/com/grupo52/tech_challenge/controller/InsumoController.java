package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.InsumoApi;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.dto.request.CreateInsumoRequestDTO;
import com.grupo52.tech_challenge.dto.request.UpdateInsumoRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreateInsumoResponseDTO;
import com.grupo52.tech_challenge.dto.response.FindInsumoResponseDTO;
import com.grupo52.tech_challenge.dto.response.UpdateInsumoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreateInsumoGateway;
import com.grupo52.tech_challenge.gateway.DeleteProdutoGateway;
import com.grupo52.tech_challenge.gateway.FindInsumoGateway;
import com.grupo52.tech_challenge.gateway.UpdateInsumoGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@Validated
public class InsumoController implements InsumoApi {

    @Autowired
    private CreateInsumoGateway createInsumoGateway;

    @Autowired
    private FindInsumoGateway findInsumoGateway;

    @Autowired
    private UpdateInsumoGateway updateInsumoGateway;

    @Autowired
    private DeleteProdutoGateway deleteProdutoGateway;

    @Override
    public ResponseEntity<CreateInsumoResponseDTO> createInsumo(CreateInsumoRequestDTO createInsumoRequestDTO) throws GatewayException {
        Insumo insumo = createInsumoGateway.execute(createInsumoRequestDTO.toDomain());

        return ResponseEntity.created(buildLocationUri(insumo)).body(CreateInsumoResponseDTO.fromDomain(insumo));
    }

    @Override
    public ResponseEntity<FindInsumoResponseDTO> findInsumo(Long insumoId) throws GatewayException {
        Insumo insumo = findInsumoGateway.execute(insumoId);

        return ResponseEntity.ok().body(FindInsumoResponseDTO.fromDomain(insumo));
    }

    @Override
    public ResponseEntity<UpdateInsumoResponseDTO> updateInsumo(Long insumoId, UpdateInsumoRequestDTO updateInsumoRequestDTO) throws GatewayException {
        Insumo insumo = updateInsumoGateway.execute(updateInsumoRequestDTO.toDomain(insumoId));

        return ResponseEntity.ok().body(UpdateInsumoResponseDTO.fromDomain(insumo));
    }

    @Override
    public ResponseEntity<Void> deleteInsumo(Long insumoId) throws GatewayException {
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