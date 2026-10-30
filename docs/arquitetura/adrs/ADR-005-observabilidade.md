# ADR-005 — Observabilidade com New Relic

- **Status:** Aceita
- **Data:** 2026-10

## Contexto

O enunciado pede Datadog ou New Relic com latência das APIs, CPU e memória do Kubernetes, healthcheck e uptime, alertas para falhas no processamento de OS, logs JSON com correlação e dashboards de volume diário de OS, tempo médio por status e erros de integração.

## Decisão

**New Relic, no plano gratuito** (100 GB/mês de ingestão, 1 usuário completo). O Datadog não tem plano gratuito que cubra APM, logs e Kubernetes juntos.

| Sinal | Como é coletado |
|---|---|
| APM, latência e traces | Java agent 9.5.0 na imagem da app, com nomes de transação por rota Spring |
| Logs | Logs JSON (formato ECS) no stdout, encaminhados pelo agente com `correlationId`, `trace.id` e `span.id` |
| Kubernetes | `nri-bundle` (Helm) em modo *low data*: CPU e memória de pods e nós, eventos |
| Lambda, API Gateway, RDS | Integração AWS por API polling, com a role `newrelic-aws-integration` (sem agente, sem custo de rede) |
| Negócio | Eventos customizados `OrdemStatusAlterado` (status, anterior, duração no status anterior) e `IntegracaoFalha` (EMAIL/PDF) |
| Uptime | Monitor Synthetics ping em `/<env>/health` pelo Gateway |

Dashboards, alertas e monitor são **Terraform** no repositório do EKS (provider `newrelic`), um conjunto por ambiente:

- Dashboard com 4 páginas: ordens de serviço (volume diário, tempo médio em Diagnóstico, Execução e Finalização, erros de integração), APIs e app, rotas e Kubernetes.
- Política de alertas com 6 condições NRQL: falha no processamento de OS (5xx em `/ordensDeServico`), falha de integração (e-mail/PDF), erros na Lambda de autenticação, latência p95 acima de 2s, taxa de erro acima de 5% e app fora do ar (healthcheck).

## Consequências

- Sem custo nos limites do plano gratuito. O modo *low data* do `nri-bundle` e a ingestão de logs só da app mantêm o volume baixo.
- O tempo por status sai direto do evento `OrdemStatusAlterado` (atributo de duração), sem consulta ao banco.
- Observabilidade versionada e reproduzível: recriar um ambiente recria o dashboard e os alertas.
- Exige dois segredos por ambiente: `NEW_RELIC_LICENSE_KEY` (ingestão) e `NEW_RELIC_API_KEY` (User key, para o Terraform). Sem a User key, o Terraform pula os recursos do New Relic.
- Localmente, o agente fica desligado (`NEW_RELIC_AGENT_ENABLED=false`).
