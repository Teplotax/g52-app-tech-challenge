package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.usecase.ApproveOrdemUseCase;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ApproveOrdemController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
class ApproveOrdemControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ApproveOrdemUseCase approveOrdemUseCase;

    @Test
    void aprovarTodosSucesso() throws Exception {
        when(approveOrdemUseCase.approveAll(any(Long.class))).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/aprovar", 1L))
                .andExpect(status().isOk());

        verify(approveOrdemUseCase, times(1)).approveAll(any(Long.class));
        verifyNoMoreInteractions(approveOrdemUseCase);
    }

    @Test
    void aprovarComListaVaziaCaiNoApproveAll() throws Exception {
        when(approveOrdemUseCase.approveAll(any(Long.class))).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/aprovar", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"servicosAprovados\":[]}"))
                .andExpect(status().isOk());

        verify(approveOrdemUseCase, times(1)).approveAll(any(Long.class));
        verifyNoMoreInteractions(approveOrdemUseCase);
    }

    @Test
    void aprovarParcialSucesso() throws Exception {
        when(approveOrdemUseCase.parcialApprove(any(Long.class), anyList())).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/aprovar", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"servicosAprovados\":[1,3]}"))
                .andExpect(status().isOk());

        verify(approveOrdemUseCase, times(1)).parcialApprove(any(Long.class), anyList());
        verifyNoMoreInteractions(approveOrdemUseCase);
    }
}