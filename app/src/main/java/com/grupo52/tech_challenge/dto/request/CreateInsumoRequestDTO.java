package com.grupo52.tech_challenge.dto.request;

import com.grupo52.tech_challenge.domain.AplicacaoProduto;
import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Enums.UnidadeDeMedida;
import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.domain.Modelo;
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
public class CreateInsumoRequestDTO {

    @NotBlank
    private String sku;

    @NotBlank
    private String ean;

    @NotBlank
    private String nome;

    @NotNull
    private BigDecimal preco;

    @PositiveOrZero
    private Integer estoque;

    @PositiveOrZero
    private Integer estoqueMinimo;

    @NotNull
    private TipoInsumo tipoInsumo;

    private BigDecimal quantidadeEmbalagem;

    @Valid
    private UnidadeDeMedida unidadeDeMedida;

    @NotEmpty
    @Valid
    private List<CreateAplicacaoProdutoRequestDTO> aplicacoes;

    public Insumo toDomain() {
        return Insumo.builder()
                .sku(this.sku)
                .ean(this.ean)
                .nome(this.nome)
                .preco(this.preco)
                .estoque(this.estoque != null ? this.estoque : 0)
                .estoqueMinimo(this.estoqueMinimo != null ? this.estoqueMinimo : 1)
                .tipoInsumo(this.tipoInsumo)
                .quantidadeEmbalagem(this.quantidadeEmbalagem)
                .unidadeDeMedida(this.unidadeDeMedida)
                .aplicacoes(this.aplicacoes.stream().map(CreateAplicacaoProdutoRequestDTO::toDomain).toList())
                .build();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateAplicacaoProdutoRequestDTO {

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

        @NotNull
        @PositiveOrZero
        private Integer quantidade;


        public AplicacaoProduto toDomain() {
            return AplicacaoProduto.builder()
                    .modelo(Modelo.builder().id(this.modeloId).build())
                    .anoInicio(this.anoInicio)
                    .anoFim(this.anoFim)
                    .quantidade(this.quantidade)
                    .build();
        }
    }
}