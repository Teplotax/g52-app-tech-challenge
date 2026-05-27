package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.dto.PagedResponse;
import com.grupo52.tech_challenge.dto.request.CreateClienteRequestDTO;
import com.grupo52.tech_challenge.dto.response.ClienteInfoResponseDTO;
import com.grupo52.tech_challenge.dto.response.CreateClienteResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreateClienteGateway;
import com.grupo52.tech_challenge.gateway.FindClienteGateway;
import com.grupo52.tech_challenge.gateway.ListClientesGateway;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private CreateClienteGateway createClienteGateway;

    @Autowired
    private FindClienteGateway findClienteGateway;

    @Autowired
    private ListClientesGateway listClientesGateway;

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

    @GetMapping
    public ResponseEntity<PagedResponse<ClienteInfoResponseDTO>> listClientes(
            @PageableDefault(size = 20, page = 0) Pageable pageable) throws GatewayException {
        Page<Cliente> clientes = listClientesGateway.execute(pageable);
        
        return ResponseEntity.ok(ClienteInfoResponseDTO.fromDomain(clientes));
    }

    private URI buildLocationUri(Cliente cliente) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cliente.getId())
                .toUri();
    }
}
