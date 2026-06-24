package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreateOSGateway;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.OrdemDeServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import com.grupo52.tech_challenge.gateway.database.repository.OrdemDeServicoRepository;
import com.grupo52.tech_challenge.gateway.database.repository.VeiculoRepository;
import com.grupo52.tech_challenge.service.UpdateOSStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateOSGatewayImpl implements CreateOSGateway {

    private final UpdateOSStatusService updateOSStatusService;
    private final OrdemDeServicoRepository ordemDeServicoRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;

    @Override
    public OrdemDeServico execute(OrdemDeServico os) throws GatewayException {
        try {
            updateOSStatusService.execute(os, StatusOS.RECEBIDA);

            ClienteDatabase cliente = clienteRepository.findById(os.getCliente().getId())
                    .orElseThrow(() -> new GatewayException("Cliente não encontrado: id=" + os.getCliente().getId()));

            VeiculoDatabase veiculo = veiculoRepository.findByIdAndClienteId(os.getVeiculo().getId(), cliente.getId())
                    .orElseThrow(() -> new GatewayException("Veículo não encontrado: id=" + os.getVeiculo().getId()));

            OrdemDeServicoDatabase osDatabase = OrdemDeServicoDatabase.fromDomain(os, cliente, veiculo);

            return ordemDeServicoRepository.save(osDatabase).toDomain();
        } catch (DataIntegrityViolationException e) {
            String cause = e.getMostSpecificCause().getMessage().toLowerCase();
            if (cause.contains("tagchave") || cause.contains("tag_chave")) {
                throw new GatewayException("tagChave já cadastrada em outra OS ativa", 409);
            }
            throw new GatewayException("Violação de integridade ao cadastrar OS", 409);
        } catch (Exception e) {
            throw new GatewayException("Falha ao cadastrar OS, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}