package com.grupo52.tech_challenge.api;

import com.grupo52.tech_challenge.dto.response.MarcaInfoResponseDTO;
import com.grupo52.tech_challenge.dto.response.ModeloInfoResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/marcas")
public interface MarcaApi {

    @GetMapping
    ResponseEntity<List<MarcaInfoResponseDTO>> listMarcas() throws GatewayException;

    @GetMapping("/{marcaId}/modelos")
    ResponseEntity<List<ModeloInfoResponseDTO>> listModelosByMarcaId(
            @PathVariable Long marcaId) throws GatewayException;
}