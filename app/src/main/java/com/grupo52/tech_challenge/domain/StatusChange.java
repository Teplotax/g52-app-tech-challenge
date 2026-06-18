package com.grupo52.tech_challenge.domain;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class StatusChange {

    private Long id;

    private StatusOS status;

    private LocalDateTime createdAt;
}
