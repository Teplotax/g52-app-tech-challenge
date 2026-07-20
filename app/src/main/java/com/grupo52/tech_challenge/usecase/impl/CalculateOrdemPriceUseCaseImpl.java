package com.grupo52.tech_challenge.usecase.impl;

import com.grupo52.tech_challenge.domain.*;
import com.grupo52.tech_challenge.domain.Enums.Complexidade;
import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.UseCaseException;
import com.grupo52.tech_challenge.gateway.*;
import com.grupo52.tech_challenge.usecase.CalculateOrdemPriceUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CalculateOrdemPriceUseCaseImpl implements CalculateOrdemPriceUseCase {

    private final BigDecimal PRECO_HORA = BigDecimal.valueOf(70.00);

    @Autowired
    private UpdateOrdemGateway updateOrdemGateway;

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
    public Ordem calculateServicosDesejados(Ordem os) throws GatewayException, UseCaseException {
        try {
            for (OrdemServico servico : os.getServicosDesejados()) {
                calculate(servico, os.getVeiculo());
                reserveProdutos(servico);
                os.setPrecoServicosDesejados(os.getPrecoServicosDesejados().add(servico.getPrecoTotal()));
            }

            os.setPrecoTotal(os.getPrecoServicosDesejados().add(os.getPrecoServicosNecessarios()).add(os.getPrecoServicosAdicionais()));
            updateComplexidade(os);

            return updateOrdemGateway.execute(os);
        } catch (GatewayException | UseCaseException e) {
            throw e;
        } catch (Exception e) {
            throw new UseCaseException("Falha inesperada calcular serviços desejados: " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Ordem calculateServicosNecessarios(Ordem os) throws GatewayException, UseCaseException {
        try {
            for (OrdemServico servico : os.getServicosNecessarios()) {
                calculate(servico, os.getVeiculo());
                reserveProdutos(servico);
                os.setPrecoServicosNecessarios(os.getPrecoServicosNecessarios().add(servico.getPrecoTotal()));
            }

            os.setPrecoTotal(os.getPrecoServicosDesejados().add(os.getPrecoServicosNecessarios()).add(os.getPrecoServicosAdicionais()));
            updateComplexidade(os);

            return updateOrdemGateway.execute(os);
        } catch (GatewayException | UseCaseException e) {
            throw e;
        } catch (Exception e) {
            throw new UseCaseException("Falha inesperada calcular serviços necessários" + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Ordem calculateServicosAdicionais(Ordem os) throws GatewayException, UseCaseException {
        try {
            for (OrdemServico servico : os.getServicosAdicionais()) {
                calculate(servico, os.getVeiculo());
                reserveProdutos(servico);
                os.setPrecoServicosAdicionais(os.getPrecoServicosAdicionais().add(servico.getPrecoTotal()));
            }
            os.setPrecoTotal(os.getPrecoServicosDesejados().add(os.getPrecoServicosNecessarios()).add(os.getPrecoServicosAdicionais()));
            updateComplexidade(os);

            return updateOrdemGateway.execute(os);
        } catch (GatewayException | UseCaseException e) {
            throw e;
        } catch (Exception e) {
            throw new UseCaseException("Falha inesperada calcular serviços adicionais" + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Ordem calculateApprovedPrice(Ordem os) throws GatewayException, UseCaseException {
        try {
            BigDecimal precoDesejados = BigDecimal.ZERO;
            BigDecimal precoNecessarios = BigDecimal.ZERO;
            BigDecimal precoAdicionais = BigDecimal.ZERO;
            BigDecimal horasTecnicas = BigDecimal.ZERO;

            for (OrdemServico servico : os.getServicosDesejados()) {
                if (servico.getAprovado()) {
                    horasTecnicas = horasTecnicas.add(servico.getServico().getHorasTecnicas());
                    precoDesejados = precoDesejados.add(servico.getPrecoTotal());
                } else {
                    releaseReservedProdutos(servico);
                }
            }
            for (OrdemServico servico : os.getServicosNecessarios()) {
                if (servico.getAprovado()) {
                    horasTecnicas = horasTecnicas.add(servico.getServico().getHorasTecnicas());
                    precoNecessarios = precoNecessarios.add(servico.getPrecoTotal());
                } else {
                    releaseReservedProdutos(servico);
                }
            }
            for (OrdemServico servico : os.getServicosAdicionais()) {
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

            return updateOrdemGateway.execute(os);
        } catch (GatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new UseCaseException("Falha inesperada calcular preço aprovado: " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    private void calculate(OrdemServico ordemServico, Veiculo veiculo) throws GatewayException, UseCaseException {
        BigDecimal precoTotalOS = BigDecimal.ZERO;

        Servico servico = findServicoGateway.execute(ordemServico.getServico().getId());

        ordemServico.setServico(servico);
        ordemServico.setPrecoHorasTecnicas(servico.getHorasTecnicas().multiply(PRECO_HORA));

        precoTotalOS = precoTotalOS.add(ordemServico.getPrecoHorasTecnicas());

        for (Servico.ServicoTipoPeca servicoTipoPeca : servico.getPecas()) {
            Integer quantidade = servicoTipoPeca.getQuantidade();

            List<Peca> pecasDisponiveis = findPecaByVeiculoGateway.execute(servicoTipoPeca.getTipoPeca(), veiculo);
            Peca peca = pecasDisponiveis.stream()
                    .filter(p -> (p.getEstoque() - p.getEstoqueReservado()) >= quantidade)
                    .findFirst()
                    .orElseThrow(() -> new UseCaseException(
                            "Estoque insuficiente para peça do tipo " + servicoTipoPeca.getTipoPeca()
                                    + ". Seleção automática entre múltiplos fornecedores está no backlog.", 422));

            BigDecimal precoPecas = peca.getPreco().multiply(BigDecimal.valueOf(quantidade));

            OrdemPeca ordemPeca = OrdemPeca.builder()
                    .peca(peca)
                    .quantidade(quantidade)
                    .precoTotal(precoPecas)
                    .build();

            ordemServico.addPeca(ordemPeca);
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
                    .orElseThrow(() -> new UseCaseException(
                            "Estoque insuficiente para insumo do tipo " + tipoInsumo
                                    + ". Seleção automática entre múltiplos fornecedores está no backlog.", 422));

            AplicacaoProduto aplicacao = insumo.getAplicacoes().stream()
                    .filter(ap -> ap.getModelo() != null
                            && veiculo.getModelo().getId().equals(ap.getModelo().getId()))
                    .toList().getFirst();

            BigDecimal precoInsumos = insumo.getPreco().multiply(BigDecimal.valueOf(aplicacao.getQuantidade()));
            OrdemInsumo ordemInsumo = OrdemInsumo.builder()
                    .insumo(insumo)
                    .quantidade(aplicacao.getQuantidade())
                    .precoTotal(precoInsumos)
                    .build();

            ordemServico.addInsumo(ordemInsumo);
            precoTotalOS = precoTotalOS.add(precoInsumos);
        }

        ordemServico.setPrecoTotal(precoTotalOS);
    }

    private void reserveProdutos(OrdemServico ordemServico) throws GatewayException {
        for (OrdemPeca ordemPeca : ordemServico.getPecas()) {
            Peca peca = ordemPeca.getPeca();
            peca.adicionarEstoqueReservado(ordemPeca.getQuantidade());
            updatePecaGateway.execute(peca);
        }
        for (OrdemInsumo ordemInsumo : ordemServico.getInsumos()) {
            Insumo insumo = ordemInsumo.getInsumo();
            insumo.adicionarEstoqueReservado(ordemInsumo.getQuantidade());
            updateInsumoGateway.execute(insumo);
        }
    }

    private void releaseReservedProdutos(OrdemServico ordemServico) throws GatewayException {
        for (OrdemPeca ordemPeca : ordemServico.getPecas()) {
            Peca peca = ordemPeca.getPeca();
            peca.removerEstoqueReservado(ordemPeca.getQuantidade());
            updatePecaGateway.execute(peca);
        }
        for (OrdemInsumo ordemInsumo : ordemServico.getInsumos()) {
            Insumo insumo = ordemInsumo.getInsumo();
            insumo.removerEstoqueReservado(ordemInsumo.getQuantidade());
            updateInsumoGateway.execute(insumo);
        }
    }

    private void updateComplexidade(Ordem os) {
        BigDecimal horasTecnicas = BigDecimal.ZERO;

        for (OrdemServico servico : os.getServicosDesejados()) {
            horasTecnicas = horasTecnicas.add(servico.getServico().getHorasTecnicas());
        }
        for (OrdemServico servico : os.getServicosNecessarios()) {
            horasTecnicas = horasTecnicas.add(servico.getServico().getHorasTecnicas());
        }
        for (OrdemServico servico : os.getServicosAdicionais()) {
            horasTecnicas = horasTecnicas.add(servico.getServico().getHorasTecnicas());
        }

        os.setComplexidade(getComplexidade(horasTecnicas));
    }

    private Complexidade getComplexidade(BigDecimal horasTecnicas) {
        if (horasTecnicas.compareTo(new BigDecimal("2")) <= 0) {
            return Complexidade.BAIXA;
        } else if (horasTecnicas.compareTo(new BigDecimal("6")) <= 0) {
            return Complexidade.MEDIA;
        } else {
            return Complexidade.ALTA;
        }
    }
}