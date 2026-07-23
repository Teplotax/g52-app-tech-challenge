package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Modelo;
import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import com.grupo52.tech_challenge.gateway.impl.FindPecaByVeiculoGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindPecaByVeiculoGatewayImplTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private FindPecaByVeiculoGatewayImpl gateway;

    @Test
    void executeSucesso() throws GatewayException {
        Veiculo veiculo = Veiculo.builder().modelo(Modelo.builder().id(1L).build()).ano(2020).build();

        ProdutoDatabase pecaDatabase = ProdutoDatabase.builder()
                .id(1L).sku("SKU-001").ean("7891234560001").nome("Pastilha de freio")
                .preco(new BigDecimal("35.00")).estoque(10).estoqueReservado(0).estoqueMinimo(2)
                .tipoProduto(TipoProduto.PECA).tipoPeca(TipoPeca.PASTILHA_FREIO).aplicacoes(new ArrayList<>())
                .build();

        when(repository.findAllByTipoPecaAndAplicacoesModeloIdAndAplicacoesAnoInicioLessThanEqualAndAplicacoesAnoFimGreaterThanEqual(
                TipoPeca.PASTILHA_FREIO, 1L, 2020, 2020))
                .thenReturn(List.of(pecaDatabase));

        List<Peca> result = gateway.execute(TipoPeca.PASTILHA_FREIO, veiculo);

        assertEquals(1, result.size());
        assertEquals("SKU-001", result.get(0).getSku());
    }

    @Test
    void executeSemResultadosRetornaListaVazia() throws GatewayException {
        Veiculo veiculo = Veiculo.builder().modelo(Modelo.builder().id(1L).build()).ano(2020).build();

        when(repository.findAllByTipoPecaAndAplicacoesModeloIdAndAplicacoesAnoInicioLessThanEqualAndAplicacoesAnoFimGreaterThanEqual(
                TipoPeca.PASTILHA_FREIO, 1L, 2020, 2020))
                .thenReturn(List.of());

        List<Peca> result = gateway.execute(TipoPeca.PASTILHA_FREIO, veiculo);

        assertTrue(result.isEmpty());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        Veiculo veiculo = Veiculo.builder().modelo(Modelo.builder().id(1L).build()).ano(2020).build();

        when(repository.findAllByTipoPecaAndAplicacoesModeloIdAndAplicacoesAnoInicioLessThanEqualAndAplicacoesAnoFimGreaterThanEqual(
                TipoPeca.PASTILHA_FREIO, 1L, 2020, 2020))
                .thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(TipoPeca.PASTILHA_FREIO, veiculo));
    }
}