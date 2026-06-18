package com.grupo52.tech_challenge.domain;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ServicoOS {

    private Long id;

    private Servico servico;

    private Boolean aprovado;

    @Setter
    private BigDecimal precoTotal;

    @Setter
    private BigDecimal precoHorasTecnicas;

    private List<PecaOS> pecas;

    private List<InsumoOS> insumos;

    public void addPeca(PecaOS peca) {
        if (pecas == null) {
            pecas = new ArrayList<>();
        }
        pecas.add(peca);
    }

    public void addInsumo(InsumoOS insumo) {
        if (insumos == null) {
            insumos = new ArrayList<>();
        }
        insumos.add(insumo);
    }
}
