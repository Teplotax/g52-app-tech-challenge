package com.grupo52.tech_challenge.dto.response;

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
public class FindVeiculoResponseDTO {
    private String placa;

    private String marca;

    private String modelo;

    private String cor;

    private Integer ano;

    public static FindVeiculoResponseDTO fromDomain(Veiculo veiculo) {
        if (veiculo == null) return null;
        return FindVeiculoResponseDTO.builder()
                .placa(veiculo.getPlaca())
                .marca(veiculo.getMarca().getNome())
                .modelo(veiculo.getModelo().getNome())
                .ano(veiculo.getAno())
                .cor(veiculo.getCor())
                .build();
    }
}