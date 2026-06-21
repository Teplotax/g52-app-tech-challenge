package com.grupo52.tech_challenge.service.impl;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.UpdateOSGateway;
import com.grupo52.tech_challenge.service.AddServicosService;
import com.grupo52.tech_challenge.service.CalculateOSPriceService;
import com.grupo52.tech_challenge.service.UpdateOSStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddServicosServiceImpl implements AddServicosService {

    @Autowired
    private FindOSGateway findOSGateway;

    @Autowired
    private UpdateOSGateway updateOSGateway;

    @Autowired
    private CalculateOSPriceService calculateOSPriceService;

    @Autowired
    private UpdateOSStatusService updateOSStatusService;


    @Override
    public OrdemDeServico execute(OrdemDeServico os) throws GatewayException, ValidationException {

        try {
            OrdemDeServico updatedOS = updateOS(os);

            return calculateOSPriceService.calculateServicosAdicionais(calculateOSPriceService.calculateServicosNecessarios(updatedOS));
        } catch (ValidationException | GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new GatewayException(e.getClass().getSimpleName());
        }
    }

    private OrdemDeServico updateOS(OrdemDeServico os) throws GatewayException, ValidationException {
        OrdemDeServico savedOS = findOSGateway.execute(os.getId());
        if(savedOS.getStatus() != StatusOS.EM_DIAGNOSTICO) {
            if(savedOS.getStatus() == StatusOS.RECEBIDA) {
                updateOSStatusService.execute(savedOS, StatusOS.EM_DIAGNOSTICO);
            } else {
                throw new InvalidStatusException("Não é permitido adicionar serviços no status " + savedOS.getStatus());
            }
        }

        savedOS.setJustificativaNecessarios(
                savedOS.getJustificativaNecessarios() != null && !savedOS.getJustificativaNecessarios().isBlank()
                        ? savedOS.getJustificativaNecessarios() + " \n " + os.getJustificativaNecessarios()
                        : os.getJustificativaNecessarios()
        );
        savedOS.setJustificativaAdicionais(
                savedOS.getJustificativaAdicionais() != null && !savedOS.getJustificativaAdicionais().isBlank()
                        ? savedOS.getJustificativaAdicionais() + " \n " + os.getJustificativaAdicionais()
                        : os.getJustificativaAdicionais()
        );
        savedOS.getServicosNecessarios().addAll(os.getServicosNecessarios());
        savedOS.getServicosAdicionais().addAll(os.getServicosAdicionais());

        return updateOSGateway.execute(savedOS);
    }
}