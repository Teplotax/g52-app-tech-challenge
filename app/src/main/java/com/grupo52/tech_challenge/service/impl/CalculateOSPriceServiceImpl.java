package com.grupo52.tech_challenge.service.impl;

import com.grupo52.tech_challenge.domain.*;
import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.FindServicoGateway;
import com.grupo52.tech_challenge.gateway.UpdateOSGateway;
import com.grupo52.tech_challenge.gateway.database.repository.OrdemDeServicoRepository;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
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
    private OrdemDeServicoRepository ordemDeServicoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Override
    public OrdemDeServico calculateServicosDesejados(OrdemDeServico os) throws GatewayException {

        try {

            for (ServicoOS servico : os.getServicosDesejados()) {
                calculate(servico, os.getVeiculo());
                os.setPrecoServicosDesejados(os.getPrecoServicosDesejados().add(servico.getPrecoTotal()));
            }

//            for (ServicoOS servico : os.getServicosNecessarios()) {
//                calculate(servico, os.getVeiculo());
//                os.setPrecoServicosNecessarios(os.getPrecoServicosNecessarios().add(servico.getPrecoTotal()));
//            }
//
//            for (ServicoOS servico : os.getServicosAdicionais()) {
//                calculate(servico, os.getVeiculo());
//                os.setPrecoServicosAdicionais(os.getPrecoServicosAdicionais().add(servico.getPrecoTotal()));
//            }

            os.setPrecoTotal(os.getPrecoServicosDesejados().add(os.getPrecoServicosNecessarios()).add(os.getPrecoServicosAdicionais()));

        } catch (Exception e) {
            throw new GatewayException(e.getClass().getSimpleName());
        }

        return updateOSGateway.execute(os);
    }

    private void calculate(ServicoOS servicoOS, Veiculo veiculo) throws GatewayException {

        BigDecimal precoTotalOS = BigDecimal.ZERO;

        Servico servico = findServicoGateway.execute(servicoOS.getServico().getId());

        servicoOS.setPrecoHorasTecnicas(servico.getHorasTecnicas().multiply(PRECO_HORA));

        precoTotalOS = precoTotalOS.add(servicoOS.getPrecoHorasTecnicas());


        System.out.println("Preço mão de obra: " + precoTotalOS);

        for (Servico.ServicoTipoPeca servicoTipoPeca : servico.getPecas()) {

//          TODO criar gateway
            Peca peca = produtoRepository.findAllByTipoPecaAndAplicacoesModeloIdAndAplicacoesAnoInicioLessThanEqualAndAplicacoesAnoFimGreaterThanEqual(servicoTipoPeca.getTipoPeca(), veiculo.getModelo().getId(), veiculo.getAno(), veiculo.getAno()).getFirst().toPecaDomain();


            Integer quantidade = servicoTipoPeca.getQuantidade();
            BigDecimal precoPecas = peca.getPreco().multiply(BigDecimal.valueOf(quantidade));

            System.out.println("Preço peças: " + precoPecas);

            System.out.println(peca.getNome());

            PecaOS pecaOS = PecaOS.builder()
                    .peca(peca)
                    .quantidade(quantidade)
                    .precoTotal(precoPecas)
                    .build();

            servicoOS.addPeca(pecaOS);


            precoTotalOS = precoTotalOS.add(precoPecas);

        }

        for (TipoInsumo tipoInsumo : servico.getInsumos()) {
            Insumo insumo = produtoRepository.findAllByTipoInsumoAndAplicacoesModeloIdAndAplicacoesAnoInicioLessThanEqualAndAplicacoesAnoFimGreaterThanEqual(tipoInsumo, veiculo.getModelo().getId(), veiculo.getAno(), veiculo.getAno()).getFirst().toInsumoDomain();

            AplicacaoProduto aplicacao = insumo.getAplicacoes().stream()
                    .filter(ap -> ap.getModelo() != null
                            && veiculo.getModelo().getId().equals(ap.getModelo().getId()))
                    .toList().getFirst();


            BigDecimal precoInsumos = insumo.getPreco().multiply(BigDecimal.valueOf(aplicacao.getQuantidade()));
            System.out.println("Preço insumos: " + precoInsumos);
            System.out.println(insumo.getNome());
            InsumoOS insumoOS = InsumoOS.builder()
                    .insumo(insumo)
                    .quantidade(aplicacao.getQuantidade())
                    .precoTotal(precoInsumos)
                    .build();

            servicoOS.addInsumo(insumoOS);
            precoTotalOS = precoTotalOS.add(precoInsumos);
        }


        servicoOS.setPrecoTotal(precoTotalOS);
        System.out.println("Preço total: " + precoTotalOS);

    }
}


//public class ServicoOS {
//
//    private Long id;
//
//    private Servico servico;
//
//    private Boolean aprovado;
//
//    private BigDecimal precoTotal;
//
//    private List<PecaOS> pecas;
//
//    private List<InsumoOS> insumos;
//}

