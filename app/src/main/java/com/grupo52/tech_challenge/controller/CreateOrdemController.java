package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.CreateOrdemApi;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.dto.request.CreateOrderRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreateOSResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.usecase.CreateOrdemUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@Validated
public class CreateOrdemController implements CreateOrdemApi {

    @Autowired
    private CreateOrdemUseCase createOrdemUseCase;

    @Override
    public ResponseEntity<CreateOSResponseDTO> execute(CreateOrderRequestDTO createOrderRequestDTO) throws GatewayException, UseCaseException {
        Ordem os = createOrdemUseCase.execute(createOrderRequestDTO.toDomain());

        return ResponseEntity.created(buildLocationUri(os)).body(CreateOSResponseDTO.fromDomain(os));
    }

    private URI buildLocationUri(Ordem os) {
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(os.getId())
                .toUri();
    }
}