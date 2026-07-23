package com.grupo52.tech_challenge.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.grupo52.tech_challenge.domain.Enums.Complexidade;
import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.OrdemInsumo;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.OrdemPeca;
import com.grupo52.tech_challenge.domain.OrdemServico;
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
public class CreateOSResponseDTO {
    private Long id;

    private Status status;

    private Complexidade complexidade;

    private String sintomas;

    private Long clienteId;

    private String clienteNomeSocial;

    private Long veiculoId;

    private String veiculoPlaca;

    private String tagChave;

    private LocalDateTime criadaEm;

    private BigDecimal precoTotal;

    private List<ServicoOSDTO> servicosDesejados;

    public static CreateOSResponseDTO fromDomain(Ordem os) {
        return CreateOSResponseDTO.builder()
                .id(os.getId())
                .status(os.getStatus())
                .complexidade(os.getComplexidade())
                .clienteId(os.getCliente().getId())
                .clienteNomeSocial(os.getCliente().getNomeSocial())
                .veiculoId(os.getVeiculo().getId())
                .veiculoPlaca(os.getVeiculo().getPlaca())
                .tagChave(os.getTagChave())
                .criadaEm(os.getCriadaEm())
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
        private Long id;
        private String servico;
        private Boolean aprovado;
        private BigDecimal precoTotal;
        private BigDecimal precoHorasTecnicas;
        private List<PecaOSDTO> pecas;
        private List<InsumoOSDTO> insumos;

        public static ServicoOSDTO fromDomain(OrdemServico ordemServico) {
            return ServicoOSDTO.builder()
                    .id(ordemServico.getId())
                    .servico(ordemServico.getServico().getNome())
                    .aprovado(ordemServico.getAprovado())
                    .precoTotal(ordemServico.getPrecoTotal().setScale(2, RoundingMode.HALF_UP))
                    .precoHorasTecnicas(ordemServico.getPrecoHorasTecnicas().setScale(2, RoundingMode.HALF_UP))
                    .pecas(PecaOSDTO.fromDomain(ordemServico.getPecas()))
                    .insumos(InsumoOSDTO.fromDomain(ordemServico.getInsumos()))
                    .build();
        }

        public static List<ServicoOSDTO> fromDomain(List<OrdemServico> servicos) {
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



        public static PecaOSDTO fromDomain(OrdemPeca ordemPeca) {
            return PecaOSDTO.builder()
                    .peca(ordemPeca.getPeca().getNome())
                    .ean(ordemPeca.getPeca().getEan())
                    .quantidade(ordemPeca.getQuantidade())
                    .precoUnitario(ordemPeca.getPeca().getPreco().setScale(2, RoundingMode.HALF_UP))
                    .precoTotal(ordemPeca.getPrecoTotal().setScale(2, RoundingMode.HALF_UP))
                    .build();
        }

        public static List<PecaOSDTO> fromDomain(List<OrdemPeca> pecas) {
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



        public static InsumoOSDTO fromDomain(OrdemInsumo ordemInsumo) {
            return InsumoOSDTO.builder()
                    .insumo(ordemInsumo.getInsumo().getNome())
                    .ean(ordemInsumo.getInsumo().getEan())
                    .quantidade(ordemInsumo.getQuantidade())
                    .precoUnitario(ordemInsumo.getInsumo().getPreco().setScale(2, RoundingMode.HALF_UP))
                    .precoTotal(ordemInsumo.getPrecoTotal().setScale(2, RoundingMode.HALF_UP))
                    .build();
        }

        public static List<InsumoOSDTO> fromDomain(List<OrdemInsumo> insumos) {
            return insumos.stream().map(insumo -> fromDomain(insumo)).toList();
        }
    }
}