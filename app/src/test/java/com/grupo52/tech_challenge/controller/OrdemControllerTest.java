package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Enums.Status;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.CreateOrdemGateway;
import com.grupo52.tech_challenge.gateway.FindOrdemGateway;
import com.grupo52.tech_challenge.gateway.ListOrdemGateway;
import com.grupo52.tech_challenge.usecase.*;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = OrdemController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
class OrdemControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CreateOrdemGateway createOrdemGateway;

    @MockitoBean
    private CalculateOrdemPriceUseCase calculateOrdemPriceUseCase;

    @MockitoBean
    private FindOrdemGateway findOrdemGateway;

    @MockitoBean
    private ListOrdemGateway listOrdemGateway;

    @MockitoBean
    private EvaluateOrdemUseCase evaluateOrdemUseCase;

    @MockitoBean
    private AddServicosUseCase addServicosOSService;

    @MockitoBean
    private RequestApprovalUseCase requestApprovalUseCase;

    @MockitoBean
    private ApproveOrdemUseCase approveOrdemUseCase;

    @MockitoBean
    private ExecuteOrdemUseCase executeOrdemUseCase;

    @MockitoBean
    private FinalizeOrdemUseCase finalizeOrdemUseCase;

    @MockitoBean
    private CancelOrdemUseCase cancelOrdemUseCase;

    @MockitoBean
    private EntregarOrdemUseCase entregarOrdemUseCase;

    @Test
    void criarOSSucesso() throws Exception {
        when(createOrdemGateway.execute(any())).thenReturn(OrdemDeServicoFixture.recebida(1L));
        when(calculateOrdemPriceUseCase.calculateServicosDesejados(any())).thenReturn(OrdemDeServicoFixture.recebida(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"placa\":\"ABC1A23\",\"tagChave\":\"001\",\"servicosDesejados\":[1]}"))
                .andExpect(status().isCreated());

        verify(createOrdemGateway, times(1)).execute(any());
        verify(calculateOrdemPriceUseCase, times(1)).calculateServicosDesejados(any());
    }

    @Test
    void diagnosticarSucesso() throws Exception {
        when(evaluateOrdemUseCase.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.emDiagnostico(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/diagnosticar", 1L))
                .andExpect(status().isOk());

        verify(evaluateOrdemUseCase, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(evaluateOrdemUseCase);
    }

    @Test
    void adicionarServicosSucesso() throws Exception {
        when(addServicosOSService.execute(any())).thenReturn(OrdemDeServicoFixture.emDiagnostico(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/adicionarServicos", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"servicosNecessarios\":[2],\"justificativaNecessarios\":\"Desgaste identificado\"}"))
                .andExpect(status().isOk());

        verify(addServicosOSService, times(1)).execute(any());
        verifyNoMoreInteractions(addServicosOSService);
    }

    @Test
    void solicitarAprovacaoSucesso() throws Exception {
        when(requestApprovalUseCase.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/solicitarAprovacao", 1L))
                .andExpect(status().isOk());

        verify(requestApprovalUseCase, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(requestApprovalUseCase);
    }

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

    @Test
    void executarSucesso() throws Exception {
        when(executeOrdemUseCase.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/executar", 1L))
                .andExpect(status().isOk());

        verify(executeOrdemUseCase, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(executeOrdemUseCase);
    }

    @Test
    void finalizarSucesso() throws Exception {
        when(finalizeOrdemUseCase.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.finalizada(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/finalizar", 1L))
                .andExpect(status().isOk());

        verify(finalizeOrdemUseCase, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(finalizeOrdemUseCase);
    }

    @Test
    void cancelarSucesso() throws Exception {
        when(cancelOrdemUseCase.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.cancelada(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/cancelar", 1L))
                .andExpect(status().isOk());

        verify(cancelOrdemUseCase, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(cancelOrdemUseCase);
    }

    @Test
    void entregarSucesso() throws Exception {
        when(entregarOrdemUseCase.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.entregue(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/entregar", 1L))
                .andExpect(status().isOk());

        verify(entregarOrdemUseCase, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(entregarOrdemUseCase);
    }

    @Test
    void findOSSucesso() throws Exception {
        when(findOrdemGateway.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.emDiagnostico(1L));

        mvc.perform(MockMvcRequestBuilders.get("/ordensDeServico/{osId}", 1L))
                .andExpect(status().isOk());

        verify(findOrdemGateway, times(1)).execute(1L);
        verifyNoMoreInteractions(findOrdemGateway);
    }

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