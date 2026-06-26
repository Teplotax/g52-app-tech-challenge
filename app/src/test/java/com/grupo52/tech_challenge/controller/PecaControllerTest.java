package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Enums.TipoPeca;
import com.grupo52.tech_challenge.domain.Enums.TipoProduto;
import com.grupo52.tech_challenge.domain.Peca;
import com.grupo52.tech_challenge.gateway.CreatePecaGateway;
import com.grupo52.tech_challenge.gateway.DeleteProdutoGateway;
import com.grupo52.tech_challenge.gateway.FindPecaGateway;
import com.grupo52.tech_challenge.gateway.UpdatePecaGateway;
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
@WebMvcTest(controllers = PecaController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
class PecaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CreatePecaGateway createPecaGateway;

    @MockitoBean
    private FindPecaGateway findPecaGateway;

    @MockitoBean
    private UpdatePecaGateway updatePecaGateway;

    @MockitoBean
    private DeleteProdutoGateway deleteProdutoGateway;

    private Peca pecaFixture() {
        return Peca.builder()
                .id(1L).sku("SKU-001").ean("7891234560001").nome("Pastilha de freio")
                .preco(new BigDecimal("35.00")).estoque(10).tipoPeca(TipoPeca.PASTILHA_FREIO)
                .tipoProduto(TipoProduto.PECA).aplicacoes(new ArrayList<>())
                .build();
    }

    private static final String CREATE_PECA_BODY =
            "{\"sku\":\"SKU-001\",\"ean\":\"7891234560001\",\"nome\":\"Pastilha de freio\"," +
                    "\"preco\":35.00,\"tipoPeca\":\"PASTILHA_FREIO\"," +
                    "\"aplicacoes\":[{\"modeloId\":1,\"anoInicio\":2018,\"anoFim\":2023}]}";

    @Test
    void createPecaSucesso() throws Exception {
        when(createPecaGateway.execute(any(Peca.class))).thenReturn(pecaFixture());

        mvc.perform(MockMvcRequestBuilders.post("/produtos/pecas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_PECA_BODY))
                .andExpect(status().isCreated());

        verify(createPecaGateway, times(1)).execute(any(Peca.class));
        verifyNoMoreInteractions(createPecaGateway);
    }

    @Test
    void findPecaSucesso() throws Exception {
        when(findPecaGateway.execute(any(Long.class))).thenReturn(pecaFixture());

        mvc.perform(MockMvcRequestBuilders.get("/produtos/pecas/{pecaId}", 1L))
                .andExpect(status().isOk());

        verify(findPecaGateway, times(1)).execute(1L);
        verifyNoMoreInteractions(findPecaGateway);
    }

    @Test
    void updatePecaSucesso() throws Exception {
        when(updatePecaGateway.execute(any(Peca.class))).thenReturn(pecaFixture());

        mvc.perform(MockMvcRequestBuilders.put("/produtos/pecas/{pecaId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Pastilha Atualizada\",\"aplicacoes\":[]}"))
                .andExpect(status().isOk());

        verify(updatePecaGateway, times(1)).execute(any(Peca.class));
        verifyNoMoreInteractions(updatePecaGateway);
    }

    @Test
    void deletePecaSucesso() throws Exception {
        doNothing().when(deleteProdutoGateway).execute(any(Long.class), any(TipoProduto.class));

        mvc.perform(MockMvcRequestBuilders.delete("/produtos/pecas/{pecaId}", 1L))
                .andExpect(status().isNoContent());

        verify(deleteProdutoGateway, times(1)).execute(1L, TipoProduto.PECA);
        verifyNoMoreInteractions(deleteProdutoGateway);
    }
}