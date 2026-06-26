package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Enums.TipoInsumo;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Insumo;
import com.grupo52.tech_challenge.gateway.CreateInsumoGateway;
import com.grupo52.tech_challenge.gateway.DeleteProdutoGateway;
import com.grupo52.tech_challenge.gateway.FindInsumoGateway;
import com.grupo52.tech_challenge.gateway.UpdateInsumoGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = InsumoController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
class InsumoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CreateInsumoGateway createInsumoGateway;

    @MockitoBean
    private FindInsumoGateway findInsumoGateway;

    @MockitoBean
    private UpdateInsumoGateway updateInsumoGateway;

    @MockitoBean
    private DeleteProdutoGateway deleteProdutoGateway;

    private Insumo insumoFixture() {
        return Insumo.builder()
                .id(1L).sku("SKU-001").ean("7891234560010").nome("Fluido DOT4")
                .preco(new BigDecimal("25.00")).estoque(5).tipoInsumo(TipoInsumo.FLUIDO_FREIO)
                .tipoProduto(TipoProduto.INSUMO).aplicacoes(new ArrayList<>())
                .build();
    }

    private static final String CREATE_INSUMO_BODY =
            "{\"sku\":\"SKU-001\",\"ean\":\"7891234560010\",\"nome\":\"Fluido DOT4\"," +
                    "\"preco\":25.00,\"tipoInsumo\":\"FLUIDO_FREIO\"," +
                    "\"aplicacoes\":[{\"modeloId\":1,\"anoInicio\":2018,\"anoFim\":2023,\"quantidade\":1}]}";

    @Test
    void createInsumoSucesso() throws Exception {
        when(createInsumoGateway.execute(any(Insumo.class))).thenReturn(insumoFixture());

        mvc.perform(MockMvcRequestBuilders.post("/produtos/insumos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_INSUMO_BODY))
                .andExpect(status().isCreated());

        verify(createInsumoGateway, times(1)).execute(any(Insumo.class));
        verifyNoMoreInteractions(createInsumoGateway);
    }

    @Test
    void findInsumoSucesso() throws Exception {
        when(findInsumoGateway.execute(any(Long.class))).thenReturn(insumoFixture());

        mvc.perform(MockMvcRequestBuilders.get("/produtos/insumos/{insumoId}", 1L))
                .andExpect(status().isOk());

        verify(findInsumoGateway, times(1)).execute(1L);
        verifyNoMoreInteractions(findInsumoGateway);
    }

    @Test
    void updateInsumoSucesso() throws Exception {
        when(updateInsumoGateway.execute(any(Insumo.class))).thenReturn(insumoFixture());

        mvc.perform(MockMvcRequestBuilders.put("/produtos/insumos/{insumoId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Fluido Atualizado\",\"aplicacoes\":[]}"))
                .andExpect(status().isOk());

        verify(updateInsumoGateway, times(1)).execute(any(Insumo.class));
        verifyNoMoreInteractions(updateInsumoGateway);
    }

    @Test
    void deleteInsumoSucesso() throws Exception {
        doNothing().when(deleteProdutoGateway).execute(any(Long.class), any(TipoProduto.class));

        mvc.perform(MockMvcRequestBuilders.delete("/produtos/insumos/{insumoId}", 1L))
                .andExpect(status().isNoContent());

        verify(deleteProdutoGateway, times(1)).execute(1L, TipoProduto.INSUMO);
        verifyNoMoreInteractions(deleteProdutoGateway);
    }
}