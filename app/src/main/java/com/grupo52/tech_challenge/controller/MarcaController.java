package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Marca;
import com.grupo52.tech_challenge.domain.Modelo;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.dto.response.MarcaInfoResponseDTO;
import com.grupo52.tech_challenge.dto.response.ModeloInfoResponseDTO;
import com.grupo52.tech_challenge.dto.response.VeiculoInfoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.ListMarcasGateway;
import com.grupo52.tech_challenge.gateway.ListModelosByMarcaGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/marcas")
public class MarcaController {

    @Autowired
    private ListMarcasGateway listMarcasGateway;

    @Autowired
    private ListModelosByMarcaGateway listModelosByMarcaGateway;

    @GetMapping
    public ResponseEntity<List<MarcaInfoResponseDTO>> listMarcas() throws GatewayException {
        List<Marca> marcas = listMarcasGateway.execute();

        return ResponseEntity.ok(MarcaInfoResponseDTO.fromDomain(marcas));
    }

    @GetMapping("/{marcaId}/modelos")
    public ResponseEntity<List<ModeloInfoResponseDTO>> listModelosByMarcaId(
            @PathVariable Long marcaId
    ) throws GatewayException {
        List<Modelo> marcas = listModelosByMarcaGateway.execute(marcaId);

        return ResponseEntity.ok(ModeloInfoResponseDTO.fromDomain(marcas));
    }
}
