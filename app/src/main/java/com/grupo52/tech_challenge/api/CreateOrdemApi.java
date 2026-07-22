package com.grupo52.tech_challenge.api;

import com.grupo52.tech_challenge.dto.request.CreateOrderRequestDTO;
import com.grupo52.tech_challenge.dto.response.CreateOSResponseDTO;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface CreateOrdemApi {

    @PostMapping("/ordensDeServico")
    ResponseEntity<CreateOSResponseDTO> execute(
            @RequestBody @Valid CreateOrderRequestDTO createOrderRequestDTO) throws GatewayException, UseCaseException;
}