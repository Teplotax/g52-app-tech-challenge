package com.grupo52.tech_challenge.dto.response;

import com.grupo52.tech_challenge.domain.Veiculo;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateVeiculoResponseDTO {

    private String placa;

    private String marca;

    private String modelo;

    private Integer ano;

    public static CreateVeiculoResponseDTO fromDomain(Veiculo veiculo) {
        if (veiculo == null) return null;
        return CreateVeiculoResponseDTO.builder()
                .placa(veiculo.getPlaca())
                .marca(veiculo.getMarca().getNome())
                .modelo(veiculo.getModelo().getNome())
                .ano(veiculo.getAno())
                .build();
    }
}