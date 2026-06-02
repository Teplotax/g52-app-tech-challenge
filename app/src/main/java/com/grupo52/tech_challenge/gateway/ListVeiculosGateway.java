package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListVeiculosGateway {
    Page<Veiculo> execute(Pageable pageable) throws GatewayException;
}