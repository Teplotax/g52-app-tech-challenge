package com.grupo52.tech_challenge.gateway.database.model;

import com.grupo52.tech_challenge.domain.Veiculo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "veiculo",
        indexes = {
                @Index(name = "idx_placa", columnList = "placa"),
                @Index(name = "idx_cliente_id", columnList = "cliente_id")
        }
)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VeiculoDatabase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String placa;

    @Column(nullable = false)
    private Integer ano;

    @Column(nullable = false)
    private String cor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modelo_id", referencedColumnName = "id")
    private ModeloDatabase modelo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", referencedColumnName = "id")
    private ClienteDatabase cliente;

    public static VeiculoDatabase fromDomain(Veiculo veiculo, ModeloDatabase modelo, ClienteDatabase cliente) {
        return VeiculoDatabase.builder()
                .id(veiculo.getId())
                .placa(veiculo.getPlaca())
                .ano(veiculo.getAno())
                .modelo(modelo)
                .cliente(cliente)
                .cor(veiculo.getCor())
                .build();
    }

    public Veiculo toDomain() {
        return Veiculo.builder()
                .id(this.id)
                .clienteId(this.cliente.getId())
                .placa(this.placa)
                .ano(this.ano)
                .modelo(this.modelo.toInfo())
                .marca(this.modelo.getMarca().toInfo())
                .cor(this.cor)
                .build();
    }
}
