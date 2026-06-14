package com.grupo52.tech_challenge.gateway.database.model;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "status_changes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusChangeDatabase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_de_servico_id", nullable = false)
    private OrdemDeServicoDatabase ordemDeServico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOS status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}