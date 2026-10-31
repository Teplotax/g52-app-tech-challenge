package com.grupo52.tech_challenge.config;

import com.grupo52.tech_challenge.domain.Cliente;
import com.grupo52.tech_challenge.domain.Ordem;
import com.grupo52.tech_challenge.exception.NotFoundGatewayException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Objects;
import java.util.Optional;

// token de cliente (cpf) só enxerga o próprio cadastro e as próprias OS; admin e auth desligada passam direto
public final class ClienteAutenticado {

    private ClienteAutenticado() {}

    public static Optional<String> cpf() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwt
                && auth.getAuthorities().stream().anyMatch(a -> "ROLE_CLIENTE".equals(a.getAuthority()))) {
            // sem a claim não pode cair no caminho do admin
            return Optional.of(Objects.requireNonNullElse(jwt.getToken().getClaimAsString("cpf"), ""));
        }
        return Optional.empty();
    }

    // 404 em vez de 403 pra não revelar que a OS existe
    public static void verificarAcesso(Ordem os) throws NotFoundGatewayException {
        if (!podeVer(os.getCliente().getDocumento())) {
            throw new NotFoundGatewayException("Ordem de serviço não encontrada");
        }
    }

    // mesma ideia: cliente só consulta o próprio cadastro
    public static void verificarAcesso(Cliente cliente) throws NotFoundGatewayException {
        if (!podeVer(cliente.getDocumento())) {
            throw new NotFoundGatewayException("Cliente não encontrado");
        }
    }

    private static boolean podeVer(String documento) {
        Optional<String> cpf = cpf();
        return cpf.isEmpty() || cpf.get().equals(documento);
    }
}
