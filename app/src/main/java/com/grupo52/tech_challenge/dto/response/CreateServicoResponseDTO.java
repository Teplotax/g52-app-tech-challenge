package com.grupo52.tech_challenge.dto.response;

import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Servico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateServicoResponseDTO {

    private String nome;

    private BigDecimal horasTecnicas;

    private List<TipoInsumo> insumos;

    private List<ServicoTipoPecaDTO> pecas;

    public static CreateServicoResponseDTO fromDomain(Servico servico) {
        return CreateServicoResponseDTO.builder()
                .nome(servico.getNome())
                .horasTecnicas(servico.getHorasTecnicas())
                .pecas(servico.getPecas() != null ? ServicoTipoPecaDTO.fromDomain(servico.getPecas()) : new ArrayList<>())
                .insumos(servico.getInsumos())
                .build();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ServicoTipoPecaDTO {

        @NotBlank
        private TipoPeca tipoPeca;

        @PositiveOrZero
        private Integer quantidade;


        public static ServicoTipoPecaDTO fromDomain(Servico.ServicoTipoPeca peca) {
            return ServicoTipoPecaDTO.builder()
                    .tipoPeca(peca.getTipoPeca())
                    .quantidade(peca.getQuantidade())
                    .build();
        }

        public static List<ServicoTipoPecaDTO> fromDomain(List<Servico.ServicoTipoPeca> pecas) {
            return pecas.stream().map(ServicoTipoPecaDTO::fromDomain).toList();
        }
    }
}