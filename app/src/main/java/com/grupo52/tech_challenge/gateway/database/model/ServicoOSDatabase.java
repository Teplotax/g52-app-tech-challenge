package com.grupo52.tech_challenge.gateway.database.model;

import com.grupo52.tech_challenge.domain.OrdemInsumo;
import com.grupo52.tech_challenge.domain.OrdemPeca;
import com.grupo52.tech_challenge.domain.OrdemServico;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "servico_os")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoOSDatabase {

    public enum TipoServicoOS { DESEJADO, NECESSARIO, ADICIONAL }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_de_servico_id", nullable = false)
    private OrdemDeServicoDatabase ordemDeServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", nullable = false)
    private ServicoDatabase servico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoServicoOS tipo;

    private Boolean aprovado;

    private BigDecimal precoTotal;

    private BigDecimal precoHorasTecnicas;

    @OneToMany(mappedBy = "servicoOS", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PecaOSDatabase> pecas = new ArrayList<>();

    @OneToMany(mappedBy = "servicoOS", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InsumoOSDatabase> insumos = new ArrayList<>();


    public static ServicoOSDatabase fromDomain(OrdemServico domain, OrdemDeServicoDatabase os, TipoServicoOS tipo) {
        ServicoOSDatabase entity = ServicoOSDatabase.builder()
                .id(domain.getId())
                .ordemDeServico(os)
                .servico(ServicoDatabase.builder().id(domain.getServico().getId()).build())
                .tipo(tipo)
                .aprovado(domain.getAprovado())
                .precoTotal(domain.getPrecoTotal())
                .precoHorasTecnicas(domain.getPrecoHorasTecnicas())
                .build();

        if (domain.getPecas() != null) {
            entity.pecas.addAll(
                    domain.getPecas().stream()
                            .map(p -> PecaOSDatabase.fromDomain(p, entity))
                            .toList());
        }
        if (domain.getInsumos() != null) {
            entity.insumos.addAll(
                    domain.getInsumos().stream()
                            .map(i -> InsumoOSDatabase.fromDomain(i, entity))
                            .toList());
        }

        return entity;
    }

    public OrdemServico toDomain() {
        return OrdemServico.builder()
                .id(this.id)
                .servico(this.servico.toDomain())
                .aprovado(this.aprovado)
                .precoTotal(this.precoTotal)
                .precoHorasTecnicas(this.precoHorasTecnicas)
                .pecas(this.pecas != null
                        ? new ArrayList<>(this.pecas.stream().map(PecaOSDatabase::toDomain).toList())
                        : new ArrayList<>())
                .insumos(this.insumos != null
                        ? new ArrayList<>(this.insumos.stream().map(InsumoOSDatabase::toDomain).toList())
                        : new ArrayList<>())
                .build();
    }


    @Entity
    @Table(name = "peca_os")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PecaOSDatabase {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "servico_os_id", nullable = false)
        private ServicoOSDatabase servicoOS;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "produto_id", nullable = false)
        private ProdutoDatabase produto;

        @Column(nullable = false)
        private Integer quantidade;

        @Column(nullable = false)
        private BigDecimal precoTotal;

        @Builder.Default
        @Column(nullable = false)
        private Boolean reservado = false;

        public static PecaOSDatabase fromDomain(OrdemPeca domain, ServicoOSDatabase servicoOS) {
            return PecaOSDatabase.builder()
                    .servicoOS(servicoOS)
                    .produto(ProdutoDatabase.builder().id(domain.getPeca().getId()).build())
                    .quantidade(domain.getQuantidade())
                    .precoTotal(domain.getPrecoTotal())
                    .reservado(domain.getReservado() != null ? domain.getReservado() : false)
                    .build();
        }

        public OrdemPeca toDomain() {
            return OrdemPeca.builder()
                    .peca(this.produto.toPecaDomain())
                    .quantidade(this.quantidade)
                    .precoTotal(this.precoTotal)
                    .reservado(this.reservado != null ? this.reservado : false)
                    .build();
        }
    }

    @Entity
    @Table(name = "insumo_os")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InsumoOSDatabase {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "servico_os_id", nullable = false)
        private ServicoOSDatabase servicoOS;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "produto_id", nullable = false)
        private ProdutoDatabase produto;

        @Column(nullable = false)
        private Integer quantidade;

        @Column(nullable = false)
        private BigDecimal precoTotal;

        @Builder.Default
        @Column(nullable = false)
        private Boolean reservado = false;

        public static InsumoOSDatabase fromDomain(OrdemInsumo domain, ServicoOSDatabase servicoOS) {
            return InsumoOSDatabase.builder()
                    .servicoOS(servicoOS)
                    .produto(ProdutoDatabase.builder().id(domain.getInsumo().getId()).build())
                    .quantidade(domain.getQuantidade())
                    .precoTotal(domain.getPrecoTotal())
                    .reservado(domain.getReservado() != null ? domain.getReservado() : false)
                    .build();
        }

        public OrdemInsumo toDomain() {
            return OrdemInsumo.builder()
                    .insumo(this.produto.toInsumoDomain())
                    .quantidade(this.quantidade)
                    .precoTotal(this.precoTotal)
                    .reservado(this.reservado != null ? this.reservado : false)
                    .build();
        }
    }
}