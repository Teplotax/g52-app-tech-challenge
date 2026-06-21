package com.grupo52.tech_challenge.service.impl;

import com.grupo52.tech_challenge.domain.*;
import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.exception.ValidationException;
import com.grupo52.tech_challenge.gateway.FindInsumoByVeiculoGateway;
import com.grupo52.tech_challenge.gateway.FindPecaByVeiculoGateway;
import com.grupo52.tech_challenge.gateway.FindServicoGateway;
import com.grupo52.tech_challenge.gateway.UpdateOSGateway;
import com.grupo52.tech_challenge.service.CalculateOSPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CalculateOSPriceServiceImpl implements CalculateOSPriceService {

    private final BigDecimal PRECO_HORA = BigDecimal.valueOf(70.00);

    @Autowired
    private UpdateOSGateway updateOSGateway;

    @Autowired
    private FindServicoGateway findServicoGateway;

    @Autowired
    private FindPecaByVeiculoGateway findPecaByVeiculoGateway;

    @Autowired
    private FindInsumoByVeiculoGateway findInsumoByVeiculoGateway;

    @Override
    public OrdemDeServico calculateServicosDesejados(OrdemDeServico os) throws GatewayException, ServiceException {

        try {
            for (ServicoOS servico : os.getServicosDesejados()) {
                calculate(servico, os.getVeiculo());
                os.setPrecoServicosDesejados(os.getPrecoServicosDesejados().add(servico.getPrecoTotal()));
            }

            os.setPrecoTotal(os.getPrecoServicosDesejados().add(os.getPrecoServicosNecessarios()).add(os.getPrecoServicosAdicionais()));

        } catch (GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Falha inesperada calcular serviços desejados", e);
        }

        return updateOSGateway.execute(os);
    }

    @Override
    public OrdemDeServico calculateServicosNecessarios(OrdemDeServico os) throws GatewayException, ServiceException {

        try {
            for (ServicoOS servico : os.getServicosNecessarios()) {
                calculate(servico, os.getVeiculo());
                os.setPrecoServicosNecessarios(os.getPrecoServicosNecessarios().add(servico.getPrecoTotal()));
            }

            os.setPrecoTotal(os.getPrecoServicosDesejados().add(os.getPrecoServicosNecessarios()).add(os.getPrecoServicosAdicionais()));

        } catch (GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Falha inesperada calcular serviços necessários", e);
        }

        return updateOSGateway.execute(os);
    }

    @Override
    public OrdemDeServico calculateServicosAdicionais(OrdemDeServico os) throws GatewayException, ServiceException {

        try {
            for (ServicoOS servico : os.getServicosAdicionais()) {
                calculate(servico, os.getVeiculo());
                os.setPrecoServicosAdicionais(os.getPrecoServicosAdicionais().add(servico.getPrecoTotal()));
            }

            os.setPrecoTotal(os.getPrecoServicosDesejados().add(os.getPrecoServicosNecessarios()).add(os.getPrecoServicosAdicionais()));

        } catch (GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Falha inesperada calcular serviços adicionais", e);
        }

        return updateOSGateway.execute(os);
    }

    private void calculate(ServicoOS servicoOS, Veiculo veiculo) throws GatewayException {

        BigDecimal precoTotalOS = BigDecimal.ZERO;

        Servico servico = findServicoGateway.execute(servicoOS.getServico().getId());

        servicoOS.setPrecoHorasTecnicas(servico.getHorasTecnicas().multiply(PRECO_HORA));

        precoTotalOS = precoTotalOS.add(servicoOS.getPrecoHorasTecnicas());


        for (Servico.ServicoTipoPeca servicoTipoPeca : servico.getPecas()) {

            Peca peca = findPecaByVeiculoGateway.execute(servicoTipoPeca.getTipoPeca(), veiculo).getFirst();

            Integer quantidade = servicoTipoPeca.getQuantidade();
            BigDecimal precoPecas = peca.getPreco().multiply(BigDecimal.valueOf(quantidade));


            PecaOS pecaOS = PecaOS.builder()
                    .peca(peca)
                    .quantidade(quantidade)
                    .precoTotal(precoPecas)
                    .build();

            servicoOS.addPeca(pecaOS);


            precoTotalOS = precoTotalOS.add(precoPecas);

        }

        for (TipoInsumo tipoInsumo : servico.getInsumos()) {
            Insumo insumo = findInsumoByVeiculoGateway.execute(tipoInsumo, veiculo).getFirst();

            AplicacaoProduto aplicacao = insumo.getAplicacoes().stream()
                    .filter(ap -> ap.getModelo() != null
                            && veiculo.getModelo().getId().equals(ap.getModelo().getId()))
                    .toList().getFirst();


            BigDecimal precoInsumos = insumo.getPreco().multiply(BigDecimal.valueOf(aplicacao.getQuantidade()));
            InsumoOS insumoOS = InsumoOS.builder()
                    .insumo(insumo)
                    .quantidade(aplicacao.getQuantidade())
                    .precoTotal(precoInsumos)
                    .build();

            servicoOS.addInsumo(insumoOS);
            precoTotalOS = precoTotalOS.add(precoInsumos);
        }


        servicoOS.setPrecoTotal(precoTotalOS);
    }
}