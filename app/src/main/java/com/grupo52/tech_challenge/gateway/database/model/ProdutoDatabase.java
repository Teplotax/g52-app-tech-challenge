package com.grupo52.tech_challenge.gateway.database.model;

import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.domain.Produto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "produtos",
        indexes = {
                @Index(name = "idx_produto_sku", columnList = "sku"),
                @Index(name = "idx_produto_ean", columnList = "ean"),
                @Index(name = "idx_produto_tipo_peca", columnList = "tipoPeca"),
                @Index(name = "idx_produto_tipo_insumo", columnList = "tipoInsumo")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProdutoDatabase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(unique = true, nullable = false)
    private String ean;

    private String nome;

    private BigDecimal preco;

    private Integer estoque;

    private Integer estoqueMinimo;

    @Enumerated(EnumType.STRING)
    private TipoProduto tipoProduto;

    @Enumerated(EnumType.STRING)
    private TipoPeca tipoPeca;

    @Enumerated(EnumType.STRING)
    private TipoInsumo tipoInsumo;

    private BigDecimal quantidadeEmbalagem;

    private String unidade;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Setter
    private List<AplicacaoProdutoDatabase> aplicacoes = new ArrayList<>();




    public static ProdutoDatabase fromDomain(Insumo insumo) {
        return ProdutoDatabase.builder()
                .id(insumo.getId())
                .sku(insumo.getSku())
                .ean(insumo.getEan())
                .nome(insumo.getNome())
                .preco(insumo.getPreco())
                .estoque(insumo.getEstoque())
                .estoqueMinimo(insumo.getEstoqueMinimo())
                .quantidadeEmbalagem(insumo.getQuantidadeEmbalagem())
                .unidade(insumo.getUnidade())
                .tipoProduto(insumo.getTipoProduto())
                .tipoInsumo(insumo.getTipoInsumo())
                .build();
    }

    public static ProdutoDatabase fromDomain(Peca peca) {
        ProdutoDatabase produtoDatabase = ProdutoDatabase.builder()
                .id(peca.getId())
                .sku(peca.getSku())
                .ean(peca.getEan())
                .nome(peca.getNome())
                .preco(peca.getPreco())
                .estoque(peca.getEstoque())
                .estoqueMinimo(peca.getEstoqueMinimo())
                .tipoProduto(peca.getTipoProduto())
                .tipoPeca(peca.getTipoPeca())
                .build();

        List<AplicacaoProdutoDatabase> aplicacoes = AplicacaoProdutoDatabase.fromDomain(peca.getAplicacoes(), produtoDatabase);
        produtoDatabase.setAplicacoes(aplicacoes);

        return produtoDatabase;
    }

    public static ProdutoDatabase fromDomain(Produto produto) {
        return ProdutoDatabase.builder()
                .id(produto.getId())
                .sku(produto.getSku())
                .ean(produto.getEan())
                .nome(produto.getNome())
                .preco(produto.getPreco())
                .estoque(produto.getEstoque())
                .estoqueMinimo(produto.getEstoqueMinimo())
                .tipoProduto(produto.getTipoProduto())
                .build();
    }

    public Insumo toInsumoDomain() {
        return Insumo.builder()
                .id(this.id)
                .sku(this.sku)
                .ean(this.ean)
                .nome(this.nome)
                .preco(this.preco)
                .estoque(this.estoque)
                .estoqueMinimo(this.estoqueMinimo)
                .quantidadeEmbalagem(this.quantidadeEmbalagem)
                .unidade(this.unidade)
                .tipoProduto(this.tipoProduto)
                .tipoInsumo(this.tipoInsumo)
                .build();
    }

    public Peca toPecaDomain() {
        return Peca.builder()
                .id(this.id)
                .sku(this.sku)
                .ean(this.ean)
                .nome(this.nome)
                .preco(this.preco)
                .estoque(this.estoque)
                .estoqueMinimo(this.estoqueMinimo)
                .tipoProduto(this.tipoProduto)
                .tipoPeca(this.tipoPeca)
                .aplicacoes(this.aplicacoes.stream().map(AplicacaoProdutoDatabase::toInfo).toList())
                .build();
    }
}