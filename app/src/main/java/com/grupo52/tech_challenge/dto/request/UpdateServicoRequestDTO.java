package com.grupo52.tech_challenge.dto.request;

import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Servico;
import jakarta.validation.constraints.PositiveOrZero;
import com.grupo52.tech_challenge.validation.annotation.SafeDto;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SafeDto
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateServicoRequestDTO {

    private String nome;

    @PositiveOrZero
    private BigDecimal horasTecnicas;

    private List<TipoInsumo> insumos;

    private List<ServicoTipoPecaDTO> pecas;

    public Servico toDomain(Long servicoId) {
        return Servico.builder()
                .id(servicoId)
                .nome(this.nome)
                .horasTecnicas(this.horasTecnicas)
                .insumos(this.insumos)
                .pecas(this.pecas != null ? this.pecas.stream().map(ServicoTipoPecaDTO::toDomain).toList() : new ArrayList<>())
                .build();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ServicoTipoPecaDTO {

        private TipoPeca tipoPeca;

        @PositiveOrZero
        private Integer quantidade;

        public Servico.ServicoTipoPeca toDomain() {
            return Servico.ServicoTipoPeca.builder()
                    .tipoPeca(this.tipoPeca)
                    .quantidade(this.quantidade != null ? this.quantidade : 1)
                    .build();
        }
    }
}