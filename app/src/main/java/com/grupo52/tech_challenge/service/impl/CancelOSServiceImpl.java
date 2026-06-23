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
import com.grupo52.tech_challenge.service.CancelOSService;
import com.grupo52.tech_challenge.service.UpdateOSStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CancelOSServiceImpl implements CancelOSService {

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
    public OrdemDeServico execute(Long osId) throws GatewayException, ServiceException, ValidationException {
        try {
            OrdemDeServico os = findOSGateway.execute(osId);
            StatusOS statusAtual = os.getStatus();

            updateOSStatusService.execute(os, StatusOS.CANCELADA);

            switch (statusAtual) {
                case RECEBIDA, EM_DIAGNOSTICO, AGUARDANDO_APROVACAO -> releaseAll(os);
                case APROVADA, EM_EXECUCAO, FINALIZADA -> releaseApproved(os);
            }

            return updateOSGateway.execute(os);
        } catch (ValidationException | GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Falha inesperada ao cancelar OS id=" + osId + ": " + e.getLocalizedMessage(), e);
        }
    }

    private void releaseAll(OrdemDeServico os) throws GatewayException {
        releaseServicos(os.getServicosDesejados());
        releaseServicos(os.getServicosNecessarios());
        releaseServicos(os.getServicosAdicionais());
    }

    private void releaseApproved(OrdemDeServico os) throws GatewayException {
        releaseApprovedServicos(os.getServicosDesejados());
        releaseApprovedServicos(os.getServicosNecessarios());
        releaseApprovedServicos(os.getServicosAdicionais());
    }

    private void releaseServicos(List<ServicoOS> servicos) throws GatewayException {
        for (ServicoOS servicoOS : servicos) {
            releaseServicoOS(servicoOS);
        }
    }

    private void releaseApprovedServicos(List<ServicoOS> servicos) throws GatewayException {
        for (ServicoOS servicoOS : servicos) {
            if (Boolean.TRUE.equals(servicoOS.getAprovado())) {
                releaseServicoOS(servicoOS);
            }
        }
    }

    private void releaseServicoOS(ServicoOS servicoOS) throws GatewayException {
        for (PecaOS pecaOS : servicoOS.getPecas()) {
            pecaOS.getPeca().removerEstoqueReservado(pecaOS.getQuantidade());
            updatePecaGateway.execute(pecaOS.getPeca());
        }
        for (InsumoOS insumoOS : servicoOS.getInsumos()) {
            insumoOS.getInsumo().removerEstoqueReservado(insumoOS.getQuantidade());
            updateInsumoGateway.execute(insumoOS.getInsumo());
        }
    }
}