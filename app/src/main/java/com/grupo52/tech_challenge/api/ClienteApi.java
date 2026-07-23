package com.grupo52.tech_challenge.api;

import com.grupo52.tech_challenge.dto.PagedResponse;
import com.grupo52.tech_challenge.dto.request.CreateClienteRequestDTO;
import com.grupo52.tech_challenge.dto.request.UpdateClienteRequestDTO;
import com.grupo52.tech_challenge.dto.response.*;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.validation.annotation.Documento;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/clientes")
public interface ClienteApi {

    @PostMapping
    ResponseEntity<CreateClienteResponseDTO> createCliente(
            @RequestBody @Valid CreateClienteRequestDTO createClienteRequestDTO) throws GatewayException;

    @GetMapping("/{clienteId}")
    ResponseEntity<FindClienteResponseDTO> findCliente(
            @PathVariable Long clienteId) throws GatewayException;

    @GetMapping("/documento/{documento}")
    ResponseEntity<FindClienteResponseDTO> findClienteByDocumento(
            @PathVariable @Documento String documento) throws GatewayException;

    @GetMapping
    ResponseEntity<PagedResponse<ClienteInfoResponseDTO>> listClientes(
            @PageableDefault(size = 20, page = 0) Pageable pageable) throws GatewayException;

    @GetMapping("/{clienteId}/veiculos")
    ResponseEntity<List<VeiculoInfoResponseDTO>> findVeiculosByCliente(
            @PathVariable Long clienteId) throws GatewayException;

    @PutMapping("/{clienteId}")
    ResponseEntity<UpdateClienteResponseDTO> updateCliente(
            @PathVariable Long clienteId,
            @RequestBody @Valid UpdateClienteRequestDTO updateClienteRequestDTO) throws GatewayException;

    @DeleteMapping("/{clienteId}")
    ResponseEntity<Void> deleteCliente(
            @PathVariable Long clienteId) throws GatewayException;
}