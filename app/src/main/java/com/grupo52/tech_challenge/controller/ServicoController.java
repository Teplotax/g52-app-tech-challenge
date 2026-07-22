package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.ServicoApi;
import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.dto.PagedResponse;
import com.grupo52.tech_challenge.dto.request.CreateServicoRequestDTO;
import com.grupo52.tech_challenge.dto.request.UpdateServicoRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreateServicoResponseDTO;
import com.grupo52.tech_challenge.dto.response.FindServicoResponseDTO;
import com.grupo52.tech_challenge.dto.response.ServicoInfoResponseDTO;
import com.grupo52.tech_challenge.dto.response.UpdateServicoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@Validated
public class ServicoController implements ServicoApi {

    @Autowired
    private CreateServicoGateway createServicoGateway;
    @Autowired
    private ListServicosGateway listServicosGateway;
    @Autowired
    private FindServicoGateway findServicoGateway;
    @Autowired
    private UpdateServicoGateway updateServicoGateway;
    @Autowired
    private DeleteServicoGateway deleteServicoGateway;

    @Override
    public ResponseEntity<CreateServicoResponseDTO> createServico(CreateServicoRequestDTO createServicoRequestDTO) throws GatewayException {
        Servico servico = createServicoGateway.execute(createServicoRequestDTO.toDomain());

        return ResponseEntity.created(buildLocationUri(servico)).body(CreateServicoResponseDTO.fromDomain(servico));
    }

    @Override
    public ResponseEntity<PagedResponse<ServicoInfoResponseDTO>> listServicos(Pageable pageable) throws GatewayException {
        Page<Servico> servicos = listServicosGateway.execute(pageable);

        return ResponseEntity.ok(ServicoInfoResponseDTO.fromDomain(servicos));
    }

    @Override
    public ResponseEntity<FindServicoResponseDTO> findServico(Long servicoId) throws GatewayException {
        Servico servico = findServicoGateway.execute(servicoId);

        return ResponseEntity.ok().body(FindServicoResponseDTO.fromDomain(servico));
    }

    @Override
    public ResponseEntity<UpdateServicoResponseDTO> updateServico(Long servicoId, UpdateServicoRequestDTO updateServicoRequestDTO) throws GatewayException {
        Servico servico = updateServicoGateway.execute(updateServicoRequestDTO.toDomain(servicoId));

        return ResponseEntity.ok().body(UpdateServicoResponseDTO.fromDomain(servico));
    }

    @Override
    public ResponseEntity<Void> deleteServico(Long servicoId) throws GatewayException {
        deleteServicoGateway.execute(servicoId);

        return ResponseEntity.noContent().header("Location", buildLocationUri()).build();
    }

    private URI buildLocationUri(Servico servico) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(servico.getId())
                .toUri();
    }

    private String buildLocationUri() {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build().toString();
    }
}