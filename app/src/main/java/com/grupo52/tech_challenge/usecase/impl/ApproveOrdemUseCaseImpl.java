package com.grupo52.tech_challenge.usecase.impl;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.OrdemServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.usecase.ApproveOrdemUseCase;
import com.grupo52.tech_challenge.usecase.CalculateOrdemPriceUseCase;
import com.grupo52.tech_challenge.usecase.UpdateOrdemStatusUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApproveOrdemUseCaseImpl implements ApproveOrdemUseCase {

    @Autowired
    private FindOrdemGateway findOrdemGateway;

    @Autowired
    private UpdateOrdemStatusUseCase updateOrdemStatusUseCase;

    @Autowired
    private CalculateOrdemPriceUseCase calculateOrdemPriceUseCase;

    @Override
    public Ordem approveAll(Long osId) throws GatewayException, ValidationException, UseCaseException {
        try {
            Ordem os = findOrdemGateway.execute(osId);
            updateOrdemStatusUseCase.execute(os, Status.APROVADA);
            os.getServicosDesejados().forEach(servicoOS -> servicoOS.setAprovado(true));
            os.getServicosNecessarios().forEach(servicoOS -> servicoOS.setAprovado(true));
            os.getServicosAdicionais().forEach(servicoOS -> servicoOS.setAprovado(true));

            return calculateOrdemPriceUseCase.calculateApprovedPrice(os);
        } catch (ValidationException | GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new UseCaseException("Falha inesperada ao aprovar OS: " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public Ordem parcialApprove(Long osId, List<Long> servicosAprovados) throws GatewayException, ValidationException, UseCaseException {
        try {
            Ordem os = findOrdemGateway.execute(osId);
            updateOrdemStatusUseCase.execute(os, Status.APROVADA);

            for (OrdemServico ordemServico : os.getServicosDesejados()) {
                ordemServico.setAprovado(servicosAprovados.contains(ordemServico.getId()));
            }
            for (OrdemServico ordemServico : os.getServicosNecessarios()) {
                ordemServico.setAprovado(servicosAprovados.contains(ordemServico.getId()));
            }
            for (OrdemServico ordemServico : os.getServicosAdicionais()) {
                ordemServico.setAprovado(servicosAprovados.contains(ordemServico.getId()));
            }

            return calculateOrdemPriceUseCase.calculateApprovedPrice(os);
        } catch (ValidationException | GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new UseCaseException("Falha inesperada ao aprovar OS parcialmente: " + e.getLocalizedMessage(), e);
        }
    }
}