package com.grupo52.tech_challenge.gateway;

import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.gateway.database.model.ProdutoDatabase;
import com.grupo52.tech_challenge.gateway.database.repository.ProdutoRepository;
import com.grupo52.tech_challenge.gateway.impl.CreateInsumoGatewayImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateInsumoGatewayImplTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private CreateInsumoGatewayImpl gateway;

    @Test
    void executeSucesso() throws GatewayException {
        Insumo insumo = Insumo.builder()
                .sku("SKU-002").ean("7891234560010").nome("Fluido de freio DOT 4")
                .preco(new BigDecimal("25.00")).estoque(5).estoqueMinimo(1)
                .tipoInsumo(TipoInsumo.FLUIDO_FREIO).aplicacoes(new ArrayList<>())
                .build();

        ProdutoDatabase salvo = ProdutoDatabase.builder()
                .id(1L).sku("SKU-002").ean("7891234560010").nome("Fluido de freio DOT 4")
                .preco(new BigDecimal("25.00")).estoque(5).estoqueReservado(0).estoqueMinimo(1)
                .tipoInsumo(TipoInsumo.FLUIDO_FREIO).aplicacoes(new ArrayList<>())
                .build();

        when(repository.save(any(ProdutoDatabase.class))).thenReturn(salvo);

        Insumo result = gateway.execute(insumo);

        assertNotNull(result);
        assertEquals("SKU-002", result.getSku());
        verify(repository, times(1)).save(any(ProdutoDatabase.class));
    }

    @Test
    void executeSkuOuEanDuplicadoLancaGatewayException() {
        Insumo insumo = Insumo.builder().sku("SKU-002").ean("7891234560010").aplicacoes(new ArrayList<>()).build();

        when(repository.save(any(ProdutoDatabase.class))).thenThrow(new DataIntegrityViolationException("duplicate key"));

        GatewayException exception = assertThrows(GatewayException.class, () -> gateway.execute(insumo));

        assertEquals(409, exception.getStatus());
    }

    @Test
    void executeErroInesperadoLancaGatewayException() {
        Insumo insumo = Insumo.builder().sku("SKU-002").ean("7891234560010").aplicacoes(new ArrayList<>()).build();

        when(repository.save(any(ProdutoDatabase.class))).thenThrow(new RuntimeException("erro"));

        assertThrows(GatewayException.class, () -> gateway.execute(insumo));
    }
}