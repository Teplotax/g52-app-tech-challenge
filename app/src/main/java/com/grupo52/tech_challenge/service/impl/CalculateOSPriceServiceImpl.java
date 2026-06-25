package com.grupo52.tech_challenge.service.impl;

import com.grupo52.tech_challenge.domain.*;
import com.grupo52.tech_challenge.domain.Enums.ComplexidadeOS;
import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.ServiceException;
import com.grupo52.tech_challenge.gateway.*;
import com.grupo52.tech_challenge.service.CalculateOSPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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
                reserveProdutos(servico);
                os.setPrecoServicosDesejados(os.getPrecoServicosDesejados().add(servico.getPrecoTotal()));
            }

            os.setPrecoTotal(os.getPrecoServicosDesejados().add(os.getPrecoServicosNecessarios()).add(os.getPrecoServicosAdicionais()));
            updateComplexidade(os);

            return updateOSGateway.execute(os);
        } catch (GatewayException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Falha inesperada calcular serviços desejados: " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public OrdemDeServico calculateServicosNecessarios(OrdemDeServico os) throws GatewayException, ServiceException {
        try {
            for (ServicoOS servico : os.getServicosNecessarios()) {
                calculate(servico, os.getVeiculo());
                reserveProdutos(servico);
                os.setPrecoServicosNecessarios(os.getPrecoServicosNecessarios().add(servico.getPrecoTotal()));
            }

            os.setPrecoTotal(os.getPrecoServicosDesejados().add(os.getPrecoServicosNecessarios()).add(os.getPrecoServicosAdicionais()));
            updateComplexidade(os);

            return updateOSGateway.execute(os);
        } catch (GatewayException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Falha inesperada calcular serviços necessários" + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public OrdemDeServico calculateServicosAdicionais(OrdemDeServico os) throws GatewayException, ServiceException {
        try {
            for (ServicoOS servico : os.getServicosAdicionais()) {
                calculate(servico, os.getVeiculo());
                reserveProdutos(servico);
                os.setPrecoServicosAdicionais(os.getPrecoServicosAdicionais().add(servico.getPrecoTotal()));
            }
            os.setPrecoTotal(os.getPrecoServicosDesejados().add(os.getPrecoServicosNecessarios()).add(os.getPrecoServicosAdicionais()));
            updateComplexidade(os);

            return updateOSGateway.execute(os);
        } catch (GatewayException | ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Falha inesperada calcular serviços adicionais" + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public OrdemDeServico calculateApprovedPrice(OrdemDeServico os) throws GatewayException, ServiceException {
        try {
            BigDecimal precoDesejados = BigDecimal.ZERO;
            BigDecimal precoNecessarios = BigDecimal.ZERO;
            BigDecimal precoAdicionais = BigDecimal.ZERO;
            BigDecimal horasTecnicas = BigDecimal.ZERO;

            for (ServicoOS servico : os.getServicosDesejados()) {
                if (servico.getAprovado()) {
                    horasTecnicas = horasTecnicas.add(servico.getServico().getHorasTecnicas());
                    precoDesejados = precoDesejados.add(servico.getPrecoTotal());
                } else {
                    releaseReservedProdutos(servico);
                }
            }
            for (ServicoOS servico : os.getServicosNecessarios()) {
                if (servico.getAprovado()) {
                    horasTecnicas = horasTecnicas.add(servico.getServico().getHorasTecnicas());
                    precoNecessarios = precoNecessarios.add(servico.getPrecoTotal());
                } else {
                    releaseReservedProdutos(servico);
                }
            }
            for (ServicoOS servico : os.getServicosAdicionais()) {
                if (servico.getAprovado()) {
                    horasTecnicas = horasTecnicas.add(servico.getServico().getHorasTecnicas());
                    precoAdicionais = precoAdicionais.add(servico.getPrecoTotal());
                } else {
                    releaseReservedProdutos(servico);
                }
            }

            os.setPrecoServicosDesejados(precoDesejados);
            os.setPrecoServicosNecessarios(precoNecessarios);
            os.setPrecoServicosAdicionais(precoAdicionais);
            os.setPrecoTotal(precoDesejados.add(precoNecessarios).add(precoAdicionais));
            os.setComplexidade(getComplexidade(horasTecnicas));

            return updateOSGateway.execute(os);
        } catch (GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Falha inesperada calcular preço aprovado: " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    private void calculate(ServicoOS servicoOS, Veiculo veiculo) throws GatewayException, ServiceException {
        BigDecimal precoTotalOS = BigDecimal.ZERO;

        Servico servico = findServicoGateway.execute(servicoOS.getServico().getId());

        servicoOS.setServico(servico);
        servicoOS.setPrecoHorasTecnicas(servico.getHorasTecnicas().multiply(PRECO_HORA));

        precoTotalOS = precoTotalOS.add(servicoOS.getPrecoHorasTecnicas());

        for (Servico.ServicoTipoPeca servicoTipoPeca : servico.getPecas()) {
            Integer quantidade = servicoTipoPeca.getQuantidade();

            List<Peca> pecasDisponiveis = findPecaByVeiculoGateway.execute(servicoTipoPeca.getTipoPeca(), veiculo);
            Peca peca = pecasDisponiveis.stream()
                    .filter(p -> (p.getEstoque() - p.getEstoqueReservado()) >= quantidade)
                    .findFirst()
                    .orElseThrow(() -> new ServiceException(
                            "Estoque insuficiente para peça do tipo " + servicoTipoPeca.getTipoPeca()
                                    + ". Seleção automática entre múltiplos fornecedores está no backlog.", 422));

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
            List<Insumo> insumosDisponiveis = findInsumoByVeiculoGateway.execute(tipoInsumo, veiculo);
            Insumo insumo = insumosDisponiveis.stream()
                    .filter(i -> {
                        AplicacaoProduto ap = i.getAplicacoes().stream()
                                .filter(a -> a.getModelo() != null && veiculo.getModelo().getId().equals(a.getModelo().getId()))
                                .findFirst().orElse(null);
                        return ap != null && (i.getEstoque() - i.getEstoqueReservado()) >= ap.getQuantidade();
                    })
                    .findFirst()
                    .orElseThrow(() -> new ServiceException(
                            "Estoque insuficiente para insumo do tipo " + tipoInsumo
                                    + ". Seleção automática entre múltiplos fornecedores está no backlog.", 422));

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
        for (PecaOS pecaOS : servicoOS.getPecas()) {
            Peca peca = pecaOS.getPeca();
            peca.adicionarEstoqueReservado(pecaOS.getQuantidade());
            updatePecaGateway.execute(peca);
        }
        for (InsumoOS insumoOS : servicoOS.getInsumos()) {
            Insumo insumo = insumoOS.getInsumo();
            insumo.adicionarEstoqueReservado(insumoOS.getQuantidade());
            updateInsumoGateway.execute(insumo);
        }
    }

    private void releaseReservedProdutos(ServicoOS servicoOS) throws GatewayException {
        for (PecaOS pecaOS : servicoOS.getPecas()) {
            Peca peca = pecaOS.getPeca();
            peca.removerEstoqueReservado(pecaOS.getQuantidade());
            updatePecaGateway.execute(peca);
        }
        for (InsumoOS insumoOS : servicoOS.getInsumos()) {
            Insumo insumo = insumoOS.getInsumo();
            insumo.removerEstoqueReservado(insumoOS.getQuantidade());
            updateInsumoGateway.execute(insumo);
        }
    }

    private void updateComplexidade(OrdemDeServico os) {
        BigDecimal horasTecnicas = BigDecimal.ZERO;

        for (ServicoOS servico : os.getServicosDesejados()) {
            horasTecnicas = horasTecnicas.add(servico.getServico().getHorasTecnicas());
        }
        for (ServicoOS servico : os.getServicosNecessarios()) {
            horasTecnicas = horasTecnicas.add(servico.getServico().getHorasTecnicas());
        }
        for (ServicoOS servico : os.getServicosAdicionais()) {
            horasTecnicas = horasTecnicas.add(servico.getServico().getHorasTecnicas());
        }

        os.setComplexidade(getComplexidade(horasTecnicas));
    }

    private ComplexidadeOS getComplexidade(BigDecimal horasTecnicas) {
        if (horasTecnicas.compareTo(new BigDecimal("2")) <= 0) {
            return ComplexidadeOS.BAIXA;
        } else if (horasTecnicas.compareTo(new BigDecimal("6")) <= 0) {
            return ComplexidadeOS.MEDIA;
        } else {
            return ComplexidadeOS.ALTA;
        }
    }
}