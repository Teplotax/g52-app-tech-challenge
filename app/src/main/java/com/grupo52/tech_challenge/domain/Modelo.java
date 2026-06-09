package com.grupo52.tech_challenge.domain;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Modelo {

    private Long id;

    private String nome;

    private Marca marca;
}
