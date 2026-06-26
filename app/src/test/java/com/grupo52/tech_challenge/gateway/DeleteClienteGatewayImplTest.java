package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import com.grupo52.tech_challenge.gateway.impl.DeleteClienteGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteClienteGatewayImplTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private DeleteClienteGatewayImpl gateway;

    @Test
    void executeSucesso() throws GatewayException {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        gateway.execute(1L);

        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void executeNaoEncontradoLancaNotFoundGatewayException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(99L));

        verify(repository, times(1)).existsById(99L);
        verify(repository, never()).deleteById(any());
    }

    @Test
    void executeErroGenericoLancaGatewayException() {
        when(repository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("db error")).when(repository).deleteById(1L);

        assertThrows(GatewayException.class, () -> gateway.execute(1L));

        verify(repository, times(1)).deleteById(1L);
    }
}