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

    @Builder.Default
    private Integer estoqueReservado = 0;

    private Integer estoqueMinimo;

    private TipoPeca tipoPeca;

    private List<AplicacaoProduto> aplicacoes;

    @Builder.Default
    private TipoProduto tipoProduto = TipoProduto.PECA;

    public void removerEstoque(Integer quantidade) {
        validarQuantidade(quantidade);

        if (this.estoque < quantidade) {
            throw new IllegalArgumentException(
                    "Estoque insuficiente. Disponível: " + this.estoque);
        }

        this.estoque -= quantidade;
    }

    public void adicionarEstoqueReservado(Integer quantidade) {
        validarQuantidade(quantidade);
        this.estoqueReservado += quantidade;
    }

    public void removerEstoqueReservado(Integer quantidade) {
        validarQuantidade(quantidade);

        if (this.estoqueReservado < quantidade) {
            throw new IllegalArgumentException(
                    "Estoque reservado insuficiente. Disponível: " + this.estoqueReservado);
        }

        this.estoqueReservado -= quantidade;
    }

    private void validarQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero.");
        }
    }
}
