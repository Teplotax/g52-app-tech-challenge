package com.grupo52.tech_challenge.gateway.database.model;

import com.grupo52.tech_challenge.domain.Enums.ComplexidadeOS;
import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.domain.ServicoOS;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "ordens_de_servico",
        indexes = {
                @Index(name = "idx_os_status", columnList = "status"),
                @Index(name = "idx_os_cliente_id", columnList = "cliente_id"),
                @Index(name = "idx_os_veiculo_id", columnList = "veiculo_id"),
                @Index(name = "idx_os_tag_chave", columnList = "tagChave")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdemDeServicoDatabase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOS status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteDatabase cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private VeiculoDatabase veiculo;

    @Enumerated(EnumType.STRING)
    private ComplexidadeOS complexidade;

    private String sintomas;

    private String tagChave;

    private BigDecimal precoTotal;

    private BigDecimal precoTotalAprovado;

    private BigDecimal precoServicosDesejados;

    private BigDecimal precoServicosNecessarios;

    private BigDecimal precoServicosAdicionais;

    @OneToMany(mappedBy = "ordemDeServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ServicoOSDatabase> servicosDesejados = new ArrayList<>();

    @OneToMany(mappedBy = "ordemDeServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ServicoOSDatabase> servicosNecessarios = new ArrayList<>();

    @OneToMany(mappedBy = "ordemDeServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ServicoOSDatabase> servicosAdicionais = new ArrayList<>();

    private String justificativaNecessarios;

    private String justificativaAdicionais;

    @OneToMany(mappedBy = "ordemDeServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StatusChangeDatabase> statusChanges = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    public static OrdemDeServicoDatabase fromDomain(
            OrdemDeServico os,
            ClienteDatabase cliente,
            VeiculoDatabase veiculo) {

        OrdemDeServicoDatabase entity = OrdemDeServicoDatabase.builder()
                .id(os.getId())
                .status(os.getStatus())
                .cliente(cliente)
                .veiculo(veiculo)
                .complexidade(os.getComplexidade())
                .sintomas(os.getSintomas())
                .tagChave(os.getTagChave())
                .precoTotal(os.getPrecoTotal())
                .precoTotalAprovado(os.getPrecoTotalAprovado())
                .precoServicosDesejados(os.getPrecoServicosDesejados())
                .precoServicosNecessarios(os.getPrecoServicosNecessarios())
                .precoServicosAdicionais(os.getPrecoServicosAdicionais())
                .justificativaNecessarios(os.getJustificativaNecessarios())
                .justificativaAdicionais(os.getJustificativaAdicionais())
                .build();

        if (os.getServicosDesejados() != null) {
            entity.servicosDesejados.addAll(
                    os.getServicosDesejados().stream()
                            .map(s -> ServicoOSDatabase.fromDomain(s, entity, ServicoOSDatabase.TipoServicoOS.DESEJADO))
                            .toList());
        }
        if (os.getServicosNecessarios() != null) {
            entity.servicosNecessarios.addAll(
                    os.getServicosNecessarios().stream()
                            .map(s -> ServicoOSDatabase.fromDomain(s, entity, ServicoOSDatabase.TipoServicoOS.NECESSARIO))
                            .toList());
        }
        if (os.getServicosAdicionais() != null) {
            entity.servicosAdicionais.addAll(
                    os.getServicosAdicionais().stream()
                            .map(s -> ServicoOSDatabase.fromDomain(s, entity, ServicoOSDatabase.TipoServicoOS.ADICIONAL))
                            .toList());
        }

        return entity;
    }

    public OrdemDeServico toDomain() {
        return OrdemDeServico.builder()
                .id(this.id)
                .status(this.status)
                .cliente(this.cliente.toDomain())
                .veiculo(this.veiculo.toDomain())
                .complexidade(this.complexidade)
                .sintomas(this.sintomas)
                .tagChave(this.tagChave)
                .precoTotal(this.precoTotal)
                .precoTotalAprovado(this.precoTotalAprovado)
                .precoServicosDesejados(this.precoServicosDesejados)
                .precoServicosNecessarios(this.precoServicosNecessarios)
                .precoServicosAdicionais(this.precoServicosAdicionais)
                .servicosDesejados(this.servicosDesejados != null
                        ? this.servicosDesejados.stream().map(ServicoOSDatabase::toDomain).toList()
                        : new ArrayList<>())
                .servicosNecessarios(this.servicosNecessarios != null
                        ? this.servicosNecessarios.stream().map(ServicoOSDatabase::toDomain).toList()
                        : new ArrayList<>())
                .servicosAdicionais(this.servicosAdicionais != null
                        ? this.servicosAdicionais.stream().map(ServicoOSDatabase::toDomain).toList()
                        : new ArrayList<>())
                .justificativaNecessarios(this.justificativaNecessarios)
                .justificativaAdicionais(this.justificativaAdicionais)
                .build();
    }
}