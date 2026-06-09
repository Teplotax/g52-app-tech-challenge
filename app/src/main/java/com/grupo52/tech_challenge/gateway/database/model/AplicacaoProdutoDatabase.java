package com.grupo52.tech_challenge.gateway.database.model;

import com.grupo52.tech_challenge.domain.AplicacaoProduto;
import com.grupo52.tech_challenge.domain.Modelo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(
        name = "aplicacao_produtos",
        indexes = {
                @Index(name = "idx_produto_id", columnList = "produto_id"),
                @Index(name = "idx_modelo_id", columnList = "modelo_id")
        }
)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AplicacaoProdutoDatabase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id", referencedColumnName = "id")
    private ProdutoDatabase produto;

    @ManyToOne
    @JoinColumn(name = "modelo_id", referencedColumnName = "id")
    private ModeloDatabase modelo;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private Integer anoInicio;

    @Column(nullable = false)
    private Integer anoFim;

    public static AplicacaoProdutoDatabase fromDomain(AplicacaoProduto aplicacao, ProdutoDatabase produto) {
        return AplicacaoProdutoDatabase.builder()
                .id(aplicacao.getId())
                .produto(produto)
                .modelo(ModeloDatabase.fromDomain(aplicacao.getModelo()))
                .quantidade(aplicacao.getQuantidade())
                .anoInicio(aplicacao.getAnoInicio())
                .anoFim(aplicacao.getAnoFim())
                .build();
    }

    public static List<AplicacaoProdutoDatabase> fromDomain(List<AplicacaoProduto> aplicacoes, ProdutoDatabase produto) {
        return aplicacoes.stream()
                .map(aplicacao -> fromDomain(aplicacao, produto))
                .toList();
    }

    public AplicacaoProduto toInfo() {
        return AplicacaoProduto.builder()
                .id(this.getId())
                .modelo(Modelo.builder()
                        .id(this.modelo.getId())
                        .nome(this.modelo.getNome())
                        .build())
                .anoInicio(this.anoInicio)
                .anoFim(this.anoFim)
                .build();
    }
}
