package com.grupo52.tech_challenge.gateway.impl;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.CreateOSGateway;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.OrdemDeServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.StatusChangeDatabase;
import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import com.grupo52.tech_challenge.gateway.database.repository.OrdemDeServicoRepository;
import com.grupo52.tech_challenge.gateway.database.repository.StatusChangeRepository;
import com.grupo52.tech_challenge.gateway.database.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateOSGatewayImpl implements CreateOSGateway {

    private final OrdemDeServicoRepository ordemDeServicoRepository;
    private final StatusChangeRepository statusChangeRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;

    @Override
    public OrdemDeServico execute(OrdemDeServico os) throws GatewayException {
        try {
            os.setStatus(StatusOS.RECEBIDA);

            ClienteDatabase cliente = clienteRepository.findById(os.getCliente().getId())
                    .orElseThrow(() -> new GatewayException("Cliente não encontrado: id=" + os.getCliente().getId()));

            VeiculoDatabase veiculo = veiculoRepository.findByIdAndClienteId(os.getVeiculo().getId(), cliente.getId())
                    .orElseThrow(() -> new GatewayException("Veículo não encontrado: id=" + os.getVeiculo().getId()));

            OrdemDeServicoDatabase osDatabase = OrdemDeServicoDatabase.fromDomain(os, cliente, veiculo);
            OrdemDeServicoDatabase savedOs = ordemDeServicoRepository.save(osDatabase);

            List<StatusChangeDatabase> statusChanges = os.getHistorico().stream()
                    .map(sc -> StatusChangeDatabase.fromDomain(sc, savedOs))
                    .toList();
            statusChangeRepository.saveAll(statusChanges);

            return ordemDeServicoRepository.findById(savedOs.getId())
                    .orElseThrow(() -> new GatewayException("Falha ao recuperar OS após criação"))
                    .toDomain();
        } catch (DataIntegrityViolationException e) {
            throw new GatewayException("Falha ao cadastrar OS", 409);
        } catch (Exception e) {
            throw new GatewayException("Falha ao cadastrar OS, cause: " + e.getClass().getSimpleName(), e);
        }
    }
}