package com.grupo52.tech_challenge.dto.response;

import com.grupo52.tech_challenge.domain.Veiculo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VeiculoInfoResponseDTO {

    private String placa;

    private String marca;

    private String modelo;

    private Integer ano;

    public static VeiculoInfoResponseDTO fromDomain(Veiculo veiculo) {
        if (veiculo == null) return null;
        return VeiculoInfoResponseDTO.builder()
                .placa(veiculo.getPlaca())
                .marca(veiculo.getMarca().getNome())
                .modelo(veiculo.getModelo().getNome())
                .ano(veiculo.getAno())
                .build();
    }

    public static List<VeiculoInfoResponseDTO> fromDomain(List<Veiculo> veiculos) {
        if (veiculos == null) return List.of();
        return veiculos.stream()
                .map(VeiculoInfoResponseDTO::fromDomain)
                .toList();
    }
}