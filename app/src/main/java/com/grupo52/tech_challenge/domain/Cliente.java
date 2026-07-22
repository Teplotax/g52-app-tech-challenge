package com.grupo52.tech_challenge.domain;

import com.grupo52.tech_challenge.domain.Enums.TipoDocumento;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Cliente {

    private Long id;

    private String nomeSocial;

    private String nome;

    private TipoDocumento tipoDocumento;

    private String documento;

    private String email;

    private String telefone;

    private Boolean contatoWhatsApp;

    private Endereco endereco;

    @Builder.Default
    private List<Veiculo> veiculos = new ArrayList<>();

    @Builder.Default
    private List<Ordem> ordensDeServico = new ArrayList<>();

}
