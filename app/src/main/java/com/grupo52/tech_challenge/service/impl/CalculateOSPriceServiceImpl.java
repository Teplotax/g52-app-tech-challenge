package com.grupo52.tech_challenge.service.impl;

import com.grupo52.tech_challenge.domain.*;
import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.gateway.*;
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

    @Autowired
    private UpdatePecaGateway updatePecaGateway;

    @Autowired
    private UpdateInsumoGateway updateInsumoGateway;

    @Override
    public OrdemDeServico calculateServicosDesejados(OrdemDeServico os) throws GatewayException, ServiceException {

        try {
            for (ServicoOS servico : os.getServicosDesejados()) {
                calculate(servico, os.getVeiculo());
                os.setPrecoServicosDesejados(os.getPrecoServicosDesejados().add(servico.getPrecoTotal()));
            }

            os.setPrecoTotal(os.getPrecoServicosDesejados().add(os.getPrecoServicosNecessarios()).add(os.getPrecoServicosAdicionais()));

            return updateOSGateway.execute(os);
        } catch (GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Falha inesperada calcular serviços desejados", e);
        }
    }

    @Override
    public OrdemDeServico calculateServicosNecessarios(OrdemDeServico os) throws GatewayException, ServiceException {

        try {
            for (ServicoOS servico : os.getServicosNecessarios()) {
                calculate(servico, os.getVeiculo());
                os.setPrecoServicosNecessarios(os.getPrecoServicosNecessarios().add(servico.getPrecoTotal()));
            }

            os.setPrecoTotal(os.getPrecoServicosDesejados().add(os.getPrecoServicosNecessarios()).add(os.getPrecoServicosAdicionais()));

            return updateOSGateway.execute(os);

        } catch (GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Falha inesperada calcular serviços necessários", e);
        }
    }

    @Override
    public OrdemDeServico calculateServicosAdicionais(OrdemDeServico os) throws GatewayException, ServiceException {

        try {
            for (ServicoOS servico : os.getServicosAdicionais()) {
                calculate(servico, os.getVeiculo());
                os.setPrecoServicosAdicionais(os.getPrecoServicosAdicionais().add(servico.getPrecoTotal()));
            }
            os.setPrecoTotal(os.getPrecoServicosDesejados().add(os.getPrecoServicosNecessarios()).add(os.getPrecoServicosAdicionais()));

            return updateOSGateway.execute(os);

        } catch (GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Falha inesperada calcular serviços adicionais", e);
        }
    }

    @Override
    public OrdemDeServico calculateApprovedPrice(OrdemDeServico os) throws GatewayException, ServiceException {

        try {
            BigDecimal approvedPrice = BigDecimal.ZERO;

            for (ServicoOS servico : os.getServicosDesejados()) {
                if (servico.getAprovado()) {
                    reserveProdutos(servico);
                    approvedPrice = approvedPrice.add(servico.getPrecoTotal());
                }
            }
            for (ServicoOS servico : os.getServicosNecessarios()) {
                if (servico.getAprovado()) {
                    reserveProdutos(servico);
                    approvedPrice = approvedPrice.add(servico.getPrecoTotal());
                }
            }
            for (ServicoOS servico : os.getServicosAdicionais()) {
                if (servico.getAprovado()) {
                    reserveProdutos(servico);
                    approvedPrice = approvedPrice.add(servico.getPrecoTotal());
                }
            }

            os.setPrecoTotalAprovado(approvedPrice);

            return updateOSGateway.execute(os);

        } catch (GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Falha inesperada calcular preço aprovado: " + e.getMessage(), e);
        }
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

    private void reserveProdutos(ServicoOS servicoOS) throws GatewayException {
        for(PecaOS pecaOS : servicoOS.getPecas()) {
            Peca peca = pecaOS.getPeca();
            System.out.println("PecaId: " + peca.getId());
            System.out.println("Nome: " + peca.getNome());
            System.out.println("Estoque: " + peca.getEstoque());
            System.out.println("Estoque reservado: " + peca.getEstoqueReservado());

            peca.adicionarEstoqueReservado(pecaOS.getQuantidade());

            System.out.println("Estoque reservado: " + peca.getEstoqueReservado());

            updatePecaGateway.execute(peca);
        }
        for(InsumoOS insumoOS : servicoOS.getInsumos()) {
            Insumo insumo = insumoOS.getInsumo();
            insumo.adicionarEstoqueReservado(insumoOS.getQuantidade());


            updateInsumoGateway.execute(insumo);
        }
    }
}