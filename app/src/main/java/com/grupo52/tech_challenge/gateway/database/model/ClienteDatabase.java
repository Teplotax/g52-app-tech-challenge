package com.grupo52.tech_challenge.gateway.database.model;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "clientes",
        indexes = {
                @Index(name = "idx_documento", columnList = "documento")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDatabase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Enumerated(EnumType.STRING)
    private TipoDocumento tipoDocumento;

    @Column(unique = true, nullable = false)
    private String documento;

    private String email;

    private String telefone;

    private Boolean contatoWhatsApp;

    @Embedded
    private EnderecoDatabase endereco;

//TODO
//    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Builder.Default
//    private List<VeiculoDatabase> veiculos = new ArrayList<>();
//    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Builder.Default
//    private List<OrdemDeServicoDatabase> ordensDeServico = new ArrayList<>();

    public static ClienteDatabase fromDomain(Cliente cliente) {
        return ClienteDatabase.builder()
                .nome(cliente.getNome())
                .tipoDocumento(cliente.getTipoDocumento()) // ou .toString()
                .documento(cliente.getDocumento())
                .email(cliente.getEmail())
                .telefone(cliente.getTelefone())
                .contatoWhatsApp(cliente.getContatoWhatsApp())
                .endereco(EnderecoDatabase.fromDomain(cliente.getEndereco()))
//TODO
//
//                .veiculos()
//                .ordensDeServico()
                .build();
    }

    public Cliente toDomain() {

        System.out.println(this.tipoDocumento);

        return Cliente.builder()
                .id(this.id)
                .nome(this.nome)
                .tipoDocumento(this.tipoDocumento)
                .documento(this.documento)
                .email(this.email)
                .telefone(this.telefone)
                .contatoWhatsApp(this.contatoWhatsApp)
                .endereco(this.endereco.toDomain())
//TODO
//                .veiculos()
//                .ordensDeServico()
                .build();
    }
}