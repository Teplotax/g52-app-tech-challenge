package com.grupo52.tech_challenge.domain;

import com.grupo52.tech_challenge.domain.Enums.Complexidade;
import com.grupo52.tech_challenge.domain.Enums.Status;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
}