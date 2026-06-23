package com.grupo52.tech_challenge.service.impl;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.domain.ServicoOS;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.service.ApproveOSService;
import com.grupo52.tech_challenge.service.CalculateOSPriceService;
import com.grupo52.tech_challenge.service.UpdateOSStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApproveOSServiceImpl implements ApproveOSService {

    @Autowired
    private FindOSGateway findOSGateway;

    @Autowired
    private UpdateOSStatusService updateOSStatusService;

    @Autowired
    private CalculateOSPriceService calculateOSPriceService;

    @Override
    public OrdemDeServico approveAll(Long osId) throws GatewayException, ValidationException, ServiceException {
        try {
            OrdemDeServico os = findOSGateway.execute(osId);
            updateOSStatusService.execute(os, StatusOS.APROVADA);
            os.getServicosDesejados().forEach(servicoOS -> servicoOS.setAprovado(true));
            os.getServicosNecessarios().forEach(servicoOS -> servicoOS.setAprovado(true));
            os.getServicosAdicionais().forEach(servicoOS -> servicoOS.setAprovado(true));

            return calculateOSPriceService.calculateApprovedPrice(os);
        } catch (ValidationException | GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Falha inesperada ao aprovar OS: " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public OrdemDeServico parcialApprove(Long osId, List<Long> servicosAprovados) throws GatewayException, ValidationException, ServiceException {
        try {
            OrdemDeServico os = findOSGateway.execute(osId);
            updateOSStatusService.execute(os, StatusOS.APROVADA);

            for (ServicoOS servicoOS : os.getServicosDesejados()) {
                servicoOS.setAprovado(servicosAprovados.contains(servicoOS.getId()));
            }
            for (ServicoOS servicoOS : os.getServicosNecessarios()) {
                servicoOS.setAprovado(servicosAprovados.contains(servicoOS.getId()));
            }
            for (ServicoOS servicoOS : os.getServicosAdicionais()) {
                servicoOS.setAprovado(servicosAprovados.contains(servicoOS.getId()));
            }

            return calculateOSPriceService.calculateApprovedPrice(os);
        } catch (ValidationException | GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Falha inesperada ao aprovar OS parcialmente: " + e.getLocalizedMessage(), e);
        }
    }
}