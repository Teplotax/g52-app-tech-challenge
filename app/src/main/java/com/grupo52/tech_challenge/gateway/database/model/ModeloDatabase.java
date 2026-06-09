package com.grupo52.tech_challenge.gateway.database.model;

import com.grupo52.tech_challenge.domain.Modelo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "modelo")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModeloDatabase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "marca_id", nullable = false)
    private MarcaDatabase marca;

    public Modelo toDomain() {
        return Modelo.builder()
                .id(this.id)
                .nome(this.nome)
                .marca(this.marca != null ? this.marca.toInfo() : null)
                .build();
    }

    public Modelo toInfo() {
        return Modelo.builder()
                .id(this.id)
                .nome(this.nome)
                .build();
    }

    public static ModeloDatabase fromDomain(Modelo modelo) {
        return ModeloDatabase.builder()
                .id(modelo.getId())
                .build();
    }
}
