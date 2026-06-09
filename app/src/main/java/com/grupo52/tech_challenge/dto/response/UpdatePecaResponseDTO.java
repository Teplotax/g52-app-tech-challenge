package com.grupo52.tech_challenge.dto.response;

import com.grupo52.tech_challenge.domain.AplicacaoProduto;
import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Peca;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePecaResponseDTO {

    private String sku;

    private String ean;

    private String nome;

    private BigDecimal preco;

    private Integer estoque;

    private Integer estoqueMinimo;

    private TipoPeca tipoPeca;

    private List<UpdateAplicacaoProdutoResponseDTO> aplicacoes;

    public static UpdatePecaResponseDTO fromDomain(Peca peca) {
        return UpdatePecaResponseDTO.builder()
                .sku(peca.getSku())
                .ean(peca.getEan())
                .nome(peca.getNome())
                .preco(peca.getPreco())
                .estoque(peca.getEstoque())
                .estoqueMinimo(peca.getEstoqueMinimo())
                .tipoPeca(peca.getTipoPeca())
                .aplicacoes(UpdateAplicacaoProdutoResponseDTO.fromDomain(peca.getAplicacoes()))
                .build();
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UpdateAplicacaoProdutoResponseDTO {
        private Long modeloId;
        private Integer anoInicio;
        private Integer anoFim;

        public static UpdateAplicacaoProdutoResponseDTO fromDomain(AplicacaoProduto aplicacao) {
            return UpdateAplicacaoProdutoResponseDTO.builder()
                    .modeloId(aplicacao.getModelo().getId())
                    .anoInicio(aplicacao.getAnoInicio())
                    .anoFim(aplicacao.getAnoFim())
                    .build();
        }

        public static List<UpdateAplicacaoProdutoResponseDTO> fromDomain(List<AplicacaoProduto> aplicacoes) {
            return aplicacoes.stream().map(aplicacao -> fromDomain(aplicacao)).toList();
        }
    }
}