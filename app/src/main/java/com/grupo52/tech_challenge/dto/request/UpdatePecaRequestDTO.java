package com.grupo52.tech_challenge.dto.request;

import com.grupo52.tech_challenge.domain.AplicacaoProduto;
import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Modelo;
import com.grupo52.tech_challenge.domain.Peca;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import com.grupo52.tech_challenge.validation.annotation.SafeDto;

@SafeDto
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePecaRequestDTO {

    private String sku;

    private String ean;

    private String nome;

    private BigDecimal preco;

    private Integer estoque;

    private Integer estoqueMinimo;

    private TipoPeca tipoPeca;

    @Valid
    private List<UpdateAplicacaoProdutoRequestDTO> aplicacoes;

    public Peca toDomain(Long pecaId) {
        return Peca.builder()
                .id(pecaId)
                .sku(this.sku)
                .ean(this.ean)
                .nome(this.nome)
                .preco(this.preco)
                .estoque(this.estoque != null ? this.estoque : 0)
                .estoqueMinimo(this.estoqueMinimo != null ? this.estoqueMinimo : 1)
                .tipoPeca(this.tipoPeca)
                .aplicacoes(this.aplicacoes.stream().map(UpdateAplicacaoProdutoRequestDTO::toDomain).toList())
                .build();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateAplicacaoProdutoRequestDTO {

        @NotNull
        private Long modeloId;

        @Min(1000)
        @Max(9999)
        @NotNull
        private Integer anoInicio;

        @Min(1000)
        @Max(9999)
        @NotNull
        private Integer anoFim;


        public AplicacaoProduto toDomain() {
            return AplicacaoProduto.builder()
                    .modelo(Modelo.builder().id(this.modeloId).build())
                    .anoInicio(this.anoInicio)
                    .anoFim(this.anoFim)
                    .build();
        }
    }
}