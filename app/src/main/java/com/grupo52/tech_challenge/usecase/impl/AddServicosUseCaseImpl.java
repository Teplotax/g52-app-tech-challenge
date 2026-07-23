package com.grupo52.tech_challenge.usecase.impl;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.gateway.UpdateOrdemGateway;
import com.grupo52.tech_challenge.usecase.AddServicosUseCase;
import com.grupo52.tech_challenge.usecase.CalculateOrdemPriceUseCase;
import com.grupo52.tech_challenge.usecase.UpdateOrdemStatusUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddServicosUseCaseImpl implements AddServicosUseCase {

    @Autowired
    private FindOrdemGateway findOrdemGateway;

    @Autowired
    private UpdateOrdemGateway updateOrdemGateway;

    @Autowired
    private CalculateOrdemPriceUseCase calculateOrdemPriceUseCase;

    @Autowired
    private UpdateOrdemStatusUseCase updateOrdemStatusUseCase;


    @Override
    public Ordem execute(Ordem os) throws GatewayException, ValidationException, UseCaseException {

        try {
            Ordem updatedOS = updateOS(os);

            return calculateOrdemPriceUseCase.calculateServicosAdicionais(calculateOrdemPriceUseCase.calculateServicosNecessarios(updatedOS));
        } catch (ValidationException | GatewayException | UseCaseException e) {
            throw e;
        } catch (Exception e) {
            throw new UseCaseException("Falha inesperada ao adicionar serviços à OS", e);
        }
    }

    private Ordem updateOS(Ordem os) throws GatewayException, ValidationException, UseCaseException {
        Ordem savedOS = findOrdemGateway.execute(os.getId());
        if(savedOS.getStatus() != Status.EM_DIAGNOSTICO) {
            if(savedOS.getStatus() == Status.RECEBIDA) {
                updateOrdemStatusUseCase.execute(savedOS, Status.EM_DIAGNOSTICO);
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

        return updateOrdemGateway.execute(savedOS);
    }
}