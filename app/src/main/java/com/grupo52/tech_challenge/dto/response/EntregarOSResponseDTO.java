package com.grupo52.tech_challenge.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.grupo52.tech_challenge.domain.Enums.Complexidade;
import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntregarOSResponseDTO {

    private Long id;

    private Long clienteId;

    private Long veiculoId;

    private Status status;

    private Complexidade complexidade;

    private String clienteNomeSocial;

    private String clienteDocumento;

    private String veiculoPlaca;

    private String sintomas;

    private String tagChave;

    private LocalDateTime criadaEm;

    private BigDecimal precoTotal;


    private List<StatusChangeDTO> historico;

    public static EntregarOSResponseDTO fromDomain(Ordem os) {
        return EntregarOSResponseDTO.builder()
                .id(os.getId())
                .status(os.getStatus())
                .clienteId(os.getCliente().getId())
                .clienteNomeSocial(os.getCliente().getNomeSocial())
                .clienteDocumento(os.getCliente().getDocumento())
                .veiculoId(os.getVeiculo().getId())
                .veiculoPlaca(os.getVeiculo().getPlaca())
                .complexidade(os.getComplexidade())
                .sintomas(os.getSintomas())
                .tagChave(os.getTagChave())
                .criadaEm(os.getCriadaEm())
                .precoTotal(scale(os.getPrecoTotal()))
                .historico(StatusChangeDTO.fromDomain(os.getHistorico()))
                .build();
    }

    private static BigDecimal scale(BigDecimal value) {
        return value != null ? value.setScale(2, RoundingMode.HALF_UP) : null;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StatusChangeDTO {
        private Status status;
        private LocalDateTime createdAt;

        public static StatusChangeDTO fromDomain(StatusChange statusChange) {
            return StatusChangeDTO.builder()
                    .status(statusChange.getStatus())
                    .createdAt(statusChange.getCreatedAt())
                    .build();
        }

        public static List<StatusChangeDTO> fromDomain(List<StatusChange> historico) {
            if (historico == null) return List.of();
            return historico.stream().map(StatusChangeDTO::fromDomain).toList();
        }
    }
}