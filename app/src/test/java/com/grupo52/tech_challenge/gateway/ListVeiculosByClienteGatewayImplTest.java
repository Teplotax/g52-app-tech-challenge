package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.MarcaDatabase;
import com.grupo52.tech_challenge.gateway.database.model.ModeloDatabase;
import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.VeiculoRepository;
import com.grupo52.tech_challenge.gateway.impl.ListVeiculosByClienteGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListVeiculosByClienteGatewayImplTest {

    @Mock
    private VeiculoRepository repository;

    @InjectMocks
    private ListVeiculosByClienteGatewayImpl gateway;

    private VeiculoDatabase veiculoDatabase() {
        MarcaDatabase marca = MarcaDatabase.builder().id(1L).nome("Toyota").build();
        ModeloDatabase modelo = ModeloDatabase.builder().id(1L).nome("Corolla").marca(marca).build();
        return VeiculoDatabase.builder()
                .id(1L).placa("ABC1D23").ano(2020).cor("Prata")
                .modelo(modelo).cliente(ClienteDatabase.builder().id(1L).build())
                .build();
    }

    @Test
    void executeSucesso() throws GatewayException {
        when(repository.findByClienteId(1L)).thenReturn(List.of(veiculoDatabase()));

        List<Veiculo> result = gateway.execute(1L);

        assertEquals(1, result.size());
        assertEquals("ABC1D23", result.get(0).getPlaca());
        verify(repository, times(1)).findByClienteId(1L);
    }

    @Test
    void executeSemVeiculosRetornaListaVazia() throws GatewayException {
        when(repository.findByClienteId(2L)).thenReturn(List.of());

        List<Veiculo> result = gateway.execute(2L);

        assertTrue(result.isEmpty());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        when(repository.findByClienteId(1L)).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(1L));
    }
}