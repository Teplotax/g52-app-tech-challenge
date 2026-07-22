package com.grupo52.tech_challenge.domain;

import com.grupo52.tech_challenge.domain.Enums.Complexidade;
import com.grupo52.tech_challenge.domain.Enums.Status;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Ordem {

    private Long id;

    private Status status;

    private Cliente cliente;

    private Veiculo veiculo;

    @Setter
    private Complexidade complexidade;

    private String sintomas;

    private String tagChave;

    private LocalDateTime criadaEm;

    @Setter
    private BigDecimal precoTotal;

    @Setter
    @Builder.Default
    private BigDecimal precoServicosDesejados = BigDecimal.ZERO;

    @Setter
    @Builder.Default
    private BigDecimal precoServicosNecessarios = BigDecimal.ZERO;

    @Setter
    @Builder.Default
    private BigDecimal precoServicosAdicionais = BigDecimal.ZERO;

    @Builder.Default
    private List<OrdemServico> servicosDesejados = new ArrayList<>();

    @Builder.Default
    private List<OrdemServico> servicosNecessarios = new ArrayList<>();

    @Builder.Default
    private List<OrdemServico> servicosAdicionais = new ArrayList<>();

    @Builder.Default
    private List<StatusChange> historico = new ArrayList<>();

    @Setter
    private String justificativaNecessarios;

    @Setter
    private String justificativaAdicionais;

    public void setStatus(Status status) {
        this.status = status;
        this.historico.add(
                StatusChange.builder()
                        .status(status)
                        .build()
        );
    }

    private Stream<OrdemServico> servicosAprovados() {
        return Stream.of(servicosDesejados, servicosNecessarios, servicosAdicionais)
                .flatMap(List::stream)
                .filter(servico -> Boolean.TRUE.equals(servico.getAprovado()));
    }

    public List<OrdemPeca> getPecasNaoReservadas() {
        return servicosAprovados()
                .flatMap(servico -> servico.getPecas().stream())
                .filter(ordemPeca -> !Boolean.TRUE.equals(ordemPeca.getReservado()))
                .toList();
    }

    public List<OrdemInsumo> getInsumosNaoReservados() {
        return servicosAprovados()
                .flatMap(servico -> servico.getInsumos().stream())
                .filter(ordemInsumo -> !Boolean.TRUE.equals(ordemInsumo.getReservado()))
                .toList();
    }
}