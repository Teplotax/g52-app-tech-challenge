package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.FindVeiculoByPlacaGateway;
import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindVeiculoByPlacaGatewayImpl implements FindVeiculoByPlacaGateway {

    private final VeiculoRepository repository;

    public Veiculo execute(String placa) throws GatewayException {
        try {
            Optional<VeiculoDatabase> veiculoOptional = repository.findByPlaca(placa);
            VeiculoDatabase veiculoDatabase = veiculoOptional.orElseThrow(
                    () -> new NotFoundGatewayException("Veículo não encontrado")
            );
            return veiculoDatabase.toDomain();
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao consultar veículo por placa, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}
