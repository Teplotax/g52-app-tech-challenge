package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Enums.StatusOS;
import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.CreateOSGateway;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.ListOSGateway;
import com.grupo52.tech_challenge.service.*;
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
@WebMvcTest(controllers = OrdemDeServicoController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
class OrdemDeServicoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CreateOSGateway createOSGateway;

    @MockitoBean
    private CalculateOSPriceService calculateOSPriceService;

    @MockitoBean
    private FindOSGateway findOSGateway;

    @MockitoBean
    private ListOSGateway listOSGateway;

    @MockitoBean
    private EvaluateOSService evaluateOSService;

    @MockitoBean
    private AddServicosService addServicosOSService;

    @MockitoBean
    private RequestApprovalService requestApprovalService;

    @MockitoBean
    private ApproveOSService approveOSService;

    @MockitoBean
    private ExecuteOSService executeOSService;

    @MockitoBean
    private FinalizeOSService finalizeOSService;

    @MockitoBean
    private CancelOSService cancelOSService;

    @MockitoBean
    private EntregarOSService entregarOSService;

    @Test
    void criarOSSucesso() throws Exception {
        when(createOSGateway.execute(any())).thenReturn(OrdemDeServicoFixture.recebida(1L));
        when(calculateOSPriceService.calculateServicosDesejados(any())).thenReturn(OrdemDeServicoFixture.recebida(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"placa\":\"ABC1A23\",\"tagChave\":\"001\",\"servicosDesejados\":[1]}"))
                .andExpect(status().isCreated());

        verify(createOSGateway, times(1)).execute(any());
        verify(calculateOSPriceService, times(1)).calculateServicosDesejados(any());
    }

    @Test
    void diagnosticarSucesso() throws Exception {
        when(evaluateOSService.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.emDiagnostico(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/diagnosticar", 1L))
                .andExpect(status().isOk());

        verify(evaluateOSService, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(evaluateOSService);
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
        when(requestApprovalService.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/solicitarAprovacao", 1L))
                .andExpect(status().isOk());

        verify(requestApprovalService, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(requestApprovalService);
    }

    @Test
    void aprovarTodosSucesso() throws Exception {
        when(approveOSService.approveAll(any(Long.class))).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/aprovar", 1L))
                .andExpect(status().isOk());

        verify(approveOSService, times(1)).approveAll(any(Long.class));
        verifyNoMoreInteractions(approveOSService);
    }

    @Test
    void aprovarComListaVaziaCaiNoApproveAll() throws Exception {
        when(approveOSService.approveAll(any(Long.class))).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/aprovar", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"servicosAprovados\":[]}"))
                .andExpect(status().isOk());

        verify(approveOSService, times(1)).approveAll(any(Long.class));
        verifyNoMoreInteractions(approveOSService);
    }

    @Test
    void aprovarParcialSucesso() throws Exception {
        when(approveOSService.parcialApprove(any(Long.class), anyList())).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/aprovar", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"servicosAprovados\":[1,3]}"))
                .andExpect(status().isOk());

        verify(approveOSService, times(1)).parcialApprove(any(Long.class), anyList());
        verifyNoMoreInteractions(approveOSService);
    }

    @Test
    void executarSucesso() throws Exception {
        when(executeOSService.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/executar", 1L))
                .andExpect(status().isOk());

        verify(executeOSService, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(executeOSService);
    }

    @Test
    void finalizarSucesso() throws Exception {
        when(finalizeOSService.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.finalizada(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/finalizar", 1L))
                .andExpect(status().isOk());

        verify(finalizeOSService, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(finalizeOSService);
    }

    @Test
    void cancelarSucesso() throws Exception {
        when(cancelOSService.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.cancelada(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/cancelar", 1L))
                .andExpect(status().isOk());

        verify(cancelOSService, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(cancelOSService);
    }

    @Test
    void entregarSucesso() throws Exception {
        when(entregarOSService.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.entregue(1L));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/entregar", 1L))
                .andExpect(status().isOk());

        verify(entregarOSService, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(entregarOSService);
    }

    @Test
    void findOSSucesso() throws Exception {
        when(findOSGateway.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.emDiagnostico(1L));

        mvc.perform(MockMvcRequestBuilders.get("/ordensDeServico/{osId}", 1L))
                .andExpect(status().isOk());

        verify(findOSGateway, times(1)).execute(1L);
        verifyNoMoreInteractions(findOSGateway);
    }

    @Test
    void listOSSucesso() throws Exception {
        when(listOSGateway.execute(any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(OrdemDeServicoFixture.emDiagnostico(1L))));

        mvc.perform(MockMvcRequestBuilders.get("/ordensDeServico"))
                .andExpect(status().isOk());

        verify(listOSGateway, times(1)).execute(any(), any(), any(), any(), any(), any(), any(Pageable.class));
        verifyNoMoreInteractions(listOSGateway);
    }

    @Test
    void listOSComFiltrosSucesso() throws Exception {
        when(listOSGateway.execute(any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(OrdemDeServicoFixture.emDiagnostico(1L))));

        mvc.perform(MockMvcRequestBuilders.get("/ordensDeServico")
                        .param("placa", "ABC1D23")
                        .param("status", StatusOS.EM_DIAGNOSTICO.name())
                        .param("dataInicio", "2024-01-01")
                        .param("dataFim", "2024-12-31"))
                .andExpect(status().isOk());

        verify(listOSGateway, times(1)).execute(any(), any(), any(), any(), any(), any(), any(Pageable.class));
        verifyNoMoreInteractions(listOSGateway);
    }
}