# RFC-003 — Estratégia de autenticação

- **Status:** Aceita
- **Autores:** Grupo 52
- **Data:** 2026-10

## Contexto

Na Fase 2, a autenticação usava **Keycloak** rodando como pod no EKS, com `client_credentials` para a equipe. A Fase 3 exige que as rotas sensíveis sejam protegidas por **autenticação via CPF**, com uma **Function Serverless** que valide o CPF, consulte a existência e o status do cliente e devolva um JWT. A equipe da oficina continua precisando de acesso administrativo a todas as rotas.

## Proposta

1. **Uma Lambda emite os dois tipos de JWT** (RS256, mesma chave):
   - `POST /auth` com o CPF → `roles: ["CLIENTE"]`. Valida os dígitos, consulta `clientes` (existe, `ativo`, tipo CPF).
   - `POST /auth/token` com `client_credentials` → `roles: ["ADMIN"]`. Compara o hash SHA-256 do `client_secret`.
2. **Chave pública publicada como JWKS** em `GET /.well-known/jwks.json`.
3. **Validação em duas camadas:**
   - no **Gateway**, por um Lambda Authorizer (TOKEN) que valida assinatura, `exp`, `iss`, `aud` e devolve uma policy só com as rotas da role;
   - na **aplicação**, pelo Spring Security Resource Server, via JWKS, com a mesma regra de role e a checagem de que o cliente só acessa as próprias OS.
4. **Keycloak removido.**

## Alternativas consideradas

| Alternativa | Prós | Contras |
|---|---|---|
| Manter o Keycloak + uma Lambda só para CPF | `client_credentials` "de fábrica" | Dois emissores e dois JWKS para validar; Keycloak consome memória do cluster e precisa de manutenção |
| Amazon Cognito | Gerenciado, `client_credentials` nativo | CPF não é um fluxo padrão (exigiria custom auth flow com 3 Lambdas); dois emissores; cobrança por token M2M |
| JWT HS256 (segredo compartilhado) | Mais simples | O segredo precisaria estar no Gateway e na aplicação; vazar em um lugar compromete todos |
| Só a aplicação valida (sem authorizer) | Menos uma Lambda | O Gateway não protegeria nada; requisições inválidas chegariam ao EKS |

## Decisão

Lambda própria como emissor único (CPF e `client_credentials`), RS256 com JWKS, e validação no Gateway (Lambda Authorizer) e na aplicação.

## Consequências

- Um emissor, uma chave e um JWKS. A aplicação não compartilha segredo com ninguém.
- Requisições sem token ou com role errada são barradas no Gateway (401/403), antes do EKS.
- O resultado do authorizer fica em cache por 5 min por token. Por isso a policy libera todas as rotas da role, e não só a rota chamada.
- O token de cliente só consulta e aprova as próprias OS. Ver [ADR-006](../adrs/ADR-006-autorizacao-por-role.md).
- O `client_secret` administrativo é gerado pelo Terraform, guardado no Secrets Manager e entregue à Lambda só como hash.
- Ponto de atenção: a NLB da aplicação é pública, então dá para chamar a app direto, sem passar pelo Gateway. Isso é mitigado pela validação do JWT na própria aplicação. A evolução seria uma NLB interna com VPC Link.
