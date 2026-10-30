# ADR-002 — Escalabilidade com HPA e Cluster Autoscaler

- **Status:** Aceita
- **Data:** 2026-10

## Contexto

A Fase 3 exige Kubernetes com escalabilidade. A aplicação é stateless (sessão nenhuma, estado todo no RDS), então pode escalar horizontalmente. Por outro lado, o orçamento é de conta de estudante: os nós são `t3.small` (2 vCPU, 2 GiB), e a JVM do Spring Boot com o agente do New Relic ocupa boa parte da memória de um nó.

## Decisão

Escalar em duas camadas:

1. **Pods — HorizontalPodAutoscaler** (`k8s/hpa.yaml`, `autoscaling/v2`):
   - `minReplicas: 1`, `maxReplicas: 2`;
   - métrica: CPU média de **70%** dos requests (via `metrics-server`).
2. **Nós — Cluster Autoscaler** (IRSA, no repositório do EKS):
   - managed node group `t3.small`, **min 2, desired 2, max 3**.

Os recursos do container ficam dimensionados para caber dois pods em nós diferentes:

| | CPU | Memória |
|---|---|---|
| requests | 300m | 500Mi |
| limits | 700m | 900Mi |

CPU foi escolhida como métrica porque o custo da aplicação vem de serialização JSON, geração de PDF e criptografia (JWT/HMAC). Memória na JVM não cai depois de alocada, então não serve para reduzir réplicas.

## Consequências

- Picos de carga (ex.: o plano JMeter `test-plans/`) levam a 2 réplicas. Se não houver espaço, o Cluster Autoscaler sobe um terceiro nó.
- O teto baixo (2 réplicas, 3 nós) limita o custo, mas também a capacidade. Para produção real, `maxReplicas` e o tamanho do node group seriam os primeiros ajustes.
- `requests` corretos são pré-requisito: o HPA calcula a utilização sobre eles, e o scheduler os usa para decidir se cabe outro pod.
- O uso de CPU e memória dos pods e nós aparece no dashboard do New Relic (página Kubernetes). Ver [ADR-005](ADR-005-observabilidade.md).
