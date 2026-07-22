package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.FindOrdemApi;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.dto.response.FindOSResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FindOrdemController implements FindOrdemApi {

    @Autowired
    private FindOrdemGateway findOrdemGateway;

    @Override
    public ResponseEntity<FindOSResponseDTO> execute(Long osId) throws GatewayException {
        Ordem os = findOrdemGateway.execute(osId);

        return ResponseEntity.ok(FindOSResponseDTO.fromDomain(os));
    }
}