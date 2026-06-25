package com.grupo52.tech_challenge.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Produto;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FindProdutoByEanResponseDTO {

    private Long id;
    private String sku;
    private String ean;
    private String nome;
    private TipoProduto tipoProduto;
    private BigDecimal preco;
    private Integer estoque;
    private Integer estoqueReservado;
    private Integer estoqueMinimo;

    public static FindProdutoByEanResponseDTO fromDomain(Produto produto) {
        return FindProdutoByEanResponseDTO.builder()
                .id(produto.getId())
                .sku(produto.getSku())
                .ean(produto.getEan())
                .nome(produto.getNome())
                .tipoProduto(produto.getTipoProduto())
                .preco(produto.getPreco())
                .estoque(produto.getEstoque())
                .estoqueReservado(produto.getEstoqueReservado())
                .estoqueMinimo(produto.getEstoqueMinimo())
                .build();
    }
}