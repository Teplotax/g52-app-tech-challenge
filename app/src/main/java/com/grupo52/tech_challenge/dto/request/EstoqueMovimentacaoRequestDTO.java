package com.grupo52.tech_challenge.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.grupo52.tech_challenge.validation.annotation.SafeDto;
import lombok.*;

@SafeDto
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EstoqueMovimentacaoRequestDTO {

        @NotNull(message = "O EAN é obrigatório")
        private String ean;

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        private Integer quantidade;
}