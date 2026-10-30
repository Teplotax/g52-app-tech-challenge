# ADR-001 — Comunicação síncrona REST via API Gateway

- **Status:** Aceita
- **Data:** 2026-10

## Contexto

A solução tem três componentes que conversam entre si: o API Gateway (porta de entrada), a Lambda de autenticação e a aplicação no EKS. A aplicação ainda fala com o RDS e com o SMTP (MailPit). Era preciso definir se a comunicação seria síncrona (HTTP) ou assíncrona (filas e eventos).

Os fluxos da oficina são interativos: abrir OS, consultar, aprovar orçamento. O cliente espera a resposta na hora, e o volume é baixo (uma oficina).

## Decisão

Toda a comunicação é **síncrona, via REST/HTTP**, centralizada no API Gateway:

- `/auth`, `/auth/token` e `/.well-known/jwks.json` → **AWS_PROXY** para a Lambda de autenticação.
- Rotas da aplicação → **HTTP_PROXY** para a NLB do EKS, protegidas pelo Lambda Authorizer.
- O destino de cada integração vem de **stage variables** (`appHost`, `authFunction`, `authorizerFunction`), então o mesmo contrato OpenAPI serve todos os ambientes.
- Um header `x-correlationid` atravessa Gateway e aplicação e aparece nos logs.
- O envio de e-mail é feito de forma síncrona dentro da requisição. Falhas viram o evento `IntegracaoFalha` no New Relic, que dispara alerta.

Não há fila (SQS/SNS) nem broker de eventos nesta fase.

## Consequências

- Simples de entender, testar (collection do Insomnia) e rastrear: uma requisição, um trace.
- O contrato do Gateway fica versionado num repositório próprio (`g52-api-tech-challenge-v1-ext`). Quando a NLB muda (app recriada), basta redeployar o contrato.
- Acoplamento temporal: se o SMTP estiver fora, a solicitação de aprovação falha na hora, em vez de ficar enfileirada para nova tentativa. Isso é aceitável para o volume atual e fica visível pelo alerta de integração.
- Evolução natural: mover e-mail e PDF para uma fila (SQS) com retry, caso o volume ou a criticidade aumentem.
