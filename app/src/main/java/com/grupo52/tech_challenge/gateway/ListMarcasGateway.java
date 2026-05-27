package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Marca;
import com.grupo52.tech_challenge.exception.GatewayException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListMarcasGateway {
    List<Marca> execute() throws GatewayException;
}