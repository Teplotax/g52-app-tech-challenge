package com.grupo52.tech_challenge.gateway.database.model;

import com.grupo52.tech_challenge.domain.Marca;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "marca")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarcaDatabase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @OneToMany(mappedBy = "marca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModeloDatabase> modelos;

    public Marca toDomain() {
        return Marca.builder()
                .id(this.id)
                .nome(this.nome)
                .modelos(
                        this.modelos != null
                                ? this.modelos.stream()
                                .map(ModeloDatabase::toDomain)
                                .toList()
                                : new java.util.ArrayList<>()
                )
                .build();
    }

    public Marca toInfo() {
        return Marca.builder()
                .id(this.id)
                .nome(this.nome)
                .modelos(new java.util.ArrayList<>())
                .build();
    }
}
