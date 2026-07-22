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
import com.grupo52.tech_challenge.gateway.impl.UpdateVeiculoGatewayImpl;
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
class UpdateVeiculoGatewayImplTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ModeloRepository modeloRepository;

    @InjectMocks
    private UpdateVeiculoGatewayImpl gateway;

    private VeiculoDatabase existingVeiculoDatabase() {
        MarcaDatabase marca = MarcaDatabase.builder().id(1L).nome("Toyota").build();
        ModeloDatabase modelo = ModeloDatabase.builder().id(1L).nome("Corolla").marca(marca).build();
        return VeiculoDatabase.builder()
                .id(1L).placa("ABC1D23").ano(2020).cor("Prata")
                .modelo(modelo).cliente(ClienteDatabase.builder().id(1L).build())
                .build();
    }

    @Test
    void executeSucessoSemTrocarModeloOuCliente() throws GatewayException {
        Veiculo veiculo = Veiculo.builder().id(1L).cor("Preto").build();

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(existingVeiculoDatabase()));
        when(veiculoRepository.save(any(VeiculoDatabase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Veiculo result = gateway.execute(veiculo);

        assertEquals("Preto", result.getCor());
        assertEquals("ABC1D23", result.getPlaca());
        verify(modeloRepository, never()).findById(any());
        verify(clienteRepository, never()).findById(any());
    }

    @Test
    void executeTrocaModelo() throws GatewayException {
        Veiculo veiculo = Veiculo.builder().id(1L).modelo(Modelo.builder().id(2L).build()).build();
        MarcaDatabase novaMarca = MarcaDatabase.builder().id(2L).nome("Honda").build();
        ModeloDatabase novoModelo = ModeloDatabase.builder().id(2L).nome("Civic").marca(novaMarca).build();

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(existingVeiculoDatabase()));
        when(modeloRepository.findById(2L)).thenReturn(Optional.of(novoModelo));
        when(veiculoRepository.save(any(VeiculoDatabase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Veiculo result = gateway.execute(veiculo);

        assertEquals("Civic", result.getModelo().getNome());
    }

    @Test
    void executeModeloNaoEncontradoLancaNotFoundGatewayException() {
        Veiculo veiculo = Veiculo.builder().id(1L).modelo(Modelo.builder().id(99L).build()).build();

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(existingVeiculoDatabase()));
        when(modeloRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(veiculo));

        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void executeClienteNaoEncontradoLancaNotFoundGatewayException() {
        Veiculo veiculo = Veiculo.builder().id(1L).clienteId(99L).build();

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(existingVeiculoDatabase()));
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(veiculo));

        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void executeVeiculoNaoEncontradoLancaNotFoundGatewayException() {
        Veiculo veiculo = Veiculo.builder().id(1L).build();

        when(veiculoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(veiculo));
    }

    @Test
    void executePlacaDuplicadaLancaGatewayException() {
        Veiculo veiculo = Veiculo.builder().id(1L).placa("XYZ9Z99").build();

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(existingVeiculoDatabase()));
        when(veiculoRepository.save(any(VeiculoDatabase.class))).thenThrow(new DataIntegrityViolationException("duplicate key"));

        GatewayException exception = assertThrows(GatewayException.class, () -> gateway.execute(veiculo));

        assertEquals(409, exception.getStatus());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        Veiculo veiculo = Veiculo.builder().id(1L).build();

        when(veiculoRepository.findById(1L)).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(veiculo));
    }
}