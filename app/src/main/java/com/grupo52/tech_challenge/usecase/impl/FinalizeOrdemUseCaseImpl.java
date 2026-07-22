package com.grupo52.tech_challenge.usecase.impl;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.OrdemInsumo;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.OrdemPeca;
import com.grupo52.tech_challenge.domain.OrdemServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.gateway.UpdateInsumoGateway;
import com.grupo52.tech_challenge.gateway.UpdateOrdemGateway;
import com.grupo52.tech_challenge.gateway.UpdatePecaGateway;
import com.grupo52.tech_challenge.usecase.FinalizeOrdemUseCase;
import com.grupo52.tech_challenge.usecase.UpdateOrdemStatusUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinalizeOrdemUseCaseImpl implements FinalizeOrdemUseCase {

    @Autowired
    private FindOrdemGateway findOrdemGateway;

    @Autowired
    private UpdateOrdemStatusUseCase updateOrdemStatusUseCase;

    @Autowired
    private UpdateOrdemGateway updateOrdemGateway;

    @Autowired
    private UpdatePecaGateway updatePecaGateway;

    @Autowired
    private UpdateInsumoGateway updateInsumoGateway;

    @Override
    public Ordem execute(Long osId) throws GatewayException, ValidationException, UseCaseException {
        try {
            Ordem os = findOrdemGateway.execute(osId);
            updateOrdemStatusUseCase.execute(os, Status.FINALIZADA);
            consumeApprovedStock(os);
            return updateOrdemGateway.execute(os);
        } catch (ValidationException | GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new UseCaseException("Falha inesperada ao finalizar OS id=" + osId + ": " + e.getLocalizedMessage(), e);
        }
    }

    private void consumeApprovedStock(Ordem os) throws GatewayException {
        consumeApprovedServicos(os.getServicosDesejados());
        consumeApprovedServicos(os.getServicosNecessarios());
        consumeApprovedServicos(os.getServicosAdicionais());
    }

    private void consumeApprovedServicos(List<OrdemServico> servicos) throws GatewayException {
        for (OrdemServico ordemServico : servicos) {
            if (Boolean.TRUE.equals(ordemServico.getAprovado())) {
                consumeServicoOS(ordemServico);
            }
        }
    }

    private void consumeServicoOS(OrdemServico ordemServico) throws GatewayException {
        for (OrdemPeca ordemPeca : ordemServico.getPecas()) {
            if (Boolean.TRUE.equals(ordemPeca.getReservado())) {
                ordemPeca.getPeca().removerEstoqueReservado(ordemPeca.getQuantidade());
                ordemPeca.getPeca().removerEstoque(ordemPeca.getQuantidade());
                updatePecaGateway.execute(ordemPeca.getPeca());
                ordemPeca.setReservado(false);
            }
        }
        for (OrdemInsumo ordemInsumo : ordemServico.getInsumos()) {
            if (Boolean.TRUE.equals(ordemInsumo.getReservado())) {
                ordemInsumo.getInsumo().removerEstoqueReservado(ordemInsumo.getQuantidade());
                ordemInsumo.getInsumo().removerEstoque(ordemInsumo.getQuantidade());
                updateInsumoGateway.execute(ordemInsumo.getInsumo());
                ordemInsumo.setReservado(false);
            }
        }
    }
}