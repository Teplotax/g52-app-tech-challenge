package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.dto.PagedResponse;
import com.grupo52.tech_challenge.dto.request.CreateClienteRequestDTO;
import com.grupo52.tech_challenge.dto.request.UpdateClienteRequestDTO;
import com.grupo52.tech_challenge.dto.response.*;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.*;
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

    @Autowired
    private ListVeiculosByClienteGateway listVeiculosByClienteGateway;

    @Autowired
    private UpdateClienteGateway updateClienteGateway;

    @Autowired
    private DeleteClienteGateway deleteClienteGateway;

    @PostMapping
    public ResponseEntity<CreateClienteResponseDTO> createCliente(@RequestBody @Valid CreateClienteRequestDTO createClienteRequestDTO) throws GatewayException {
        Cliente cliente = createClienteGateway.execute(createClienteRequestDTO.toDomain());

        return ResponseEntity.created(buildLocationUri(cliente)).body(CreateClienteResponseDTO.fromDomain(cliente));
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<FindClienteResponseDTO> findCliente(@PathVariable Long clienteId) throws GatewayException {
        Cliente cliente = findClienteGateway.execute(clienteId);

        return ResponseEntity.ok().body(FindClienteResponseDTO.fromDomain(cliente));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ClienteInfoResponseDTO>> listClientes(
            @PageableDefault(size = 20, page = 0) Pageable pageable) throws GatewayException {
        Page<Cliente> clientes = listClientesGateway.execute(pageable);
        
        return ResponseEntity.ok(ClienteInfoResponseDTO.fromDomain(clientes));
    }

    @GetMapping("/{clienteId}/veiculos")
    public ResponseEntity<List<VeiculoInfoResponseDTO>> findVeiculosByCliente(@PathVariable Long clienteId) throws GatewayException {
        List<Veiculo> veiculos = listVeiculosByClienteGateway.execute(clienteId);

        return ResponseEntity.ok().body(VeiculoInfoResponseDTO.fromDomain(veiculos));
    }

    @PutMapping("/{clienteId}")
    public ResponseEntity<UpdateClienteResponseDTO> updateCliente(
            @PathVariable Long clienteId,
            @RequestBody @Valid UpdateClienteRequestDTO updateClienteRequestDTO) throws GatewayException {
        Cliente cliente = updateClienteGateway.execute(updateClienteRequestDTO.toDomain(clienteId));

        return ResponseEntity.ok().body(UpdateClienteResponseDTO.fromDomain(cliente));
    }

    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long clienteId) throws GatewayException {
        deleteClienteGateway.execute(clienteId);

        return ResponseEntity.noContent().build();
    }

    private URI buildLocationUri(Cliente cliente) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cliente.getId())
                .toUri();
    }
}
