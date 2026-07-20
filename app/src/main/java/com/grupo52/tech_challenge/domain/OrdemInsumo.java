package com.grupo52.tech_challenge.domain;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrdemInsumo {

    private Insumo insumo;

    private Integer quantidade;

    private BigDecimal precoTotal;
}
