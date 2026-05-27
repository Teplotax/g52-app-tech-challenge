package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.dto.response.VeiculoInfoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.ListVeiculosByClienteGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ListVeiculosByClienteController {

    @Autowired
    private ListVeiculosByClienteGateway listVeiculosByClienteGateway;

    @GetMapping("/clientes/{clienteId}/veiculos")
    public ResponseEntity<List<VeiculoInfoResponseDTO>> findCliente(@PathVariable Long clienteId) throws GatewayException {
        List<Veiculo> veiculos = listVeiculosByClienteGateway.execute(clienteId);

        return ResponseEntity.ok().body(VeiculoInfoResponseDTO.fromDomain(veiculos));
    }
}
