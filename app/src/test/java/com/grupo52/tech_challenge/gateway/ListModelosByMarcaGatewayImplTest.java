package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Modelo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.model.MarcaDatabase;
import com.grupo52.tech_challenge.gateway.database.model.ModeloDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ModeloRepository;
import com.grupo52.tech_challenge.gateway.impl.ListModelosByMarcaGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListModelosByMarcaGatewayImplTest {

    @Mock
    private ModeloRepository repository;

    @InjectMocks
    private ListModelosByMarcaGatewayImpl gateway;

    @Test
    void executeSucesso() throws GatewayException {
        MarcaDatabase marca = MarcaDatabase.builder().id(1L).nome("Toyota").build();
        ModeloDatabase modeloDatabase = ModeloDatabase.builder().id(1L).nome("Corolla").marca(marca).build();

        when(repository.findByMarcaId(1L)).thenReturn(List.of(modeloDatabase));

        List<Modelo> result = gateway.execute(1L);

        assertEquals(1, result.size());
        assertEquals("Corolla", result.get(0).getNome());
        verify(repository, times(1)).findByMarcaId(1L);
    }

    @Test
    void executeSemModelosRetornaListaVazia() throws GatewayException {
        when(repository.findByMarcaId(1L)).thenReturn(List.of());

        List<Modelo> result = gateway.execute(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        when(repository.findByMarcaId(1L)).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(1L));
    }
}