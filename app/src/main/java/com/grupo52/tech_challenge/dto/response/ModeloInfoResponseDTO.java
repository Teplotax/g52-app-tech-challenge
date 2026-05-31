package com.grupo52.tech_challenge.dto.response;

import com.grupo52.tech_challenge.domain.Modelo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModeloInfoResponseDTO {

    private Long id;

    private String nome;

    public static ModeloInfoResponseDTO fromDomain(Modelo modelo) {
        return ModeloInfoResponseDTO.builder()
                .id(modelo.getId())
                .nome(modelo.getNome())
                .build();
    }

    public static List<ModeloInfoResponseDTO> fromDomain(List<Modelo> modelos) {
        return modelos.stream()
                .map(ModeloInfoResponseDTO::fromDomain)
                .toList();
    }
}
