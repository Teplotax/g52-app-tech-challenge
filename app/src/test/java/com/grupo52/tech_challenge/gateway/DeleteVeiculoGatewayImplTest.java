package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.repository.VeiculoRepository;
import com.grupo52.tech_challenge.gateway.impl.DeleteVeiculoGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteVeiculoGatewayImplTest {

    @Mock
    private VeiculoRepository repository;

    @InjectMocks
    private DeleteVeiculoGatewayImpl gateway;

    @Test
    void executeSucesso() throws GatewayException {
        when(repository.existsById(1L)).thenReturn(true);

        gateway.execute(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void executeNaoEncontradoLancaNotFoundGatewayException() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(1L));

        verify(repository, never()).deleteById(any());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        when(repository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("erro")).when(repository).deleteById(1L);

        assertThrows(GatewayException.class, () -> gateway.execute(1L));
    }
}