package com.grupo52.tech_challenge.controller;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Endereco;
import com.grupo52.tech_challenge.domain.Enums.TipoDocumento;
import com.grupo52.tech_challenge.domain.Marca;
import com.grupo52.tech_challenge.domain.Modelo;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.gateway.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ClienteController.class,
        excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
public class ClienteControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CreateClienteGateway createClienteGateway;

    @MockitoBean
    private FindClienteGateway findClienteGateway;

    @MockitoBean
    private FindClienteByDocumentGateway findClienteByDocumentGateway;

    @MockitoBean
    private ListClientesGateway listClientesGateway;

    @MockitoBean
    private ListVeiculosByClienteGateway listVeiculosByClienteGateway;

    @MockitoBean
    private UpdateClienteGateway updateClienteGateway;

    @MockitoBean
    private DeleteClienteGateway deleteClienteGateway;

    private static final String VALID_CPF = "12345678909";

    private static final String CREATE_CLIENTE_BODY =
            "{" +
                    "\"nome\":\"João Silva\"," +
                    "\"nomeSocial\":\"João Silva\"," +
                    "\"tipoDocumento\":\"CPF\"," +
                    "\"documento\":\"" + VALID_CPF + "\"," +
                    "\"email\":\"joao@email.com\"," +
                    "\"telefone\":\"11999999999\"," +
                    "\"contatoWhatsApp\":true," +
                    "\"endereco\":{" +
                    "\"logradouro\":\"Rua das Flores\"," +
                    "\"numero\":\"123\"," +
                    "\"bairro\":\"Centro\"," +
                    "\"cidade\":\"São Paulo\"," +
                    "\"uf\":\"SP\"," +
                    "\"cep\":\"01310100\"" +
                    "}" +
                    "}";

    private Cliente clienteFixture() {
        return Cliente.builder()
                .id(1L)
                .nomeSocial("João Silva")
                .nome("João Silva")
                .tipoDocumento(TipoDocumento.CPF)
                .documento(VALID_CPF)
                .email("joao@email.com")
                .telefone("11999999999")
                .contatoWhatsApp(true)
                .endereco(Endereco.builder()
                        .logradouro("Rua das Flores").numero("123")
                        .bairro("Centro").cidade("São Paulo").uf("SP").cep("01310100")
                        .build())
                .build();
    }

    private Veiculo veiculoFixture() {
        return Veiculo.builder()
                .id(1L).placa("ABC1D23").cor("Prata").ano(2020).clienteId(1L)
                .marca(Marca.builder().id(1L).nome("Toyota").build())
                .modelo(Modelo.builder().id(1L).nome("Corolla").build())
                .build();
    }

    @Test
    void createClienteSucesso() throws Exception {
        when(createClienteGateway.execute(any(Cliente.class))).thenReturn(clienteFixture());

        mvc.perform(MockMvcRequestBuilders.post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_CLIENTE_BODY))
                .andExpect(status().isCreated());

        verify(createClienteGateway, times(1)).execute(any(Cliente.class));
        verifyNoMoreInteractions(createClienteGateway);
    }

    @Test
    void findClienteSucesso() throws Exception {
        when(findClienteGateway.execute(any(Long.class))).thenReturn(clienteFixture());

        mvc.perform(MockMvcRequestBuilders.get("/clientes/{clienteId}", 1L))
                .andExpect(status().isOk());

        verify(findClienteGateway, times(1)).execute(1L);
        verifyNoMoreInteractions(findClienteGateway);
    }

    @Test
    void findClienteByDocumentoSucesso() throws Exception {
        when(findClienteByDocumentGateway.execute(any(String.class))).thenReturn(clienteFixture());

        mvc.perform(MockMvcRequestBuilders.get("/clientes/documento/{documento}", VALID_CPF))
                .andExpect(status().isOk());

        verify(findClienteByDocumentGateway, times(1)).execute(VALID_CPF);
        verifyNoMoreInteractions(findClienteByDocumentGateway);
    }

    @Test
    void listClientesSucesso() throws Exception {
        when(listClientesGateway.execute(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(clienteFixture())));

        mvc.perform(MockMvcRequestBuilders.get("/clientes"))
                .andExpect(status().isOk());

        verify(listClientesGateway, times(1)).execute(any(Pageable.class));
        verifyNoMoreInteractions(listClientesGateway);
    }

    @Test
    void listVeiculosByClienteSucesso() throws Exception {
        when(listVeiculosByClienteGateway.execute(any(Long.class)))
                .thenReturn(List.of(veiculoFixture()));

        mvc.perform(MockMvcRequestBuilders.get("/clientes/{clienteId}/veiculos", 1L))
                .andExpect(status().isOk());

        verify(listVeiculosByClienteGateway, times(1)).execute(1L);
        verifyNoMoreInteractions(listVeiculosByClienteGateway);
    }

    @Test
    void updateClienteSucesso() throws Exception {
        when(updateClienteGateway.execute(any(Cliente.class))).thenReturn(clienteFixture());

        mvc.perform(MockMvcRequestBuilders.put("/clientes/{clienteId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeSocial\":\"João Atualizado\"}"))
                .andExpect(status().isOk());

        verify(updateClienteGateway, times(1)).execute(any(Cliente.class));
        verifyNoMoreInteractions(updateClienteGateway);
    }

    @Test
    void deleteClienteSucesso() throws Exception {
        doNothing().when(deleteClienteGateway).execute(any(Long.class));

        mvc.perform(MockMvcRequestBuilders.delete("/clientes/{clienteId}", 1L))
                .andExpect(status().isNoContent());

        verify(deleteClienteGateway, times(1)).execute(1L);
        verifyNoMoreInteractions(deleteClienteGateway);
    }
}