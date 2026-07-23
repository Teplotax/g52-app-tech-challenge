package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.repository.OrdemDeServicoRepository;
import com.grupo52.tech_challenge.gateway.impl.ClearTagChaveGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClearTagChaveGatewayImplTest {

    @Mock
    private OrdemDeServicoRepository ordemDeServicoRepository;

    @InjectMocks
    private ClearTagChaveGatewayImpl gateway;

    @Test
    void executeSucesso() throws GatewayException {
        when(ordemDeServicoRepository.clearTagChave(1L)).thenReturn(1);

        gateway.execute(1L);

        verify(ordemDeServicoRepository, times(1)).clearTagChave(1L);
    }

    @Test
    void executeNaoEncontradaLancaGatewayException() {
        when(ordemDeServicoRepository.clearTagChave(99L)).thenReturn(0);

        assertThrows(GatewayException.class, () -> gateway.execute(99L));
    }
}