package com.grupo52.tech_challenge.dto.response;

import com.grupo52.tech_challenge.domain.AplicacaoProduto;
import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Enums.UnidadeDeMedida;
import com.grupo52.tech_challenge.domain.Insumo;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateInsumoResponseDTO {

    private String sku;

    private String ean;

    private String nome;

    private TipoInsumo tipoInsumo;

    private BigDecimal preco;

    private Integer estoque;

    private Integer estoqueMinimo;

    private BigDecimal quantidadeEmbalagem;

    private UnidadeDeMedida unidadeDeMedida;

    private List<CreateAplicacaoProdutoResponseDTO> aplicacoes;

    public static CreateInsumoResponseDTO fromDomain(Insumo insumo) {
        return CreateInsumoResponseDTO.builder()
                .sku(insumo.getSku())
                .ean(insumo.getEan())
                .nome(insumo.getNome())
                .preco(insumo.getPreco())
                .estoque(insumo.getEstoque())
                .estoqueMinimo(insumo.getEstoqueMinimo())
                .tipoInsumo(insumo.getTipoInsumo())
                .quantidadeEmbalagem(insumo.getQuantidadeEmbalagem())
                .unidadeDeMedida(insumo.getUnidadeDeMedida())
                .aplicacoes(CreateAplicacaoProdutoResponseDTO.fromDomain(insumo.getAplicacoes()))
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
        private Integer quantidade;

        public static CreateAplicacaoProdutoResponseDTO fromDomain(AplicacaoProduto aplicacao) {
            return CreateAplicacaoProdutoResponseDTO.builder()
                    .modeloId(aplicacao.getModelo().getId())
                    .anoInicio(aplicacao.getAnoInicio())
                    .anoFim(aplicacao.getAnoFim())
                    .quantidade(aplicacao.getQuantidade())
                    .build();
        }

        public static List<CreateAplicacaoProdutoResponseDTO> fromDomain(List<AplicacaoProduto> aplicacoes) {
            return aplicacoes.stream().map(aplicacao -> fromDomain(aplicacao)).toList();
        }
    }
}