package com.grupo52.tech_challenge.gateway.database.model;

import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Servico;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "servicos")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoDatabase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;

    private BigDecimal horasTecnicas;

    @ElementCollection(targetClass = TipoInsumo.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "servico_insumos", joinColumns = @JoinColumn(name = "servico_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_insumo")
    @Builder.Default
    private List<TipoInsumo> insumos = new ArrayList<>();

    @OneToMany(mappedBy = "servico", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Setter
    private List<ServicoTipoPecaDatabase> pecas = new ArrayList<>();

    public static ServicoDatabase fromDomain(Servico servico) {
        ServicoDatabase servicoDatabase = ServicoDatabase.builder()
                .id(servico.getId())
                .nome(servico.getNome())
                .horasTecnicas(servico.getHorasTecnicas())
                .insumos(servico.getInsumos() != null ? servico.getInsumos() : new ArrayList<>())
                .build();

        List<ServicoTipoPecaDatabase> pecas = servico.getPecas() != null
                ? servico.getPecas().stream()
                .map(p -> ServicoTipoPecaDatabase.fromDomain(p, servicoDatabase))
                .toList()
                : new ArrayList<>();
        servicoDatabase.setPecas(pecas);

        return servicoDatabase;
    }

    public Servico toDomain() {
        return Servico.builder()
                .id(this.id)
                .nome(this.nome)
                .horasTecnicas(this.horasTecnicas)
                .insumos(this.insumos != null ? this.insumos : new ArrayList<>())
                .pecas(this.pecas != null
                        ? this.pecas.stream().map(ServicoTipoPecaDatabase::toDomain).toList()
                        : new ArrayList<>())
                .build();
    }

    @Entity
    @Table(name = "servico_tipo_pecas")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServicoTipoPecaDatabase {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "servico_id", nullable = false)
        private ServicoDatabase servico;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private TipoPeca tipoPeca;

        @Column(nullable = false)
        private Integer quantidade;

        public static ServicoTipoPecaDatabase fromDomain(Servico.ServicoTipoPeca domain, ServicoDatabase servico) {
            return ServicoTipoPecaDatabase.builder()
                    .id(domain.getId())
                    .servico(servico)
                    .tipoPeca(domain.getTipoPeca())
                    .quantidade(domain.getQuantidade())
                    .build();
        }

        public Servico.ServicoTipoPeca toDomain() {
            return Servico.ServicoTipoPeca.builder()
                    .id(this.id)
                    .tipoPeca(this.tipoPeca)
                    .quantidade(this.quantidade)
                    .build();
        }
    }
}
