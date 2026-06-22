package com.grupo52.tech_challenge.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.grupo52.tech_challenge.domain.Enums.ComplexidadeOS;
import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.OrdemDeServico;
import com.grupo52.tech_challenge.dto.PagedResponse;
import lombok.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OSInfoResponseDTO {

    private Long id;

    private Long clienteId;

    private Long veiculoId;

    private StatusOS status;

    private ComplexidadeOS complexidade;

    private String clienteNomeSocial;

    private String clienteDocumento;

    private String veiculoPlaca;

    private String sintomas;

    private String tagChave;

    private LocalDateTime criadaEm;

    private BigDecimal precoTotal;

    public static OSInfoResponseDTO fromDomain(OrdemDeServico os) {
        return OSInfoResponseDTO.builder()
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
                .precoTotal(os.getPrecoTotal() != null
                        ? os.getPrecoTotal().setScale(2, RoundingMode.HALF_UP)
                        : null)
                .build();
    }

    public static List<OSInfoResponseDTO> fromDomain(List<OrdemDeServico> list) {
        if (list == null) return List.of();
        return list.stream().map(OSInfoResponseDTO::fromDomain).toList();
    }

    public static PagedResponse<OSInfoResponseDTO> fromDomain(Page<OrdemDeServico> page) {
        return PagedResponse.<OSInfoResponseDTO>builder()
                .content(page.getContent().stream()
                        .map(OSInfoResponseDTO::fromDomain)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}