package com.grupo52.tech_challenge.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.InsumoOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.domain.PecaOS;
import com.grupo52.tech_challenge.domain.ServicoOS;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateOSResponseDTO {

    private StatusOS status;

    private String sintomas;

    private Long clienteId;

    private Long veiculoId;

    private String tagChave;

    private BigDecimal precoTotal;

    private List<ServicoOSDTO> servicosDesejados;

    public static CreateOSResponseDTO fromDomain(OrdemDeServico os) {
        return CreateOSResponseDTO.builder()
                .status(os.getStatus())
                .clienteId(os.getCliente().getId())
                .veiculoId(os.getVeiculo().getId())
                .tagChave(os.getTagChave())
                .sintomas(os.getSintomas())
                .precoTotal(os.getPrecoTotal().setScale(2, RoundingMode.HALF_UP))
                .servicosDesejados(ServicoOSDTO.fromDomain(os.getServicosDesejados()))
                .build();
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ServicoOSDTO {
        private String servico;
        private BigDecimal precoTotal;
        private BigDecimal precoHorasTecnicas;
        private List<PecaOSDTO> pecas;
        private List<InsumoOSDTO> insumos;

        public static ServicoOSDTO fromDomain(ServicoOS servicoOS) {
            return ServicoOSDTO.builder()
                    .servico(servicoOS.getServico().getNome())
                    .precoTotal(servicoOS.getPrecoTotal().setScale(2, RoundingMode.HALF_UP))
                    .precoHorasTecnicas(servicoOS.getPrecoHorasTecnicas().setScale(2, RoundingMode.HALF_UP))
                    .pecas(PecaOSDTO.fromDomain(servicoOS.getPecas()))
                    .insumos(InsumoOSDTO.fromDomain(servicoOS.getInsumos()))
                    .build();
        }

        public static List<ServicoOSDTO> fromDomain(List<ServicoOS> servicos) {
            return servicos.stream().map(servico -> fromDomain(servico)).toList();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PecaOSDTO {
        private String peca;
        private String ean;
        private Integer quantidade;
        private BigDecimal precoUnitario;
        private BigDecimal precoTotal;



        public static PecaOSDTO fromDomain(PecaOS pecaOS) {
            return PecaOSDTO.builder()
                    .peca(pecaOS.getPeca().getNome())
                    .ean(pecaOS.getPeca().getEan())
                    .quantidade(pecaOS.getQuantidade())
                    .precoUnitario(pecaOS.getPeca().getPreco().setScale(2, RoundingMode.HALF_UP))
                    .precoTotal(pecaOS.getPrecoTotal().setScale(2, RoundingMode.HALF_UP))
                    .build();
        }

        public static List<PecaOSDTO> fromDomain(List<PecaOS> pecas) {
            return pecas.stream().map(peca -> fromDomain(peca)).toList();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class InsumoOSDTO {
        private String insumo;
        private String ean;
        private Integer quantidade;
        private BigDecimal precoUnitario;
        private BigDecimal precoTotal;



        public static InsumoOSDTO fromDomain(InsumoOS insumoOS) {
            return InsumoOSDTO.builder()
                    .insumo(insumoOS.getInsumo().getNome())
                    .ean(insumoOS.getInsumo().getEan())
                    .quantidade(insumoOS.getQuantidade())
                    .precoUnitario(insumoOS.getInsumo().getPreco().setScale(2, RoundingMode.HALF_UP))
                    .precoTotal(insumoOS.getPrecoTotal().setScale(2, RoundingMode.HALF_UP))
                    .build();
        }

        public static List<InsumoOSDTO> fromDomain(List<InsumoOS> insumos) {
            return insumos.stream().map(insumo -> fromDomain(insumo)).toList();
        }
    }
}
