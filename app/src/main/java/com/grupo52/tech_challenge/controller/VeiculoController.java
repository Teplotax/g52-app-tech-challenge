package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.VeiculoApi;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.dto.PagedResponse;
import com.grupo52.tech_challenge.dto.request.CreateVeiculoRequestDTO;
import com.grupo52.tech_challenge.dto.request.UpdateVeiculoRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreateVeiculoResponseDTO;
import com.grupo52.tech_challenge.dto.response.FindVeiculoResponseDTO;
import com.grupo52.tech_challenge.dto.response.VeiculoInfoResponseDTO;
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
public class VeiculoController implements VeiculoApi {

    @Autowired
    private CreateVeiculoGateway createVeiculoGateway;

    @Autowired
    private FindVeiculoGateway findVeiculoGateway;

    @Autowired
    private FindVeiculoByPlacaGateway findVeiculoByPlacaGateway;

    @Autowired
    private ListVeiculosGateway listVeiculosGateway;

    @Autowired
    private UpdateVeiculoGateway updateVeiculoGateway;

    @Autowired
    private DeleteVeiculoGateway deleteVeiculoGateway;

    @Override
    public ResponseEntity<CreateVeiculoResponseDTO> createVeiculo(CreateVeiculoRequestDTO createVeiculoRequestDTO) throws GatewayException {
        Veiculo veiculo = createVeiculoGateway.execute(createVeiculoRequestDTO.toDomain());

        return ResponseEntity.created(buildLocationUri(veiculo)).body(CreateVeiculoResponseDTO.fromDomain(veiculo));
    }

    @Override
    public ResponseEntity<FindVeiculoResponseDTO> findVeiculo(Long veiculoId) throws GatewayException {
        Veiculo veiculo = findVeiculoGateway.execute(veiculoId);

        return ResponseEntity.ok().body(FindVeiculoResponseDTO.fromDomain(veiculo));
    }

    @Override
    public ResponseEntity<FindVeiculoResponseDTO> findVeiculo(String placa) throws GatewayException {
        Veiculo veiculo = findVeiculoByPlacaGateway.execute(placa);

        return ResponseEntity.ok().body(FindVeiculoResponseDTO.fromDomain(veiculo));
    }

    @Override
    public ResponseEntity<PagedResponse<VeiculoInfoResponseDTO>> listVeiculos(Pageable pageable) throws GatewayException {
        Page<Veiculo> veiculos = listVeiculosGateway.execute(pageable);

        return ResponseEntity.ok(VeiculoInfoResponseDTO.fromDomain(veiculos));
    }

    @Override
    public ResponseEntity<VeiculoInfoResponseDTO> updateVeiculo(Long veiculoId, UpdateVeiculoRequestDTO updateVeiculoRequestDTO) throws GatewayException {
        Veiculo veiculo = updateVeiculoGateway.execute(updateVeiculoRequestDTO.toDomain(veiculoId));

        return ResponseEntity.ok().body(VeiculoInfoResponseDTO.fromDomain(veiculo));
    }

    @Override
    public ResponseEntity<Void> deleteVeiculo(Long veiculoId) throws GatewayException {
        deleteVeiculoGateway.execute(veiculoId);

        return ResponseEntity.noContent().header("Location", buildLocationUri()).build();
    }

    private URI buildLocationUri(Veiculo veiculo) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(veiculo.getId())
                .toUri();
    }

    private String buildLocationUri() {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build().toString();
    }
}