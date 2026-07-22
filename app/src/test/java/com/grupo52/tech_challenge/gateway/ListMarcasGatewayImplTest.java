package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Marca;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.model.MarcaDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.MarcaRepository;
import com.grupo52.tech_challenge.gateway.impl.ListMarcasGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListMarcasGatewayImplTest {

    @Mock
    private MarcaRepository repository;

    @InjectMocks
    private ListMarcasGatewayImpl gateway;

    @Test
    void executeSucesso() throws GatewayException {
        MarcaDatabase marcaDatabase = MarcaDatabase.builder().id(1L).nome("Toyota").build();

        when(repository.findAll()).thenReturn(List.of(marcaDatabase));

        List<Marca> result = gateway.execute();

        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getNome());
        verify(repository, times(1)).findAll();
    }

    @Test
    void executeSemMarcasRetornaListaVazia() throws GatewayException {
        when(repository.findAll()).thenReturn(List.of());

        List<Marca> result = gateway.execute();

        assertTrue(result.isEmpty());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        when(repository.findAll()).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute());
    }
}