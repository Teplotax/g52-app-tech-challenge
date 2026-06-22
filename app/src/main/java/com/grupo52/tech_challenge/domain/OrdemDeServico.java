package com.grupo52.tech_challenge.domain;

import com.grupo52.tech_challenge.domain.Enums.ComplexidadeOS;
import com.grupo52.tech_challenge.domain.Enums.StatusOS;
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
public class OrdemDeServico {

    private Long id;

    private StatusOS status;

    private Cliente cliente;

    private Veiculo veiculo;

    @Setter
    private ComplexidadeOS complexidade;

    private String sintomas;

    private String tagChave;

    private LocalDateTime criadaEm;

    @Setter
    private BigDecimal precoTotal;

    @Setter
    private BigDecimal precoTotalAprovado;

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
    private List<ServicoOS> servicosDesejados = new ArrayList<>();

    @Builder.Default
    private List<ServicoOS> servicosNecessarios = new ArrayList<>();

    @Builder.Default
    private List<ServicoOS> servicosAdicionais = new ArrayList<>();

    @Builder.Default
    private List<StatusChange> historico = new ArrayList<>();

    @Setter
    private String justificativaNecessarios;

    @Setter
    private String justificativaAdicionais;

    public void setStatus(StatusOS status) {
        this.status = status;
        this.historico.add(
                StatusChange.builder()
                        .status(status)
                        .build()
        );
    }
}