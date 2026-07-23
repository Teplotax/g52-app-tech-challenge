package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.PecaApi;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.dto.request.CreatePecaRequestDTO;
import com.grupo52.tech_challenge.dto.request.UpdatePecaRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreatePecaResponseDTO;
import com.grupo52.tech_challenge.dto.response.FindPecaResponseDTO;
import com.grupo52.tech_challenge.dto.response.UpdatePecaResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreatePecaGateway;
import com.grupo52.tech_challenge.gateway.DeleteProdutoGateway;
import com.grupo52.tech_challenge.gateway.FindPecaGateway;
import com.grupo52.tech_challenge.gateway.UpdatePecaGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@Validated
public class PecaController implements PecaApi {

    @Autowired
    private CreatePecaGateway createPecaGateway;

    @Autowired
    private FindPecaGateway findPecaGateway;

    @Autowired
    private UpdatePecaGateway updatePecaGateway;

    @Autowired
    private DeleteProdutoGateway deleteProdutoGateway;

    @Override
    public ResponseEntity<CreatePecaResponseDTO> createPeca(CreatePecaRequestDTO createPecaRequestDTO) throws GatewayException {
        Peca peca = createPecaGateway.execute(createPecaRequestDTO.toDomain());

        return ResponseEntity.created(buildLocationUri(peca)).body(CreatePecaResponseDTO.fromDomain(peca));
    }

    @Override
    public ResponseEntity<FindPecaResponseDTO> findPeca(Long pecaId) throws GatewayException {
        Peca peca = findPecaGateway.execute(pecaId);

        return ResponseEntity.ok().body(FindPecaResponseDTO.fromDomain(peca));
    }

    @Override
    public ResponseEntity<UpdatePecaResponseDTO> updatePeca(Long pecaId, UpdatePecaRequestDTO updatePecaRequestDTO) throws GatewayException {
        Peca peca = updatePecaGateway.execute(updatePecaRequestDTO.toDomain(pecaId));

        return ResponseEntity.ok().body(UpdatePecaResponseDTO.fromDomain(peca));
    }

    @Override
    public ResponseEntity<Void> deletePeca(Long pecaId) throws GatewayException {
        deleteProdutoGateway.execute(pecaId, TipoProduto.PECA);

        return ResponseEntity.noContent().header("Location", buildLocationUri()
        ).build();
    }

    private URI buildLocationUri(Peca peca) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(peca.getId())
                .toUri();
    }

    private String buildLocationUri() {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build().toString();
    }
}