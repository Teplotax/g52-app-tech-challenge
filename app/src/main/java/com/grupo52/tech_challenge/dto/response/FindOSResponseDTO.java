package com.grupo52.tech_challenge.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.grupo52.tech_challenge.domain.Enums.ComplexidadeOS;
import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.InsumoOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.domain.PecaOS;
import com.grupo52.tech_challenge.domain.ServicoOS;
import com.grupo52.tech_challenge.domain.StatusChange;
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
public class FindOSResponseDTO {

    private Long id;

    private StatusOS status;

    private Long clienteId;

    private String clienteNomeSocial;

    private String clienteDocumento;

    private Long veiculoId;

    private String veiculoPlaca;

    private ComplexidadeOS complexidade;

    private String sintomas;

    private String tagChave;

    private LocalDateTime criadaEm;

    private BigDecimal precoTotal;

    private BigDecimal precoTotalAprovado;

    private BigDecimal precoServicosDesejados;

    private BigDecimal precoServicosNecessarios;

    private BigDecimal precoServicosAdicionais;

    private String justificativaNecessarios;

    private String justificativaAdicionais;

    private List<ServicoOSDetailDTO> servicosDesejados;

    private List<ServicoOSDetailDTO> servicosNecessarios;

    private List<ServicoOSDetailDTO> servicosAdicionais;

    private List<StatusChangeDTO> historico;

    public static FindOSResponseDTO fromDomain(OrdemDeServico os) {
        return FindOSResponseDTO.builder()
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
                .precoTotalAprovado(scale(os.getPrecoTotalAprovado()))
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
        private String servico;
        private BigDecimal precoTotal;
        private BigDecimal precoHorasTecnicas;
        private List<PecaOSDetailDTO> pecas;
        private List<InsumoOSDetailDTO> insumos;

        public static ServicoOSDetailDTO fromDomain(ServicoOS servicoOS) {
            return ServicoOSDetailDTO.builder()
                    .servico(servicoOS.getServico().getNome())
                    .precoTotal(scale(servicoOS.getPrecoTotal()))
                    .precoHorasTecnicas(scale(servicoOS.getPrecoHorasTecnicas()))
                    .pecas(PecaOSDetailDTO.fromDomain(servicoOS.getPecas()))
                    .insumos(InsumoOSDetailDTO.fromDomain(servicoOS.getInsumos()))
                    .build();
        }

        private static BigDecimal scale(BigDecimal value) {
            return value != null ? value.setScale(2, RoundingMode.HALF_UP) : null;
        }

        public static List<ServicoOSDetailDTO> fromDomain(List<ServicoOS> servicos) {
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

        public static PecaOSDetailDTO fromDomain(PecaOS pecaOS) {
            return PecaOSDetailDTO.builder()
                    .peca(pecaOS.getPeca().getNome())
                    .ean(pecaOS.getPeca().getEan())
                    .quantidade(pecaOS.getQuantidade())
                    .precoUnitario(pecaOS.getPeca().getPreco().setScale(2, RoundingMode.HALF_UP))
                    .precoTotal(pecaOS.getPrecoTotal().setScale(2, RoundingMode.HALF_UP))
                    .build();
        }

        public static List<PecaOSDetailDTO> fromDomain(List<PecaOS> pecas) {
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

        public static InsumoOSDetailDTO fromDomain(InsumoOS insumoOS) {
            return InsumoOSDetailDTO.builder()
                    .insumo(insumoOS.getInsumo().getNome())
                    .ean(insumoOS.getInsumo().getEan())
                    .quantidade(insumoOS.getQuantidade())
                    .precoUnitario(insumoOS.getInsumo().getPreco().setScale(2, RoundingMode.HALF_UP))
                    .precoTotal(insumoOS.getPrecoTotal().setScale(2, RoundingMode.HALF_UP))
                    .build();
        }

        public static List<InsumoOSDetailDTO> fromDomain(List<InsumoOS> insumos) {
            if (insumos == null) return List.of();
            return insumos.stream().map(InsumoOSDetailDTO::fromDomain).toList();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StatusChangeDTO {
        private StatusOS status;
        private java.time.LocalDateTime createdAt;

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