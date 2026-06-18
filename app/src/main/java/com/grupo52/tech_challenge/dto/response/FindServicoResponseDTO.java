package com.grupo52.tech_challenge.dto.response;

import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Servico;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindServicoResponseDTO {

    private Long id;

    private String nome;

    private BigDecimal horasTecnicas;

    private List<TipoInsumo> insumos;

    private List<ServicoTipoPecaDTO> pecas;

    public static FindServicoResponseDTO fromDomain(Servico servico) {
        return FindServicoResponseDTO.builder()
                .id(servico.getId())
                .nome(servico.getNome())
                .horasTecnicas(servico.getHorasTecnicas())
                .insumos(servico.getInsumos())
                .pecas(servico.getPecas() != null
                        ? servico.getPecas().stream().map(ServicoTipoPecaDTO::fromDomain).toList()
                        : List.of())
                .build();
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ServicoTipoPecaDTO {

        private TipoPeca tipoPeca;
        private Integer quantidade;

        public static ServicoTipoPecaDTO fromDomain(Servico.ServicoTipoPeca peca) {
            return ServicoTipoPecaDTO.builder()
                    .tipoPeca(peca.getTipoPeca())
                    .quantidade(peca.getQuantidade())
                    .build();
        }
    }
}