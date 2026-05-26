package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.dto.request.CreateVeiculoRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreateVeiculoResponseDTO;
import com.grupo52.tech_challenge.dto.response.VeiculoInfoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreateVeiculoGateway;
import com.grupo52.tech_challenge.gateway.FindVeiculoGateway;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private CreateVeiculoGateway createVeiculoGateway;

    @Autowired
    private FindVeiculoGateway findVeiculoGateway;

    @PostMapping
    public ResponseEntity<CreateVeiculoResponseDTO> createCliente(@RequestBody @Valid CreateVeiculoRequestDTO createVeiculoRequestDTO) throws GatewayException {
        Veiculo veiculo = createVeiculoGateway.execute(createVeiculoRequestDTO.toDomain());

        return ResponseEntity.created(buildLocationUri(veiculo)).body(CreateVeiculoResponseDTO.fromDomain(veiculo));
    }

    @GetMapping("/{veiculoId}")
    public ResponseEntity<VeiculoInfoResponseDTO> findVeiculo(@PathVariable Long veiculoId) throws GatewayException {
        Veiculo veiculo = findVeiculoGateway.execute(veiculoId);

        return ResponseEntity.ok().body(VeiculoInfoResponseDTO.fromDomain(veiculo));
    }

    private URI buildLocationUri(Veiculo veiculo) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(veiculo.getId())
                .toUri();
    }
}
