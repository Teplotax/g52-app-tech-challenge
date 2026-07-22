package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ServicoRepository;
import com.grupo52.tech_challenge.gateway.impl.FindServicoGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindServicoGatewayImplTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private FindServicoGatewayImpl gateway;

    @Test
    void executeSucesso() throws GatewayException {
        ServicoDatabase servicoDatabase = ServicoDatabase.builder().id(1L).nome("Troca de óleo").horasTecnicas(new BigDecimal("1.5")).build();

        when(repository.findById(1L)).thenReturn(Optional.of(servicoDatabase));

        Servico result = gateway.execute(1L);

        assertNotNull(result);
        assertEquals("Troca de óleo", result.getNome());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void executeNaoEncontradoLancaNotFoundGatewayException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(1L));
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        when(repository.findById(1L)).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(1L));
    }
}