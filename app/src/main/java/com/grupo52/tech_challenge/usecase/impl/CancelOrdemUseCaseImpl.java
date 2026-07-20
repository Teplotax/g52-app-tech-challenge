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
import com.grupo52.tech_challenge.usecase.CancelOrdemUseCase;
import com.grupo52.tech_challenge.usecase.UpdateOrdemStatusUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CancelOrdemUseCaseImpl implements CancelOrdemUseCase {

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
    public Ordem execute(Long osId) throws GatewayException, UseCaseException, ValidationException {
        try {
            Ordem os = findOrdemGateway.execute(osId);
            Status statusAtual = os.getStatus();

            updateOrdemStatusUseCase.execute(os, Status.CANCELADA);

            switch (statusAtual) {
                case RECEBIDA, EM_DIAGNOSTICO, AGUARDANDO_APROVACAO -> releaseAll(os);
                case APROVADA, EM_EXECUCAO, FINALIZADA -> releaseApproved(os);
            }

            return updateOrdemGateway.execute(os);
        } catch (ValidationException | GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new UseCaseException("Falha inesperada ao cancelar OS id=" + osId + ": " + e.getLocalizedMessage(), e);
        }
    }

    private void releaseAll(Ordem os) throws GatewayException {
        releaseServicos(os.getServicosDesejados());
        releaseServicos(os.getServicosNecessarios());
        releaseServicos(os.getServicosAdicionais());
    }

    private void releaseApproved(Ordem os) throws GatewayException {
        releaseApprovedServicos(os.getServicosDesejados());
        releaseApprovedServicos(os.getServicosNecessarios());
        releaseApprovedServicos(os.getServicosAdicionais());
    }

    private void releaseServicos(List<OrdemServico> servicos) throws GatewayException {
        for (OrdemServico ordemServico : servicos) {
            releaseServicoOS(ordemServico);
        }
    }

    private void releaseApprovedServicos(List<OrdemServico> servicos) throws GatewayException {
        for (OrdemServico ordemServico : servicos) {
            if (Boolean.TRUE.equals(ordemServico.getAprovado())) {
                releaseServicoOS(ordemServico);
            }
        }
    }

    private void releaseServicoOS(OrdemServico ordemServico) throws GatewayException {
        for (OrdemPeca ordemPeca : ordemServico.getPecas()) {
            ordemPeca.getPeca().removerEstoqueReservado(ordemPeca.getQuantidade());
            updatePecaGateway.execute(ordemPeca.getPeca());
        }
        for (OrdemInsumo ordemInsumo : ordemServico.getInsumos()) {
            ordemInsumo.getInsumo().removerEstoqueReservado(ordemInsumo.getQuantidade());
            updateInsumoGateway.execute(ordemInsumo.getInsumo());
        }
    }
}