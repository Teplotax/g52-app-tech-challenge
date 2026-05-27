package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Marca;
import com.grupo52.tech_challenge.dto.response.MarcaInfoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.ListMarcasGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/marcas")
public class MarcaController {

    @Autowired
    private ListMarcasGateway listMarcasGateway;

    @GetMapping
    public ResponseEntity<List<MarcaInfoResponseDTO>> listClientes() throws GatewayException {
        List<Marca> marcas = listMarcasGateway.execute();

        return ResponseEntity.ok(MarcaInfoResponseDTO.fromDomain(marcas));
    }
}
