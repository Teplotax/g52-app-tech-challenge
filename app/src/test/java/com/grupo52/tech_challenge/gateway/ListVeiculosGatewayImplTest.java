package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.MarcaDatabase;
import com.grupo52.tech_challenge.gateway.database.model.ModeloDatabase;
import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.VeiculoRepository;
import com.grupo52.tech_challenge.gateway.impl.ListVeiculosGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListVeiculosGatewayImplTest {

    @Mock
    private VeiculoRepository repository;

    @InjectMocks
    private ListVeiculosGatewayImpl gateway;

    @Test
    void executeSucesso() throws GatewayException {
        MarcaDatabase marca = MarcaDatabase.builder().id(1L).nome("Toyota").build();
        ModeloDatabase modelo = ModeloDatabase.builder().id(1L).nome("Corolla").marca(marca).build();
        VeiculoDatabase veiculoDatabase = VeiculoDatabase.builder()
                .id(1L).placa("ABC1D23").ano(2020).cor("Prata")
                .modelo(modelo).cliente(ClienteDatabase.builder().id(1L).build())
                .build();

        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(veiculoDatabase)));

        Page<Veiculo> result = gateway.execute(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        verify(repository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        when(repository.findAll(any(Pageable.class))).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(Pageable.unpaged()));
    }
}