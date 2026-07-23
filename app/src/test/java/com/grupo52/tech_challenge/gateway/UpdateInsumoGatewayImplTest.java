package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Enums.UnidadeDeMedida;
import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import com.grupo52.tech_challenge.gateway.impl.UpdateInsumoGatewayImpl;
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
class UpdateInsumoGatewayImplTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private UpdateInsumoGatewayImpl gateway;

    private ProdutoDatabase existingInsumoDatabase() {
        return ProdutoDatabase.builder()
                .id(1L).sku("SKU-002").ean("7891234560010").nome("Fluido de freio DOT 4")
                .preco(new BigDecimal("25.00")).estoque(5).estoqueReservado(0).estoqueMinimo(1)
                .quantidadeEmbalagem(new BigDecimal("1")).unidadeDeMedida(UnidadeDeMedida.L)
                .tipoProduto(TipoProduto.INSUMO).tipoInsumo(TipoInsumo.FLUIDO_FREIO).aplicacoes(new ArrayList<>())
                .build();
    }

    @Test
    void executeSucesso() throws GatewayException {
        Insumo insumo = Insumo.builder().id(1L).preco(new BigDecimal("28.00")).aplicacoes(new ArrayList<>()).build();

        when(repository.findByIdAndTipoProduto(1L, TipoProduto.INSUMO)).thenReturn(Optional.of(existingInsumoDatabase()));
        when(repository.save(any(ProdutoDatabase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Insumo result = gateway.execute(insumo);

        assertEquals(0, new BigDecimal("28.00").compareTo(result.getPreco()));
        assertEquals("SKU-002", result.getSku());
        verify(repository, times(1)).save(any(ProdutoDatabase.class));
    }

    @Test
    void executeNaoEncontradoLancaNotFoundGatewayException() {
        Insumo insumo = Insumo.builder().id(1L).aplicacoes(new ArrayList<>()).build();

        when(repository.findByIdAndTipoProduto(1L, TipoProduto.INSUMO)).thenReturn(Optional.empty());

        assertThrows(NotFoundGatewayException.class, () -> gateway.execute(insumo));

        verify(repository, never()).save(any());
    }

    @Test
    void executeSkuOuEanDuplicadoLancaGatewayException() {
        Insumo insumo = Insumo.builder().id(1L).sku("SKU-999").aplicacoes(new ArrayList<>()).build();

        when(repository.findByIdAndTipoProduto(1L, TipoProduto.INSUMO)).thenReturn(Optional.of(existingInsumoDatabase()));
        when(repository.save(any(ProdutoDatabase.class))).thenThrow(new DataIntegrityViolationException("duplicate key"));

        GatewayException exception = assertThrows(GatewayException.class, () -> gateway.execute(insumo));

        assertEquals(409, exception.getStatus());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        Insumo insumo = Insumo.builder().id(1L).aplicacoes(new ArrayList<>()).build();

        when(repository.findByIdAndTipoProduto(1L, TipoProduto.INSUMO)).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(insumo));
    }
}