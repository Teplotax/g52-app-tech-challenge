package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Servico;
import com.grupo52.tech_challenge.gateway.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ServicoController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
class ServicoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CreateServicoGateway createServicoGateway;

    @MockitoBean
    private ListServicosGateway listServicosGateway;

    @MockitoBean
    private FindServicoGateway findServicoGateway;

    @MockitoBean
    private UpdateServicoGateway updateServicoGateway;

    @MockitoBean
    private DeleteServicoGateway deleteServicoGateway;

    private Servico servicoFixture() {
        return Servico.builder()
                .id(1L)
                .nome("Revisão de freios")
                .horasTecnicas(new BigDecimal("2.0"))
                .insumos(new ArrayList<>())
                .pecas(new ArrayList<>())
                .build();
    }

    @Test
    void createServicoSucesso() throws Exception {
        when(createServicoGateway.execute(any(Servico.class))).thenReturn(servicoFixture());

        mvc.perform(MockMvcRequestBuilders.post("/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Revisão de freios\",\"horasTecnicas\":2.0}"))
                .andExpect(status().isCreated());

        verify(createServicoGateway, times(1)).execute(any(Servico.class));
        verifyNoMoreInteractions(createServicoGateway);
    }

    @Test
    void listServicosSucesso() throws Exception {
        when(listServicosGateway.execute(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(servicoFixture())));

        mvc.perform(MockMvcRequestBuilders.get("/servicos"))
                .andExpect(status().isOk());

        verify(listServicosGateway, times(1)).execute(any(Pageable.class));
        verifyNoMoreInteractions(listServicosGateway);
    }

    @Test
    void findServicoSucesso() throws Exception {
        when(findServicoGateway.execute(any(Long.class))).thenReturn(servicoFixture());

        mvc.perform(MockMvcRequestBuilders.get("/servicos/{servicoId}", 1L))
                .andExpect(status().isOk());

        verify(findServicoGateway, times(1)).execute(1L);
        verifyNoMoreInteractions(findServicoGateway);
    }

    @Test
    void updateServicoSucesso() throws Exception {
        when(updateServicoGateway.execute(any(Servico.class))).thenReturn(servicoFixture());

        mvc.perform(MockMvcRequestBuilders.put("/servicos/{servicoId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Revisão atualizada\"}"))
                .andExpect(status().isOk());

        verify(updateServicoGateway, times(1)).execute(any(Servico.class));
        verifyNoMoreInteractions(updateServicoGateway);
    }

    @Test
    void deleteServicoSucesso() throws Exception {
        doNothing().when(deleteServicoGateway).execute(any(Long.class));

        mvc.perform(MockMvcRequestBuilders.delete("/servicos/{servicoId}", 1L))
                .andExpect(status().isNoContent());

        verify(deleteServicoGateway, times(1)).execute(1L);
        verifyNoMoreInteractions(deleteServicoGateway);
    }
}