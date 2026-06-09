package com.grupo52.tech_challenge.dto.response;

import com.grupo52.tech_challenge.domain.Marca;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarcaInfoResponseDTO {

    private Long id;

    private String nome;

    public static MarcaInfoResponseDTO fromDomain(Marca marca) {
        return MarcaInfoResponseDTO.builder()
                .id(marca.getId())
                .nome(marca.getNome())
                .build();
    }

    public static List<MarcaInfoResponseDTO> fromDomain(List<Marca> marcas) {
        return marcas.stream()
                .map(MarcaInfoResponseDTO::fromDomain)
                .toList();
    }
}
