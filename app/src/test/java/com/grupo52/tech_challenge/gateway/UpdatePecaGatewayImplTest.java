package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import com.grupo52.tech_challenge.gateway.impl.UpdatePecaGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePecaGatewayImplTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private UpdatePecaGatewayImpl gateway;

    private ProdutoDatabase existingPecaDatabase() {
        return ProdutoDatabase.builder()
                .id(1L).sku("SKU-001").ean("7891234560001").nome("Pastilha de freio")
                .preco(new BigDecimal("35.00")).estoque(10).estoqueReservado(0).estoqueMinimo(2)
                .tipoProduto(TipoProduto.PECA).tipoPeca(TipoPeca.PASTILHA_FREIO).aplicacoes(new ArrayList<>())
                .build();
    }

    @Test
    void executeSucesso() throws GatewayException {
        Peca peca = Peca.builder().id(1L).preco(new BigDecimal("40.00")).aplicacoes(new ArrayList<>()).build();

        when(repository.findByIdAndTipoProduto(1L, TipoProduto.PECA)).thenReturn(Optional.of(existingPecaDatabase()));
        when(repository.save(any(ProdutoDatabase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Peca result = gateway.execute(peca);

        assertEquals(0, new BigDecimal("40.00").compareTo(result.getPreco()));
        assertEquals("SKU-001", result.getSku());
        verify(repository, times(1)).save(any(ProdutoDatabase.class));
    }

    @Test
    void executeNaoEncontradaLancaNotFoundGatewayException() {
        Peca peca = Peca.builder().id(1L).aplicacoes(new ArrayList<>()).build();

        when(repository.findByIdAndTipoProduto(1L, TipoProduto.PECA)).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(peca));

        verify(repository, never()).save(any());
    }

    @Test
    void executeSkuOuEanDuplicadoLancaGatewayException() {
        Peca peca = Peca.builder().id(1L).sku("SKU-999").aplicacoes(new ArrayList<>()).build();

        when(repository.findByIdAndTipoProduto(1L, TipoProduto.PECA)).thenReturn(Optional.of(existingPecaDatabase()));
        when(repository.save(any(ProdutoDatabase.class))).thenThrow(new DataIntegrityViolationException("duplicate key"));

        GatewayException exception = assertThrows(GatewayException.class, () -> gateway.execute(peca));

        assertEquals(409, exception.getStatus());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        Peca peca = Peca.builder().id(1L).aplicacoes(new ArrayList<>()).build();

        when(repository.findByIdAndTipoProduto(1L, TipoProduto.PECA)).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(peca));
    }
}