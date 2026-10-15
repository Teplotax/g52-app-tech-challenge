# RFC-002 — Banco de dados gerenciado

- **Status:** Aceita
- **Autores:** Grupo 52
- **Data:** 2026-10

## Contexto

Na Fase 2, o PostgreSQL rodava como pod no EKS, com volume EBS. O backup era manual, o volume ficava órfão quando o cluster era destruído, não havia failover e o banco disputava CPU e memória com a aplicação. A Fase 3 exige banco gerenciado, além de uma justificativa formal da escolha e do modelo relacional.

## Proposta

**Amazon RDS for PostgreSQL 16**, provisionado por Terraform em repositório próprio (`g52-infra-rds-tech-challenge`):

- `db.t4g.micro` em dev e `db.t3.micro` em hom/prod. O `t4g` ficou sem capacidade nas AZs usadas.
- 20 GB gp3 com autoscaling até 50 GB, criptografado, sem acesso público e com TLS obrigatório.
- Acesso restrito a um security group de clientes (`<id>-clients`), que os nós do EKS e a Lambda anexam.
- Senha gerada pelo Terraform e guardada no Secrets Manager.
- Schema versionado por Flyway na aplicação. A migration V3 revisou o modelo: status do cliente, índices nas FKs e constraints.

## Alternativas consideradas

| Alternativa | Por que não |
|---|---|
| Manter o Postgres no Kubernetes | Sem backup automático nem failover, ciclo de vida preso ao do cluster, ocupa recursos dos nós |
| Aurora PostgreSQL / Aurora Serverless v2 | Mais caro (instância mínima maior ou capacidade mínima cobrada continuamente) sem ganho para o volume da oficina |
| MySQL (RDS) | `CHECK` e índices parciais mais limitados, além de reescrever as migrations sem nenhum ganho |
| DynamoDB | Não relacional: transações entre várias entidades (OS, itens, estoque) e relatórios (volume diário, tempo por status) ficariam complexos |

## Decisão

RDS for PostgreSQL. A justificativa completa, o diagrama ER, os relacionamentos e os ajustes do modelo estão em [`modelo-de-dados.md`](https://github.com/Teplotax/g52-infra-rds-tech-challenge/blob/main/docs/modelo-de-dados.md), no repositório do banco.

## Consequências

- O banco passa a ser a base dos ambientes. A ordem de deploy começa pelo RDS, e a de destroy termina nele.
- O RDS recém-criado vem vazio: o schema e o seed são aplicados pelo Flyway no primeiro start da aplicação.
- Sem rotação automática de senha, porque a Lambda recebe a senha por variável de ambiente. Ver [ADR-004](../adrs/ADR-004-segredos-da-lambda.md).
