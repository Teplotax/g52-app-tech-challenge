package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.api.AddServicosApi;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.dto.request.AddServicosRequestDTO;
import com.grupo52.tech_challenge.dto.response.AddServicosResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.usecase.AddServicosUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class AddServicosController implements AddServicosApi {

    @Autowired
    private AddServicosUseCase addServicosUseCase;

    @Override
    public ResponseEntity<AddServicosResponseDTO> execute(Long osId, AddServicosRequestDTO addServicosRequestDTO) throws GatewayException, ValidationException, UseCaseException {
        Ordem os = addServicosUseCase.execute(addServicosRequestDTO.toDomain(osId));

        return ResponseEntity.ok(AddServicosResponseDTO.fromDomain(os));
    }
}