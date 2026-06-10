package com.grupo52.tech_challenge.domain;

import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Servico {

    private Long id;

    private String nome;

    private BigDecimal horasTecnicas;

    @Builder.Default
    private List<TipoInsumo> insumos = new ArrayList<>();

    @Builder.Default
    private List<ServicoTipoPeca> pecas = new ArrayList<>();

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class ServicoTipoPeca {
        private Long id;
        private TipoPeca tipoPeca;
        private Integer quantidade;
    }
}
