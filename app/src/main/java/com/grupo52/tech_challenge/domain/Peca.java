package com.grupo52.tech_challenge.domain;

import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Peca implements Produto {
    private Long id;

    private String sku;

    private String ean;

    private String nome;

    private BigDecimal preco;

    private Integer estoque;

    private Integer estoqueMinimo;

    private TipoPeca tipoPeca;

    private List<AplicacaoProduto> aplicacoes;

    @Builder.Default
    private TipoProduto tipoProduto = TipoProduto.PECA;
}
