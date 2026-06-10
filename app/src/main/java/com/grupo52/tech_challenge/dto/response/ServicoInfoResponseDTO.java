package com.grupo52.tech_challenge.dto.response;

import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.dto.PagedResponse;
import lombok.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicoInfoResponseDTO {

    private Long id;

    private String nome;

    private BigDecimal horasTecnicas;

    private List<TipoInsumo> insumos;

    private List<ServicoTipoPecaDTO> pecas;

    public static ServicoInfoResponseDTO fromDomain(Servico servico) {
        return ServicoInfoResponseDTO.builder()
                .id(servico.getId())
                .nome(servico.getNome())
                .horasTecnicas(servico.getHorasTecnicas())
                .insumos(servico.getInsumos())
                .pecas(servico.getPecas() != null
                        ? servico.getPecas().stream().map(ServicoTipoPecaDTO::fromDomain).toList()
                        : List.of())
                .build();
    }

    public static PagedResponse<ServicoInfoResponseDTO> fromDomain(Page<Servico> servicos) {
        return PagedResponse.<ServicoInfoResponseDTO>builder()
                .content(servicos.getContent().stream()
                        .map(ServicoInfoResponseDTO::fromDomain)
                        .toList())
                .page(servicos.getNumber())
                .size(servicos.getSize())
                .totalElements(servicos.getTotalElements())
                .totalPages(servicos.getTotalPages())
                .build();
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ServicoTipoPecaDTO {

        private Long id;
        private TipoPeca tipoPeca;
        private Integer quantidade;

        public static ServicoTipoPecaDTO fromDomain(Servico.ServicoTipoPeca peca) {
            return ServicoTipoPecaDTO.builder()
                    .id(peca.getId())
                    .tipoPeca(peca.getTipoPeca())
                    .quantidade(peca.getQuantidade())
                    .build();
        }
    }
}