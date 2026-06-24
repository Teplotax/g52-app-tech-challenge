package com.grupo52.tech_challenge.gateway.database.model;

import com.grupo52.tech_challenge.domain.Enums.ComplexidadeOS;
import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;

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
    @Setter
    private StatusOS status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteDatabase cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private VeiculoDatabase veiculo;

    @Enumerated(EnumType.STRING)
    @Setter
    private ComplexidadeOS complexidade;

    @Setter
    private String sintomas;

    @Setter
    @Column(unique = true)
    private String tagChave;

    @Setter
    private BigDecimal precoTotal;

    @Setter
    private BigDecimal precoServicosDesejados;

    @Setter
    private BigDecimal precoServicosNecessarios;

    @Setter
    private BigDecimal precoServicosAdicionais;

    @OneToMany(mappedBy = "ordemDeServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("tipo = 'DESEJADO'")
    @Builder.Default
    private List<ServicoOSDatabase> servicosDesejados = new ArrayList<>();

    @OneToMany(mappedBy = "ordemDeServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("tipo = 'NECESSARIO'")
    @Builder.Default
    private List<ServicoOSDatabase> servicosNecessarios = new ArrayList<>();

    @OneToMany(mappedBy = "ordemDeServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @SQLRestriction("tipo = 'ADICIONAL'")
    @Builder.Default
    private List<ServicoOSDatabase> servicosAdicionais = new ArrayList<>();

    @Setter
    private String justificativaNecessarios;

    @Setter
    private String justificativaAdicionais;

    @OneToMany(mappedBy = "ordemDeServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StatusChangeDatabase> historico = new ArrayList<>();

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
        if (os.getHistorico() != null) {
            entity.historico.addAll(
                    os.getHistorico().stream()
                            .map(statusChange -> StatusChangeDatabase.fromDomain(statusChange, entity))
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
                .criadaEm(this.createdAt)
                .precoTotal(this.precoTotal)
                .precoServicosDesejados(this.precoServicosDesejados)
                .precoServicosNecessarios(this.precoServicosNecessarios)
                .precoServicosAdicionais(this.precoServicosAdicionais)
                .servicosDesejados(this.servicosDesejados != null
                        ? new ArrayList<>(this.servicosDesejados.stream().map(ServicoOSDatabase::toDomain).toList())
                        : new ArrayList<>())
                .servicosNecessarios(this.servicosNecessarios != null
                        ? new ArrayList<>(this.servicosNecessarios.stream().map(ServicoOSDatabase::toDomain).toList())
                        : new ArrayList<>())
                .servicosAdicionais(this.servicosAdicionais != null
                        ? new ArrayList<>(this.servicosAdicionais.stream().map(ServicoOSDatabase::toDomain).toList())
                        : new ArrayList<>())
                .historico(this.historico != null
                        ? new ArrayList<>(this.historico.stream().map(StatusChangeDatabase::toDomain).toList())
                        : new ArrayList<>())
                .justificativaNecessarios(this.justificativaNecessarios)
                .justificativaAdicionais(this.justificativaAdicionais)
                .build();
    }
}