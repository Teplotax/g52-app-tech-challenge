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
public class OrdemServico {

    private Long id;

    @Setter
    private Servico servico;

    @Setter
    private Boolean aprovado;

    @Setter
    private BigDecimal precoTotal;

    @Setter
    private BigDecimal precoHorasTecnicas;

    private List<OrdemPeca> pecas;

    private List<OrdemInsumo> insumos;

    public void addPeca(OrdemPeca peca) {
        if (pecas == null) {
            pecas = new ArrayList<>();
        }
        pecas.add(peca);
    }

    public void addInsumo(OrdemInsumo insumo) {
        if (insumos == null) {
            insumos = new ArrayList<>();
        }
        insumos.add(insumo);
    }
}
