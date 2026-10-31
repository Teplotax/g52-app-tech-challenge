# ADR-006 — Autorização por roles CLIENTE e ADMIN

- **Status:** Aceita
- **Data:** 2026-10

## Contexto

A Lambda emite dois tipos de token (ver [RFC-003](../rfcs/RFC-003-estrategia-de-autenticacao.md)): o do cliente, obtido pelo CPF, e o da equipe, obtido via `client_credentials`. O cliente não pode ver dados de outros clientes nem operar a oficina (estoque, serviços, transições de status).

## Decisão

Autorização baseada na claim `roles` do JWT, aplicada em três pontos:

1. **Lambda Authorizer (Gateway):** a policy devolvida libera só as rotas da role.
   - `CLIENTE`: `GET /clientes/documento/*`, `GET /clientes/documento/*/veiculos`, `GET /ordensDeServico`, `GET /ordensDeServico/*`, `POST /ordensDeServico/*/aprovar`.
   - `ADMIN`: todas as rotas.
2. **Spring Security (`SecurityConfig`):** as mesmas regras por rota, com `hasAnyRole("ADMIN", "CLIENTE")` nas cinco rotas acima e `hasRole("ADMIN")` no resto.
3. **Posse do cadastro e da OS (`ClienteAutenticado`):** com token `CLIENTE`, a listagem de OS é filtrada pelo CPF do token, e um documento (cadastro ou veículos) ou uma OS de outro cliente responde **404**, não 403, para não revelar que o registro existe.

Ficam públicas, sem JWT:
- `/aprovacao/**` e `/aquisicao/**`, protegidas por token HMAC no link do e-mail;
- `/actuator/health`, para o healthcheck.

Localmente (perfil `local` e docker-compose), `app.security.enabled=false` desliga a autenticação.

## Consequências

- O cliente usa a mesma API da equipe, com menos permissões. Não há endpoints duplicados.
- A regra de rota está em dois lugares (authorizer e `SecurityConfig`). Uma rota nova de cliente precisa ser incluída nos dois, o que é um custo de manutenção aceito em troca da defesa em profundidade.
- A NLB da aplicação é pública, e já recebe tráfego de scanners chamando o host direto, sem passar pelo Gateway. Essas requisições são barradas pelo Spring Security (401), o que reforça a validação dupla. A evolução é uma NLB interna com VPC Link.
