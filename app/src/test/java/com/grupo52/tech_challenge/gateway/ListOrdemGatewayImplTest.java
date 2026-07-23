package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.EnderecoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.MarcaDatabase;
import com.grupo52.tech_challenge.gateway.database.model.ModeloDatabase;
import com.grupo52.tech_challenge.gateway.database.model.OrdemDeServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.OrdemDeServicoRepository;
import com.grupo52.tech_challenge.gateway.impl.ListOrdemGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListOrdemGatewayImplTest {

    @Mock
    private OrdemDeServicoRepository repository;

    @InjectMocks
    private ListOrdemGatewayImpl gateway;

    private OrdemDeServicoDatabase osDatabase() {
        MarcaDatabase marca = MarcaDatabase.builder().id(1L).nome("Toyota").build();
        ModeloDatabase modelo = ModeloDatabase.builder().id(1L).nome("Corolla").marca(marca).build();
        VeiculoDatabase veiculo = VeiculoDatabase.builder()
                .id(1L).placa("ABC1D23").ano(2020).cor("Prata")
                .modelo(modelo).cliente(ClienteDatabase.builder().id(1L).endereco(EnderecoDatabase.builder().build()).build())
                .build();

        return OrdemDeServicoDatabase.builder()
                .id(1L).status(Status.EM_DIAGNOSTICO)
                .cliente(veiculo.getCliente()).veiculo(veiculo)
                .servicosDesejados(new ArrayList<>())
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .historico(new ArrayList<>())
                .build();
    }

    @Test
    void executeSucesso() throws GatewayException {
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(osDatabase())));

        Page<Ordem> result = gateway.execute(null, null, null, null, null, null, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        verify(repository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void executeComFiltrosSucesso() throws GatewayException {
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(osDatabase())));

        Page<Ordem> result = gateway.execute(
                "ABC1D23", "123.456.789-00", Status.EM_DIAGNOSTICO, null,
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(null, null, null, null, null, null, Pageable.unpaged()));
    }
}