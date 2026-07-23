package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.MarcaApi;
import com.grupo52.tech_challenge.domain.Marca;
import com.grupo52.tech_challenge.domain.Modelo;
import com.grupo52.tech_challenge.dto.response.MarcaInfoResponseDTO;
import com.grupo52.tech_challenge.dto.response.ModeloInfoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.ListMarcasGateway;
import com.grupo52.tech_challenge.gateway.ListModelosByMarcaGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MarcaController implements MarcaApi {

    @Autowired
    private ListMarcasGateway listMarcasGateway;

    @Autowired
    private ListModelosByMarcaGateway listModelosByMarcaGateway;

    @Override
    public ResponseEntity<List<MarcaInfoResponseDTO>> listMarcas() throws GatewayException {
        List<Marca> marcas = listMarcasGateway.execute();

        return ResponseEntity.ok(MarcaInfoResponseDTO.fromDomain(marcas));
    }

    @Override
    public ResponseEntity<List<ModeloInfoResponseDTO>> listModelosByMarcaId(Long marcaId) throws GatewayException {
        List<Modelo> marcas = listModelosByMarcaGateway.execute(marcaId);

        return ResponseEntity.ok(ModeloInfoResponseDTO.fromDomain(marcas));
    }
}