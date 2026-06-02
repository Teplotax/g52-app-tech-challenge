package com.grupo52.tech_challenge.dto.response;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.dto.PagedResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VeiculoInfoResponseDTO {

    private Long id;

    private String placa;

    private String marca;

    private String modelo;

    private String cor;

    private Integer ano;

    public static VeiculoInfoResponseDTO fromDomain(Veiculo veiculo) {
        if (veiculo == null) return null;
        return VeiculoInfoResponseDTO.builder()
                .id(veiculo.getId())
                .placa(veiculo.getPlaca())
                .marca(veiculo.getMarca().getNome())
                .modelo(veiculo.getModelo().getNome())
                .ano(veiculo.getAno())
                .cor(veiculo.getCor())
                .build();
    }

    public static List<VeiculoInfoResponseDTO> fromDomain(List<Veiculo> veiculos) {
        if (veiculos == null) return List.of();
        return veiculos.stream()
                .map(VeiculoInfoResponseDTO::fromDomain)
                .toList();
    }

    public static PagedResponse<VeiculoInfoResponseDTO> fromDomain(Page<Veiculo> veiculos) {
        return PagedResponse.<VeiculoInfoResponseDTO>builder()
                .content(veiculos.getContent().stream()
                        .map(VeiculoInfoResponseDTO::fromDomain)
                        .toList())
                .page(veiculos.getNumber())
                .size(veiculos.getSize())
                .totalElements(veiculos.getTotalElements())
                .totalPages(veiculos.getTotalPages())
                .build();
    }
}