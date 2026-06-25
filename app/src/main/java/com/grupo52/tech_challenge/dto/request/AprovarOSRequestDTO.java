package com.grupo52.tech_challenge.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.grupo52.tech_challenge.validation.annotation.SafeDto;
import lombok.*;

import java.util.List;

@SafeDto
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AprovarOSRequestDTO {

    private List<@NotNull @Positive Long> servicosAprovados;
}