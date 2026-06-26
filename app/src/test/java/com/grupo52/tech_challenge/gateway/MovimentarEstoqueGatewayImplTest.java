package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Produto;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import com.grupo52.tech_challenge.gateway.impl.MovimentarEstoqueGatewayImpl;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovimentarEstoqueGatewayImplTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private MovimentarEstoqueGatewayImpl gateway;

    private ProdutoDatabase produtoDatabase;

    @BeforeEach
    void setUp() {
        produtoDatabase = ProdutoDatabase.builder()
                .id(1L)
                .sku("SKU-001")
                .ean("7891234560001")
                .nome("Pastilha de freio")
                .preco(new BigDecimal("35.00"))
                .estoque(10)
                .estoqueReservado(0)
                .estoqueMinimo(2)
                .tipoProduto(TipoProduto.PECA)
                .aplicacoes(new ArrayList<>())
                .build();
    }

    @Test
    void entradaSucesso() throws GatewayException {
        ProdutoDatabase atualizado = ProdutoDatabase.builder()
                .id(1L).sku("SKU-001").ean("7891234560001").nome("Pastilha de freio")
                .preco(new BigDecimal("35.00")).estoque(15).estoqueReservado(0).estoqueMinimo(2)
                .tipoProduto(TipoProduto.PECA).aplicacoes(new ArrayList<>()).build();

        when(repository.findByEan("7891234560001")).thenReturn(Optional.of(produtoDatabase));
        when(repository.save(any(ProdutoDatabase.class))).thenReturn(atualizado);

        List<Produto> result = gateway.entrada(List.of("7891234560001"), List.of(5));

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findByEan("7891234560001");
        verify(repository, times(1)).save(any(ProdutoDatabase.class));
    }

    @Test
    void entradaEanNaoEncontradoLancaNotFoundGatewayException() {
        when(repository.findByEan("EAN_INVALIDO")).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class,
                () -> gateway.entrada(List.of("EAN_INVALIDO"), List.of(5)));

        verify(repository, times(1)).findByEan("EAN_INVALIDO");
        verify(repository, never()).save(any());
    }

    @Test
    void saidaSucesso() throws GatewayException {
        ProdutoDatabase atualizado = ProdutoDatabase.builder()
                .id(1L).sku("SKU-001").ean("7891234560001").nome("Pastilha de freio")
                .preco(new BigDecimal("35.00")).estoque(8).estoqueReservado(0).estoqueMinimo(2)
                .tipoProduto(TipoProduto.PECA).aplicacoes(new ArrayList<>()).build();

        when(repository.findByEan("7891234560001")).thenReturn(Optional.of(produtoDatabase));
        when(repository.save(any(ProdutoDatabase.class))).thenReturn(atualizado);

        List<Produto> result = gateway.saida(List.of("7891234560001"), List.of(2));

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository, times(1)).findByEan("7891234560001");
        verify(repository, times(1)).save(any(ProdutoDatabase.class));
    }

    @Test
    void saidaEstoqueInsuficienteLancaGatewayException422() {
        when(repository.findByEan("7891234560001")).thenReturn(Optional.of(produtoDatabase));

        GatewayException ex = assertThrows(GatewayException.class,
                () -> gateway.saida(List.of("7891234560001"), List.of(100)));

        assertEquals(422, ex.getStatus());
        verify(repository, never()).save(any());
    }

    @Test
    void saidaEanNaoEncontradoLancaNotFoundGatewayException() {
        when(repository.findByEan("EAN_INVALIDO")).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class,
                () -> gateway.saida(List.of("EAN_INVALIDO"), List.of(1)));

        verify(repository, never()).save(any());
    }
}