package com.grupo52.tech_challenge.api;

import com.grupo52.tech_challenge.dto.request.AddServicosRequestDTO;
import com.grupo52.tech_challenge.dto.response.AddServicosResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AddServicosApi {

    @PostMapping("/ordensDeServico/{osId}/adicionarServicos")
    ResponseEntity<AddServicosResponseDTO> execute(
            @PathVariable Long osId,
            @RequestBody @Valid AddServicosRequestDTO addServicosRequestDTO) throws GatewayException, ValidationException, UseCaseException;
}