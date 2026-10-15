# RFC-001 — Escolha da nuvem

- **Status:** Aceita
- **Autores:** Grupo 52
- **Data:** 2026-05

## Contexto

A Fase 3 exige API Gateway, Function Serverless para autenticação, banco de dados gerenciado, cluster Kubernetes com escalabilidade e provisionamento por Terraform, com escolha livre de nuvem. O projeto já rodava na AWS desde a Fase 2 (ECS Fargate e, depois, EKS), com state do Terraform em S3 e pipelines autenticando via OIDC.

## Proposta

Manter a **AWS** (`us-east-1`) para todos os componentes:

| Requisito | Serviço AWS |
|---|---|
| API Gateway | Amazon API Gateway (REST API) |
| Function Serverless | AWS Lambda (Python 3.12) + Lambda Authorizer |
| Banco gerenciado | Amazon RDS for PostgreSQL |
| Kubernetes | Amazon EKS (managed node group + Cluster Autoscaler) |
| Segredos | AWS Secrets Manager |
| Imagens | Amazon ECR |
| IaC | Terraform (provider `hashicorp/aws`) com state em S3 |

## Alternativas consideradas

| Nuvem | Prós | Contras |
|---|---|---|
| **AWS** | Infra da Fase 2 já pronta, conhecimento do grupo, OIDC com GitHub configurado, todos os serviços exigidos disponíveis e maduros no Terraform | EKS cobra o control plane (~US$ 73/mês por cluster) |
| Azure (AKS, API Management, Functions) | Control plane do AKS gratuito; API Management visto em aula | Recomeçar toda a infra; API Management tem custo alto mesmo no tier de desenvolvimento |
| GCP (GKE, API Gateway, Cloud Functions) | GKE Autopilot simples de operar | Nenhuma experiência prévia do grupo; migração completa |

## Decisão

AWS. O custo de migrar a infra e o conhecimento acumulado pesam mais que a economia no control plane do Kubernetes.

## Consequências

- Reaproveitamento dos repositórios de infra da Fase 2 (EKS, gateway e state no S3).
- O custo do EKS é controlado derrubando ambientes que não estão em uso (flag `destroy` nos pipelines). Ver [ADR-003](../adrs/ADR-003-ambientes.md).
- Os serviços gerenciados (Lambda, API Gateway, RDS) se integram ao New Relic pela integração AWS, sem agente. Ver [ADR-005](../adrs/ADR-005-observabilidade.md).
