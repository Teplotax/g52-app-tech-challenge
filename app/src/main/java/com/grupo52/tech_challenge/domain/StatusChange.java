package com.grupo52.tech_challenge.domain;

import com.grupo52.tech_challenge.domain.Enums.Status;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class StatusChange {

    private Long id;

    private Status status;

    private LocalDateTime createdAt;
}
