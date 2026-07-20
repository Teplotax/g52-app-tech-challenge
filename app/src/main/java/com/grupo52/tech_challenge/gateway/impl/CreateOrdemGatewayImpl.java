package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.gateway.CreateOrdemGateway;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.OrdemDeServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.OrdemDeServicoRepository;
import com.grupo52.tech_challenge.gateway.database.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateOrdemGatewayImpl implements CreateOrdemGateway {

    private final OrdemDeServicoRepository ordemDeServicoRepository;
    private final VeiculoRepository veiculoRepository;

    @Override
    @Transactional
    public Ordem execute(Ordem os) throws GatewayException, UseCaseException {
        try {
            VeiculoDatabase veiculo = veiculoRepository.findByPlaca(os.getVeiculo().getPlaca())
                    .orElseThrow(() -> new NotFoundGatewayException("Veículo não encontrado"));

            ClienteDatabase cliente = veiculo.getCliente();

            OrdemDeServicoDatabase osDatabase = OrdemDeServicoDatabase.fromDomain(os, cliente, veiculo);

            return ordemDeServicoRepository.saveAndFlush(osDatabase).toDomain();
        } catch (DataIntegrityViolationException e) {
            String cause = e.getMostSpecificCause().getMessage().toLowerCase();
            if (cause.contains("tagchave") || cause.contains("tag_chave")) {
                throw new GatewayException("tagChave já cadastrada em outra OS ativa", 409);
            }
            throw new GatewayException("Violação de integridade ao cadastrar OS", 409);
        } catch (GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException("Falha ao cadastrar OS, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}