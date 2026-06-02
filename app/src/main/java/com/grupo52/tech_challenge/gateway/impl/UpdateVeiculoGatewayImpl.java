package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.UpdateVeiculoGateway;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.ModeloDatabase;
import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import com.grupo52.tech_challenge.gateway.database.repository.ModeloRepository;
import com.grupo52.tech_challenge.gateway.database.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateVeiculoGatewayImpl implements UpdateVeiculoGateway {

    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;
    private final ModeloRepository modeloRepository;

    @Override
    public Veiculo execute(Veiculo veiculo) throws GatewayException {
        try {
            VeiculoDatabase existing = veiculoRepository.findById(veiculo.getId()).orElseThrow(
                    () -> new NotFoundGatewayException("Veículo não encontrado")
            );

            ModeloDatabase modelo = existing.getModelo();
            if (veiculo.getModelo() != null && veiculo.getModelo().getId() != null) {
                modelo = modeloRepository.findById(veiculo.getModelo().getId()).orElseThrow(
                        () -> new NotFoundGatewayException("Modelo não encontrado")
                );
            }

            ClienteDatabase cliente = existing.getCliente();
            if (veiculo.getCliente() != null && veiculo.getCliente().getId() != null) {
                cliente = clienteRepository.findById(veiculo.getCliente().getId()).orElseThrow(
                        () -> new NotFoundGatewayException("Cliente não encontrado")
                );
            }

            VeiculoDatabase updated = VeiculoDatabase.builder()
                    .id(existing.getId())
                    .placa(veiculo.getPlaca() != null ? veiculo.getPlaca() : existing.getPlaca())
                    .ano(veiculo.getAno() != null ? veiculo.getAno() : existing.getAno())
                    .cor(veiculo.getCor() != null ? veiculo.getCor() : existing.getCor())
                    .modelo(modelo)
                    .cliente(cliente)
                    .build();

            return veiculoRepository.save(updated).toDomain();
        } catch (NotFoundGatewayException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new GatewayException("Falha ao atualizar Veículo, placa já cadastrada", 409);
        } catch (Exception e) {
            throw new GatewayException("Falha ao atualizar Veículo, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}