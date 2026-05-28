package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.CreateVeiculoGateway;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.ModeloDatabase;
import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import com.grupo52.tech_challenge.gateway.database.repository.ModeloRepository;
import com.grupo52.tech_challenge.gateway.database.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateVeiculoGatewayImpl implements CreateVeiculoGateway {

    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;
    private final ModeloRepository modeloRepository;

    public Veiculo execute(Veiculo veiculo) throws GatewayException {
        try {
            Optional<ClienteDatabase> clienteOptional = clienteRepository.findById(veiculo.getCliente().getId());
            ClienteDatabase clienteDatabase = clienteOptional.orElseThrow(
                    () -> new NotFoundGatewayException("Cliente não encontrado")
            );
            Optional<ModeloDatabase> modeloOptional = modeloRepository.findById(veiculo.getModelo().getId());
            ModeloDatabase modeloDatabase = modeloOptional.orElseThrow(
                    () -> new NotFoundGatewayException("Modelo não encontrado")
            );

            return veiculoRepository.save(VeiculoDatabase.fromDomain(veiculo, modeloDatabase, clienteDatabase)).toDomain();
        } catch (DataIntegrityViolationException e) {
            throw new GatewayException("Falha ao cadastrar Veículo, placa já cadastrada", 409);
        } catch (Exception e) {
            throw new GatewayException("Falha ao cadastrar Veículo, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}