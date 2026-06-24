package com.grupo52.tech_challenge.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.grupo52.tech_challenge.domain.Produto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstoqueMovimentacaoResponseDTO {

    private List<ItemDTO> itens;

    public static EstoqueMovimentacaoResponseDTO fromDomain(List<Produto> produtos) {
        return EstoqueMovimentacaoResponseDTO.builder()
                .itens(ItemDTO.fromDomain(produtos))
                .build();
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemDTO {
        private String ean;
        private String nome;
        private Integer estoque;

        public static ItemDTO fromDomain(Produto produto) {
            return ItemDTO.builder()
                    .ean(produto.getEan())
                    .nome(produto.getNome())
                    .estoque(produto.getEstoque())
                    .build();
        }

        public static List<ItemDTO> fromDomain(List<Produto> produtos) {
            return produtos.stream().map(ItemDTO::fromDomain).toList();
        }
    }
}