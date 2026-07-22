package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.ClienteApi;
import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.dto.PagedResponse;
import com.grupo52.tech_challenge.dto.request.CreateClienteRequestDTO;
import com.grupo52.tech_challenge.dto.request.UpdateClienteRequestDTO;
import com.grupo52.tech_challenge.dto.response.*;
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
import java.util.List;

@RestController
@Validated
public class ClienteController implements ClienteApi {

    @Autowired
    private CreateClienteGateway createClienteGateway;

    @Autowired
    private FindClienteGateway findClienteGateway;

    @Autowired
    private FindClienteByDocumentGateway findClienteByDocumentGateway;

    @Autowired
    private ListClientesGateway listClientesGateway;

    @Autowired
    private ListVeiculosByClienteGateway listVeiculosByClienteGateway;

    @Autowired
    private UpdateClienteGateway updateClienteGateway;

    @Autowired
    private DeleteClienteGateway deleteClienteGateway;

    @Override
    public ResponseEntity<CreateClienteResponseDTO> createCliente(CreateClienteRequestDTO createClienteRequestDTO) throws GatewayException {
        Cliente cliente = createClienteGateway.execute(createClienteRequestDTO.toDomain());

        return ResponseEntity.created(buildLocationUri(cliente)).body(CreateClienteResponseDTO.fromDomain(cliente));
    }

    @Override
    public ResponseEntity<FindClienteResponseDTO> findCliente(Long clienteId) throws GatewayException {
        Cliente cliente = findClienteGateway.execute(clienteId);

        return ResponseEntity.ok().body(FindClienteResponseDTO.fromDomain(cliente));
    }

    @Override
    public ResponseEntity<FindClienteResponseDTO> findClienteByDocumento(String documento) throws GatewayException {
        Cliente cliente = findClienteByDocumentGateway.execute(documento);

        return ResponseEntity.ok().body(FindClienteResponseDTO.fromDomain(cliente));
    }

    @Override
    public ResponseEntity<PagedResponse<ClienteInfoResponseDTO>> listClientes(Pageable pageable) throws GatewayException {
        Page<Cliente> clientes = listClientesGateway.execute(pageable);

        return ResponseEntity.ok(ClienteInfoResponseDTO.fromDomain(clientes));
    }

    @Override
    public ResponseEntity<List<VeiculoInfoResponseDTO>> findVeiculosByCliente(Long clienteId) throws GatewayException {
        List<Veiculo> veiculos = listVeiculosByClienteGateway.execute(clienteId);

        return ResponseEntity.ok().body(VeiculoInfoResponseDTO.fromDomain(veiculos));
    }

    @Override
    public ResponseEntity<UpdateClienteResponseDTO> updateCliente(Long clienteId, UpdateClienteRequestDTO updateClienteRequestDTO) throws GatewayException {
        Cliente cliente = updateClienteGateway.execute(updateClienteRequestDTO.toDomain(clienteId));

        return ResponseEntity.ok().body(UpdateClienteResponseDTO.fromDomain(cliente));
    }

    @Override
    public ResponseEntity<Void> deleteCliente(Long clienteId) throws GatewayException {
        deleteClienteGateway.execute(clienteId);

        return ResponseEntity.noContent().header("Location", buildLocationUri()
        ).build();
    }

    private URI buildLocationUri(Cliente cliente) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cliente.getId())
                .toUri();
    }

    private String buildLocationUri() {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build().toString();
    }
}