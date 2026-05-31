package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.ListVeiculosByClienteGateway;
import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListVeiculosByClienteGatewayImpl implements ListVeiculosByClienteGateway {

    private final VeiculoRepository repository;

    public List<Veiculo> execute(Long veiculoId) throws GatewayException {
        try {
            List<VeiculoDatabase> veiculos = repository.findByClienteId(veiculoId);

            return veiculos.stream().map(VeiculoDatabase::toDomain).toList();

        } catch (Exception e) {
            throw new GatewayException("Falha ao consultar veículo, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}
