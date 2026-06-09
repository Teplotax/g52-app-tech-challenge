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
public class FindPecaResponseDTO {

    private String sku;

    private String ean;

    private String nome;

    private BigDecimal preco;

    private Integer estoque;

    private Integer estoqueReservado;

    private Integer estoqueMinimo;

    private TipoPeca tipoPeca;

    private List<CreateAplicacaoProdutoResponseDTO> aplicacoes;

    public static FindPecaResponseDTO fromDomain(Peca peca) {
        return FindPecaResponseDTO.builder()
                .sku(peca.getSku())
                .ean(peca.getEan())
                .nome(peca.getNome())
                .preco(peca.getPreco())
                .estoque(peca.getEstoque())
                .estoqueReservado(peca.getEstoqueReservado())
                .estoqueMinimo(peca.getEstoqueMinimo())
                .tipoPeca(peca.getTipoPeca())
                .aplicacoes(CreateAplicacaoProdutoResponseDTO.fromDomain(peca.getAplicacoes()))
                .build();
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateAplicacaoProdutoResponseDTO {
        private Long modeloId;
        private Integer anoInicio;
        private Integer anoFim;

        public static CreateAplicacaoProdutoResponseDTO fromDomain(AplicacaoProduto aplicacao) {
            return CreateAplicacaoProdutoResponseDTO.builder()
                    .modeloId(aplicacao.getModelo().getId())
                    .anoInicio(aplicacao.getAnoInicio())
                    .anoFim(aplicacao.getAnoFim())
                    .build();
        }

        public static List<CreateAplicacaoProdutoResponseDTO> fromDomain(List<AplicacaoProduto> aplicacoes) {
            return aplicacoes.stream().map(aplicacao -> fromDomain(aplicacao)).toList();
        }
    }
}