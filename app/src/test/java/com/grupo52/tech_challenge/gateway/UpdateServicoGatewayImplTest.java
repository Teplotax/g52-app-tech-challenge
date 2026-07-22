package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ServicoRepository;
import com.grupo52.tech_challenge.gateway.impl.UpdateServicoGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateServicoGatewayImplTest {

    @Mock
    private ServicoRepository repository;

    @InjectMocks
    private UpdateServicoGatewayImpl gateway;

    private ServicoDatabase existingServicoDatabase() {
        return ServicoDatabase.builder().id(1L).nome("Troca de óleo").horasTecnicas(new BigDecimal("1.5")).build();
    }

    @Test
    void executeSucesso() throws GatewayException {
        Servico servico = Servico.builder().id(1L).horasTecnicas(new BigDecimal("2.0")).build();

        when(repository.findById(1L)).thenReturn(Optional.of(existingServicoDatabase()));
        when(repository.save(any(ServicoDatabase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Servico result = gateway.execute(servico);

        assertEquals("Troca de óleo", result.getNome());
        assertEquals(0, new BigDecimal("2.0").compareTo(result.getHorasTecnicas()));
        verify(repository, times(1)).save(any(ServicoDatabase.class));
    }

    @Test
    void executeNaoEncontradoLancaNotFoundGatewayException() {
        Servico servico = Servico.builder().id(1L).build();

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(servico));

        verify(repository, never()).save(any());
    }

    @Test
    void executeNomeDuplicadoLancaGatewayException() {
        Servico servico = Servico.builder().id(1L).nome("Outro nome").build();

        when(repository.findById(1L)).thenReturn(Optional.of(existingServicoDatabase()));
        when(repository.save(any(ServicoDatabase.class))).thenThrow(new DataIntegrityViolationException("duplicate key"));

        GatewayException exception = assertThrows(GatewayException.class, () -> gateway.execute(servico));

        assertEquals(409, exception.getStatus());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        Servico servico = Servico.builder().id(1L).build();

        when(repository.findById(1L)).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(servico));
    }
}