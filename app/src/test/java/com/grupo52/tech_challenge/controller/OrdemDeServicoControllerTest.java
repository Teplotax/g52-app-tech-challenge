package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.fixture.OrdemDeServicoFixture;
import com.grupo52.tech_challenge.gateway.CreateOSGateway;
import com.grupo52.tech_challenge.gateway.FindOSGateway;
import com.grupo52.tech_challenge.gateway.ListOSGateway;
import com.grupo52.tech_challenge.service.AddServicosService;
import com.grupo52.tech_challenge.service.CalculateOSPriceService;
import com.grupo52.tech_challenge.service.EvaluateOSService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(
        controllers = OrdemDeServicoController.class
)
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

    @Test
    public void diagnosticarSucesso() throws Exception {
        Long osId = 1L;

        when(evaluateOSService.execute(any(Long.class))).thenReturn(OrdemDeServicoFixture.emDiagnostico(osId));

        mvc.perform(MockMvcRequestBuilders.post("/ordensDeServico/{osId}/diagnosticar", osId)
                        .header("x-correlationid", "9491b617-43e6-4667-9710-a6b51516744a"))
                .andExpect(status().isOk());

        verify(evaluateOSService, times(1)).execute(any(Long.class));
        verifyNoMoreInteractions(evaluateOSService);
    }
}