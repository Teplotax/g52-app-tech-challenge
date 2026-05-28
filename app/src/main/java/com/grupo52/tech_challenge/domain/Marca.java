package com.grupo52.tech_challenge.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Marca {

    private Long id;

    private String nome;

    @Builder.Default
    private List<Modelo> modelos = new ArrayList<>();
}
