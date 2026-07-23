package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.usecase.RequestApprovalUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = RequestApprovalController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
class RequestApprovalControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private RequestApprovalUseCase requestApprovalUseCase;

    @Test
    void solicitarAprovacaoSucesso() throws Exception {
        when(requestApprovalUseCase.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/solicitarAprovacao", 1L))
                .andExpect(status().isOk());

        verify(requestApprovalUseCase, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(requestApprovalUseCase);
    }
}