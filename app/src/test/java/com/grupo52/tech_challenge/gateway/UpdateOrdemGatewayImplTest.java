package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.domain.OrdemServico;
import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.domain.StatusChange;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ClienteDatabase;
import com.grupo52.tech_challenge.gateway.database.model.EnderecoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.MarcaDatabase;
import com.grupo52.tech_challenge.gateway.database.model.ModeloDatabase;
import com.grupo52.tech_challenge.gateway.database.model.OrdemDeServicoDatabase;
import com.grupo52.tech_challenge.gateway.database.model.StatusChangeDatabase;
import com.grupo52.tech_challenge.gateway.database.model.VeiculoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.OrdemDeServicoRepository;
import com.grupo52.tech_challenge.gateway.impl.UpdateOrdemGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateOrdemGatewayImplTest {

    @Mock
    private OrdemDeServicoRepository ordemDeServicoRepository;

    @InjectMocks
    private UpdateOrdemGatewayImpl gateway;

    private OrdemDeServicoDatabase existingOsDatabase() {
        MarcaDatabase marca = MarcaDatabase.builder().id(1L).nome("Toyota").build();
        ModeloDatabase modelo = ModeloDatabase.builder().id(1L).nome("Corolla").marca(marca).build();
        VeiculoDatabase veiculo = VeiculoDatabase.builder()
                .id(1L).placa("ABC1D23").ano(2020).cor("Prata")
                .modelo(modelo).cliente(ClienteDatabase.builder().id(1L).endereco(EnderecoDatabase.builder().build()).build())
                .build();

        OrdemDeServicoDatabase existing = OrdemDeServicoDatabase.builder()
                .id(1L).status(Status.RECEBIDA)
                .cliente(veiculo.getCliente()).veiculo(veiculo)
                .tagChave("001")
                .servicosDesejados(new ArrayList<>())
                .servicosNecessarios(new ArrayList<>())
                .servicosAdicionais(new ArrayList<>())
                .historico(new ArrayList<>())
                .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .build();

        StatusChangeDatabase historicoExistente = StatusChangeDatabase.builder()
                .id(10L).ordemDeServico(existing).status(Status.RECEBIDA)
                .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .build();
        existing.getHistorico().add(historicoExistente);

        return existing;
    }

    @Test
    void executeSucessoAtualizaStatusEPreservaClienteVeiculoCreatedAt() throws GatewayException {
        Ordem os = Ordem.builder().id(1L).status(Status.EM_DIAGNOSTICO).build();

        when(ordemDeServicoRepository.findById(1L)).thenReturn(Optional.of(existingOsDatabase()));
        when(ordemDeServicoRepository.save(any(OrdemDeServicoDatabase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = gateway.execute(os);

        assertEquals(Status.EM_DIAGNOSTICO, result.getStatus());
        assertEquals("001", result.getTagChave());
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), result.getCriadaEm());
    }

    @Test
    void executeNaoEncontradaLancaGatewayException() {
        Ordem os = Ordem.builder().id(99L).build();

        when(ordemDeServicoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(GatewayException.class, () -> gateway.execute(os));

        verify(ordemDeServicoRepository, never()).save(any());
    }

    @Test
    void executeSubstituiServicosDesejadosQuandoInformados() throws GatewayException {
        OrdemServico novoServico = OrdemServico.builder()
                .servico(Servico.builder().id(5L).build())
                .aprovado(false)
                .build();
        Ordem os = Ordem.builder().id(1L).servicosDesejados(List.of(novoServico)).build();

        when(ordemDeServicoRepository.findById(1L)).thenReturn(Optional.of(existingOsDatabase()));
        when(ordemDeServicoRepository.save(any(OrdemDeServicoDatabase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = gateway.execute(os);

        assertEquals(1, result.getServicosDesejados().size());
    }

    @Test
    void executeMantemServicosDesejadosVaziosQuandoNaoInformados() throws GatewayException {
        Ordem os = Ordem.builder().id(1L).status(Status.EM_DIAGNOSTICO).build();

        when(ordemDeServicoRepository.findById(1L)).thenReturn(Optional.of(existingOsDatabase()));
        when(ordemDeServicoRepository.save(any(OrdemDeServicoDatabase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ordem result = gateway.execute(os);

        assertTrue(result.getServicosDesejados().isEmpty());
    }

    @Test
    void executePreservaHistoricoExistenteEAdicionaNovo() throws GatewayException {
        StatusChange novoStatusChange = StatusChange.builder().status(Status.EM_DIAGNOSTICO).build();
        Ordem os = Ordem.builder().id(1L).status(Status.EM_DIAGNOSTICO).historico(List.of(novoStatusChange)).build();

        ArgumentCaptor<OrdemDeServicoDatabase> captor = ArgumentCaptor.forClass(OrdemDeServicoDatabase.class);

        when(ordemDeServicoRepository.findById(1L)).thenReturn(Optional.of(existingOsDatabase()));
        when(ordemDeServicoRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        gateway.execute(os);

        assertEquals(2, captor.getValue().getHistorico().size());
    }

    @Test
    void executeNaoDuplicaHistoricoJaPersistido() throws GatewayException {
        StatusChange statusChangeJaSalvo = StatusChange.builder().id(10L).status(Status.RECEBIDA).build();
        Ordem os = Ordem.builder().id(1L).status(Status.RECEBIDA).historico(List.of(statusChangeJaSalvo)).build();

        ArgumentCaptor<OrdemDeServicoDatabase> captor = ArgumentCaptor.forClass(OrdemDeServicoDatabase.class);

        when(ordemDeServicoRepository.findById(1L)).thenReturn(Optional.of(existingOsDatabase()));
        when(ordemDeServicoRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        gateway.execute(os);

        assertEquals(1, captor.getValue().getHistorico().size());
    }
}