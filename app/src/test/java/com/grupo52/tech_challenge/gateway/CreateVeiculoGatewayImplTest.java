package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Modelo;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.MarcaDatabase;
import com.grupo52.tech_challenge.gateway.database.model.ModeloDatabase;
import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ClienteRepository;
import com.grupo52.tech_challenge.gateway.database.repository.ModeloRepository;
import com.grupo52.tech_challenge.gateway.database.repository.VeiculoRepository;
import com.grupo52.tech_challenge.gateway.impl.CreateVeiculoGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateVeiculoGatewayImplTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ModeloRepository modeloRepository;

    @InjectMocks
    private CreateVeiculoGatewayImpl gateway;

    private ClienteDatabase clienteDatabase() {
        return ClienteDatabase.builder().id(1L).nomeSocial("João Silva").build();
    }

    private ModeloDatabase modeloDatabase() {
        MarcaDatabase marca = MarcaDatabase.builder().id(1L).nome("Toyota").build();
        return ModeloDatabase.builder().id(1L).nome("Corolla").marca(marca).build();
    }

    private VeiculoDatabase veiculoSalvo() {
        return VeiculoDatabase.builder()
                .id(1L).placa("ABC1D23").ano(2020).cor("Prata")
                .modelo(modeloDatabase()).cliente(clienteDatabase())
                .build();
    }

    @Test
    void executeSucesso() throws GatewayException {
        Veiculo veiculo = Veiculo.builder()
                .placa("ABC1D23").ano(2020).cor("Prata")
                .clienteId(1L).modelo(Modelo.builder().id(1L).build())
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteDatabase()));
        when(modeloRepository.findById(1L)).thenReturn(Optional.of(modeloDatabase()));
        when(veiculoRepository.save(any(VeiculoDatabase.class))).thenReturn(veiculoSalvo());

        Veiculo result = gateway.execute(veiculo);

        assertNotNull(result);
        assertEquals("ABC1D23", result.getPlaca());
        verify(veiculoRepository, times(1)).save(any(VeiculoDatabase.class));
    }

    @Test
    void executeClienteNaoEncontradoLancaNotFoundGatewayException() {
        Veiculo veiculo = Veiculo.builder().placa("ABC1D23").clienteId(1L).modelo(Modelo.builder().id(1L).build()).build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(veiculo));

        verify(modeloRepository, never()).findById(any());
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void executeModeloNaoEncontradoLancaNotFoundGatewayException() {
        Veiculo veiculo = Veiculo.builder().placa("ABC1D23").clienteId(1L).modelo(Modelo.builder().id(99L).build()).build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteDatabase()));
        when(modeloRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(veiculo));

        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void executePlacaDuplicadaLancaGatewayException() {
        Veiculo veiculo = Veiculo.builder().placa("ABC1D23").clienteId(1L).modelo(Modelo.builder().id(1L).build()).build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteDatabase()));
        when(modeloRepository.findById(1L)).thenReturn(Optional.of(modeloDatabase()));
        when(veiculoRepository.save(any(VeiculoDatabase.class))).thenThrow(new DataIntegrityViolationException("duplicate key"));

        GatewayException exception = assertThrows(GatewayException.class, () -> gateway.execute(veiculo));

        assertEquals(409, exception.getStatus());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        Veiculo veiculo = Veiculo.builder().placa("ABC1D23").clienteId(1L).modelo(Modelo.builder().id(1L).build()).build();

        when(clienteRepository.findById(1L)).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(veiculo));
    }
}