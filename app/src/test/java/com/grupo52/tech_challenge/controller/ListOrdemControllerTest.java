package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.ListOrdemGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ListOrdemController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
class ListOrdemControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ListOrdemGateway listOrdemGateway;

    @Test
    void listOSSucesso() throws Exception {
        when(listOrdemGateway.execute(any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(OrdemDeServicoFixture.emDiagnostico(1L))));

        mvc.perform(MockMvcRequestBuilders.get("/ordensDeServico"))
                .andExpect(status().isOk());

        verify(listOrdemGateway, times(1)).execute(any(), any(), any(), any(), any(), any(), any(Pageable.class));
        verifyNoMoreInteractions(listOrdemGateway);
    }

    @Test
    void listOSComFiltrosSucesso() throws Exception {
        when(listOrdemGateway.execute(any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(OrdemDeServicoFixture.emDiagnostico(1L))));

        mvc.perform(MockMvcRequestBuilders.get("/ordensDeServico")
                        .param("placa", "ABC1D23")
                        .param("status", Status.EM_DIAGNOSTICO.name())
                        .param("dataInicio", "2024-01-01")
                        .param("dataFim", "2024-12-31"))
                .andExpect(status().isOk());

        verify(listOrdemGateway, times(1)).execute(any(), any(), any(), any(), any(), any(), any(Pageable.class));
        verifyNoMoreInteractions(listOrdemGateway);
    }
}