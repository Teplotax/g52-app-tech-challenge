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
public class AddServicosResponseDTO {

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


    private BigDecimal precoServicosDesejados;

    private BigDecimal precoServicosNecessarios;

    private BigDecimal precoServicosAdicionais;

    private String justificativaNecessarios;

    private String justificativaAdicionais;

    private List<ServicoOSDetailDTO> servicosDesejados;

    private List<ServicoOSDetailDTO> servicosNecessarios;

    private List<ServicoOSDetailDTO> servicosAdicionais;

    private List<StatusChangeDTO> historico;

    public static AddServicosResponseDTO fromDomain(Ordem os) {
        return AddServicosResponseDTO.builder()
                .id(os.getId())
                .status(os.getStatus())
                .complexidade(os.getComplexidade())
                .clienteId(os.getCliente().getId())
                .clienteNomeSocial(os.getCliente().getNomeSocial())
                .clienteDocumento(os.getCliente().getDocumento())
                .veiculoId(os.getVeiculo().getId())
                .veiculoPlaca(os.getVeiculo().getPlaca())
                .sintomas(os.getSintomas())
                .tagChave(os.getTagChave())
                .criadaEm(os.getCriadaEm())
                .precoTotal(scale(os.getPrecoTotal()))
                .precoServicosDesejados(scale(os.getPrecoServicosDesejados()))
                .precoServicosNecessarios(scale(os.getPrecoServicosNecessarios()))
                .precoServicosAdicionais(scale(os.getPrecoServicosAdicionais()))
                .justificativaNecessarios(os.getJustificativaNecessarios())
                .justificativaAdicionais(os.getJustificativaAdicionais())
                .servicosDesejados(ServicoOSDetailDTO.fromDomain(os.getServicosDesejados()))
                .servicosNecessarios(ServicoOSDetailDTO.fromDomain(os.getServicosNecessarios()))
                .servicosAdicionais(ServicoOSDetailDTO.fromDomain(os.getServicosAdicionais()))
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
    public static class ServicoOSDetailDTO {
        private Long id;
        private String servico;
        private Boolean aprovado;
        private BigDecimal precoTotal;
        private BigDecimal precoHorasTecnicas;
        private List<PecaOSDetailDTO> pecas;
        private List<InsumoOSDetailDTO> insumos;

        public static ServicoOSDetailDTO fromDomain(OrdemServico ordemServico) {
            return ServicoOSDetailDTO.builder()
                    .id(ordemServico.getId())
                    .servico(ordemServico.getServico().getNome())
                    .aprovado(ordemServico.getAprovado())
                    .precoTotal(scale(ordemServico.getPrecoTotal()))
                    .precoHorasTecnicas(scale(ordemServico.getPrecoHorasTecnicas()))
                    .pecas(PecaOSDetailDTO.fromDomain(ordemServico.getPecas()))
                    .insumos(InsumoOSDetailDTO.fromDomain(ordemServico.getInsumos()))
                    .build();
        }

        private static BigDecimal scale(BigDecimal value) {
            return value != null ? value.setScale(2, RoundingMode.HALF_UP) : null;
        }

        public static List<ServicoOSDetailDTO> fromDomain(List<OrdemServico> servicos) {
            if (servicos == null) return List.of();
            return servicos.stream().map(ServicoOSDetailDTO::fromDomain).toList();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PecaOSDetailDTO {
        private String peca;
        private String ean;
        private Integer quantidade;
        private BigDecimal precoUnitario;
        private BigDecimal precoTotal;

        public static PecaOSDetailDTO fromDomain(OrdemPeca ordemPeca) {
            return PecaOSDetailDTO.builder()
                    .peca(ordemPeca.getPeca().getNome())
                    .ean(ordemPeca.getPeca().getEan())
                    .quantidade(ordemPeca.getQuantidade())
                    .precoUnitario(ordemPeca.getPeca().getPreco().setScale(2, RoundingMode.HALF_UP))
                    .precoTotal(ordemPeca.getPrecoTotal().setScale(2, RoundingMode.HALF_UP))
                    .build();
        }

        public static List<PecaOSDetailDTO> fromDomain(List<OrdemPeca> pecas) {
            if (pecas == null) return List.of();
            return pecas.stream().map(PecaOSDetailDTO::fromDomain).toList();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InsumoOSDetailDTO {
        private String insumo;
        private String ean;
        private Integer quantidade;
        private BigDecimal precoUnitario;
        private BigDecimal precoTotal;

        public static InsumoOSDetailDTO fromDomain(OrdemInsumo ordemInsumo) {
            return InsumoOSDetailDTO.builder()
                    .insumo(ordemInsumo.getInsumo().getNome())
                    .ean(ordemInsumo.getInsumo().getEan())
                    .quantidade(ordemInsumo.getQuantidade())
                    .precoUnitario(ordemInsumo.getInsumo().getPreco().setScale(2, RoundingMode.HALF_UP))
                    .precoTotal(ordemInsumo.getPrecoTotal().setScale(2, RoundingMode.HALF_UP))
                    .build();
        }

        public static List<InsumoOSDetailDTO> fromDomain(List<OrdemInsumo> insumos) {
            if (insumos == null) return List.of();
            return insumos.stream().map(InsumoOSDetailDTO::fromDomain).toList();
        }
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