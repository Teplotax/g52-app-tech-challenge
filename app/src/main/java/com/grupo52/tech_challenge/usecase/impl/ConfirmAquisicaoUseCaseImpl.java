package com.grupo52.tech_challenge.usecase.impl;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.OrdemInsumo;
import com.grupo52.tech_challenge.domain.OrdemPeca;
import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.gateway.UpdateInsumoGateway;
import com.grupo52.tech_challenge.gateway.UpdateOrdemGateway;
import com.grupo52.tech_challenge.gateway.UpdatePecaGateway;
import com.grupo52.tech_challenge.usecase.ConfirmAquisicaoUseCase;
import com.grupo52.tech_challenge.usecase.UpdateOrdemStatusUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfirmAquisicaoUseCaseImpl implements ConfirmAquisicaoUseCase {

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

            for (OrdemPeca ordemPeca : os.getPecasNaoReservadas()) {
                Peca peca = ordemPeca.getPeca();
                peca.adicionarEstoque(ordemPeca.getQuantidade());
                peca.adicionarEstoqueReservado(ordemPeca.getQuantidade());
                updatePecaGateway.execute(peca);
                ordemPeca.setReservado(true);
            }
            for (OrdemInsumo ordemInsumo : os.getInsumosNaoReservados()) {
                Insumo insumo = ordemInsumo.getInsumo();
                insumo.adicionarEstoque(ordemInsumo.getQuantidade());
                insumo.adicionarEstoqueReservado(ordemInsumo.getQuantidade());
                updateInsumoGateway.execute(insumo);
                ordemInsumo.setReservado(true);
            }

            updateOrdemStatusUseCase.execute(os, Status.APROVADA);

            return updateOrdemGateway.execute(os);
        } catch (ValidationException | GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new UseCaseException("Falha inesperada ao confirmar aquisição da OS id=" + osId + ": " + e.getLocalizedMessage(), e);
        }
    }
}