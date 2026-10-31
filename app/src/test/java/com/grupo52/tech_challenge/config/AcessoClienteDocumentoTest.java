package com.grupo52.tech_challenge.config;

import com.grupo52.tech_challenge.controller.ClienteController;
import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Endereco;
import com.grupo52.tech_challenge.domain.Marca;
import com.grupo52.tech_challenge.domain.Modelo;
import com.grupo52.tech_challenge.domain.Veiculo;
import com.grupo52.tech_challenge.domain.Enums.TipoDocumento;
import com.grupo52.tech_challenge.gateway.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// token de cliente (cpf) só consulta o próprio cadastro e os próprios veículos por documento
@WebMvcTest(controllers = ClienteController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.security.jwt.jwk-set-uri=http://localhost/.well-known/jwks.json")
class AcessoClienteDocumentoTest {

    private static final String CPF_MARIA = "55563271064";
    private static final String CPF_DOUGLAS = "84673421027";

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

    private JwtRequestPostProcessor cliente(String cpf) {
        return jwt().jwt(j -> j.subject("1").claim("cpf", cpf))
                .authorities(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }

    private JwtRequestPostProcessor admin() {
        return jwt().jwt(j -> j.subject("g52-oficina-admin"))
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private Cliente clienteComDocumento(String documento) {
        return Cliente.builder()
                .id(1L)
                .nomeSocial("Maria Oliveira")
                .nome("Marcos Antônio Oliveira")
                .tipoDocumento(TipoDocumento.CPF)
                .documento(documento)
                .email("maria.oliveira@email.com")
                .telefone("11988887777")
                .contatoWhatsApp(true)
                .endereco(Endereco.builder()
                        .logradouro("Avenida Brasil").numero("456")
                        .bairro("Jardim América").cidade("São Paulo").uf("SP").cep("01432000")
                        .build())
                .build();
    }

    @Test
    void clienteConsultaProprioCadastro() throws Exception {
        when(findClienteByDocumentGateway.execute(CPF_MARIA)).thenReturn(clienteComDocumento(CPF_MARIA));

        mvc.perform(MockMvcRequestBuilders.get("/clientes/documento/{documento}", CPF_MARIA).with(cliente(CPF_MARIA)))
                .andExpect(status().isOk());
    }

    @Test
    void clienteNaoConsultaCadastroDeOutroCliente() throws Exception {
        when(findClienteByDocumentGateway.execute(CPF_DOUGLAS)).thenReturn(clienteComDocumento(CPF_DOUGLAS));

        mvc.perform(MockMvcRequestBuilders.get("/clientes/documento/{documento}", CPF_DOUGLAS).with(cliente(CPF_MARIA)))
                .andExpect(status().isNotFound());
    }

    @Test
    void adminConsultaQualquerCadastro() throws Exception {
        when(findClienteByDocumentGateway.execute(CPF_DOUGLAS)).thenReturn(clienteComDocumento(CPF_DOUGLAS));

        mvc.perform(MockMvcRequestBuilders.get("/clientes/documento/{documento}", CPF_DOUGLAS).with(admin()))
                .andExpect(status().isOk());
    }

    private Veiculo veiculoDoCliente() {
        return Veiculo.builder()
                .id(1L).placa("ABC1A23").cor("Prata").ano(2020).clienteId(1L)
                .marca(Marca.builder().id(1L).nome("Toyota").build())
                .modelo(Modelo.builder().id(1L).nome("Corolla").build())
                .build();
    }

    @Test
    void clienteListaProprioVeiculos() throws Exception {
        when(findClienteByDocumentGateway.execute(CPF_MARIA)).thenReturn(clienteComDocumento(CPF_MARIA));
        when(listVeiculosByClienteGateway.execute(1L)).thenReturn(List.of(veiculoDoCliente()));

        mvc.perform(MockMvcRequestBuilders.get("/clientes/documento/{documento}/veiculos", CPF_MARIA).with(cliente(CPF_MARIA)))
                .andExpect(status().isOk());

        verify(listVeiculosByClienteGateway).execute(1L);
    }

    @Test
    void clienteNaoListaVeiculosDeOutroCliente() throws Exception {
        when(findClienteByDocumentGateway.execute(CPF_DOUGLAS)).thenReturn(clienteComDocumento(CPF_DOUGLAS));

        mvc.perform(MockMvcRequestBuilders.get("/clientes/documento/{documento}/veiculos", CPF_DOUGLAS).with(cliente(CPF_MARIA)))
                .andExpect(status().isNotFound());

        verifyNoInteractions(listVeiculosByClienteGateway);
    }

    @Test
    void adminListaVeiculosDeQualquerCliente() throws Exception {
        when(findClienteByDocumentGateway.execute(CPF_DOUGLAS)).thenReturn(clienteComDocumento(CPF_DOUGLAS));
        when(listVeiculosByClienteGateway.execute(1L)).thenReturn(List.of(veiculoDoCliente()));

        mvc.perform(MockMvcRequestBuilders.get("/clientes/documento/{documento}/veiculos", CPF_DOUGLAS).with(admin()))
                .andExpect(status().isOk());
    }

    @Test
    void clienteNaoListaClientes() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/clientes").with(cliente(CPF_MARIA)))
                .andExpect(status().isForbidden());
    }

    @Test
    void clienteNaoConsultaClientePorId() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/clientes/{clienteId}", 1L).with(cliente(CPF_MARIA)))
                .andExpect(status().isForbidden());
    }
}
