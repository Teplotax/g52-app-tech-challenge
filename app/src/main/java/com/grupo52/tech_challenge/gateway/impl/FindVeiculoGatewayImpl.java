package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.FindVeiculoGateway;
import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindVeiculoGatewayImpl implements FindVeiculoGateway {

    private final VeiculoRepository repository;

    public Veiculo execute(Long veiculoId) throws GatewayException {
        try {
            Optional<VeiculoDatabase> veiculoOptional = repository.findById(veiculoId);
            VeiculoDatabase veiculoDatabase = veiculoOptional.orElseThrow(
                    () -> new NotFoundGatewayException("Veículo não encontrado")
            );
            return veiculoDatabase.toDomain();
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao consultar veículo, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}
