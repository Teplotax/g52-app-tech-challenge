package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.dto.request.CreateClienteRequestDTO;
import com.grupo52.tech_challenge.dto.response.ClienteInfoResponseDTO;
import com.grupo52.tech_challenge.dto.response.CreateClienteResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreateClienteGateway;
import com.grupo52.tech_challenge.gateway.FindClienteGateway;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private CreateClienteGateway createClienteGateway;

    @Autowired
    private FindClienteGateway findClienteGateway;

    @PostMapping
    public ResponseEntity<CreateClienteResponseDTO> createCliente(@RequestBody @Valid CreateClienteRequestDTO createClienteRequestDTO) throws GatewayException {
        Cliente cliente = createClienteGateway.execute(createClienteRequestDTO.toDomain());

        return ResponseEntity.created(buildLocationUri(cliente)).body(CreateClienteResponseDTO.fromDomain(cliente));
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<ClienteInfoResponseDTO> findCliente(@PathVariable Long clienteId) throws GatewayException {
        Cliente cliente = findClienteGateway.execute(clienteId);

        return ResponseEntity.ok().body(ClienteInfoResponseDTO.fromDomain(cliente));
    }

    private URI buildLocationUri(Cliente cliente) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cliente.getId())
                .toUri();
    }
}
