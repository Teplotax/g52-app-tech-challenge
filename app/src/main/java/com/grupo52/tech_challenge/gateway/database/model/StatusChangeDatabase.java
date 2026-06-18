package com.grupo52.tech_challenge.gateway.database.model;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.domain.StatusChange;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    public static StatusChangeDatabase fromDomain(StatusChange statusChange, OrdemDeServicoDatabase os) {
        return StatusChangeDatabase.builder()
                .id(statusChange.getId())
                .ordemDeServico(os)
                .status(statusChange.getStatus())
                .createdAt(statusChange.getCreatedAt())
                .build();
    }

    public StatusChange toDomain() {
        return StatusChange.builder()
                .id(this.id)
                .status(this.status)
                .createdAt(this.createdAt)
                .build();
    }
}