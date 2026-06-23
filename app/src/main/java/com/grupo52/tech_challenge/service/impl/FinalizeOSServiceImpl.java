package com.grupo52.tech_challenge.service.impl;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.InsumoOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.domain.PecaOS;
import com.grupo52.tech_challenge.domain.ServicoOS;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.UpdateInsumoGateway;
import com.grupo52.tech_challenge.gateway.UpdateOSGateway;
import com.grupo52.tech_challenge.gateway.UpdatePecaGateway;
import com.grupo52.tech_challenge.service.FinalizeOSService;
import com.grupo52.tech_challenge.service.UpdateOSStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinalizeOSServiceImpl implements FinalizeOSService {

    @Autowired
    private FindOSGateway findOSGateway;

    @Autowired
    private UpdateOSStatusService updateOSStatusService;

    @Autowired
    private UpdateOSGateway updateOSGateway;

    @Autowired
    private UpdatePecaGateway updatePecaGateway;

    @Autowired
    private UpdateInsumoGateway updateInsumoGateway;

    @Override
    public OrdemDeServico execute(Long osId) throws GatewayException, ValidationException, ServiceException {
        try {
            OrdemDeServico os = findOSGateway.execute(osId);
            updateOSStatusService.execute(os, StatusOS.FINALIZADA);
            consumeApprovedStock(os);
            return updateOSGateway.execute(os);
        } catch (ValidationException | GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Falha inesperada ao finalizar OS id=" + osId + ": " + e.getLocalizedMessage(), e);
        }
    }

    private void consumeApprovedStock(OrdemDeServico os) throws GatewayException {
        consumeApprovedServicos(os.getServicosDesejados());
        consumeApprovedServicos(os.getServicosNecessarios());
        consumeApprovedServicos(os.getServicosAdicionais());
    }

    private void consumeApprovedServicos(List<ServicoOS> servicos) throws GatewayException {
        for (ServicoOS servicoOS : servicos) {
            if (Boolean.TRUE.equals(servicoOS.getAprovado())) {
                consumeServicoOS(servicoOS);
            }
        }
    }

    private void consumeServicoOS(ServicoOS servicoOS) throws GatewayException {
        for (PecaOS pecaOS : servicoOS.getPecas()) {
            pecaOS.getPeca().removerEstoqueReservado(pecaOS.getQuantidade());
            pecaOS.getPeca().removerEstoque(pecaOS.getQuantidade());
            updatePecaGateway.execute(pecaOS.getPeca());
        }
        for (InsumoOS insumoOS : servicoOS.getInsumos()) {
            insumoOS.getInsumo().removerEstoqueReservado(insumoOS.getQuantidade());
            insumoOS.getInsumo().removerEstoque(insumoOS.getQuantidade());
            updateInsumoGateway.execute(insumoOS.getInsumo());
        }
    }
}