package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.CreateOSGateway;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.ListOSGateway;
import com.grupo52.tech_challenge.service.*;
import com.grupo52.tech_challenge.service.EntregarOSService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = OrdemDeServicoController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
public class OrdemDeServicoControllerTest {

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
    @WithMockUser
    public void diagnosticarSucesso() throws Exception {
        Long osId = 1L;

        when(evaluateOSService.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.emDiagnostico(osId));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/diagnosticar", osId)
                        .header("x-correlationid", "9491b617-43e6-4667-9710-a6b51516744a"))
                .andExpect(status().isOk());

        verify(evaluateOSService, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(evaluateOSService);
    }

    @Test
    @WithMockUser
    public void solicitarAprovacaoSucesso() throws Exception {
        Long osId = 1L;

        when(requestApprovalService.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(osId));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/solicitarAprovacao", osId)
                        .header("x-correlationid", "9491b617-43e6-4667-9710-a6b51516744a"))
                .andExpect(status().isOk());

        verify(requestApprovalService, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(requestApprovalService);
    }

    @Test
    @WithMockUser
    public void aprovarTodosSucesso() throws Exception {
        Long osId = 1L;

        when(approveOSService.approveAll(any(Long.class))).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(osId));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/aprovar", osId)
                        .header("x-correlationid", "9491b617-43e6-4667-9710-a6b51516744a"))
                .andExpect(status().isOk());

        verify(approveOSService, times(1)).approveAll(any(Long.class));
        verifyNoMoreInteractions(approveOSService);
    }

    @Test
    @WithMockUser
    public void aprovarParcialSucesso() throws Exception {
        Long osId = 1L;

        when(approveOSService.parcialApprove(any(Long.class), anyList())).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(osId));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/aprovar", osId)
                        .header("x-correlationid", "9491b617-43e6-4667-9710-a6b51516744a")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"servicosAprovados\":[1,3]}"))
                .andExpect(status().isOk());

        verify(approveOSService, times(1)).parcialApprove(any(Long.class), anyList());
        verifyNoMoreInteractions(approveOSService);
    }

    @Test
    @WithMockUser
    public void executarSucesso() throws Exception {
        Long osId = 1L;

        when(executeOSService.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.aguardandoAprovacao(osId));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/executar", osId)
                        .header("x-correlationid", "9491b617-43e6-4667-9710-a6b51516744a"))
                .andExpect(status().isOk());

        verify(executeOSService, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(executeOSService);
    }

    @Test
    @WithMockUser
    public void finalizarSucesso() throws Exception {
        Long osId = 1L;

        when(finalizeOSService.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.finalizada(osId));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/finalizar", osId)
                        .header("x-correlationid", "9491b617-43e6-4667-9710-a6b51516744a"))
                .andExpect(status().isOk());

        verify(finalizeOSService, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(finalizeOSService);
    }

    @Test
    @WithMockUser
    public void cancelarSucesso() throws Exception {
        Long osId = 1L;

        when(cancelOSService.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.cancelada(osId));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/cancelar", osId)
                        .header("x-correlationid", "9491b617-43e6-4667-9710-a6b51516744a"))
                .andExpect(status().isOk());

        verify(cancelOSService, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(cancelOSService);
    }

    @Test
    @WithMockUser
    public void entregarSucesso() throws Exception {
        Long osId = 1L;

        when(entregarOSService.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.entregue(osId));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/entregar", osId)
                        .header("x-correlationid", "9491b617-43e6-4667-9710-a6b51516744a"))
                .andExpect(status().isOk());

        verify(entregarOSService, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(entregarOSService);
    }
}