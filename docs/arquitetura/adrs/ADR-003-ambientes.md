# ADR-003 — Ambientes dev, hom e prod

- **Status:** Aceita
- **Data:** 2026-10

## Contexto

O enunciado pede branch main protegida, merge só por PR e deploy automático para homologação e produção. Ao mesmo tempo, manter três clusters EKS e três RDS ligados o tempo todo estoura o orçamento.

## Decisão

Três ambientes, cada um ligado a uma branch:

| Ambiente | Branch | Gatilho | `destroy` padrão |
|---|---|---|---|
| dev | `develop` | push em `develop` | `true` |
| hom | `release/*` | merge do PR `develop` → `release/*` | `false` |
| prod | `main` | merge do PR `release/*` → `main` | `true` (fica `false` só para gravação e avaliação) |

- **Fluxo de promoção:** o push em `develop` faz deploy em dev e abre o PR para `release/*`. O merge faz deploy em hom e abre o PR para `main`. O merge em `main` faz deploy em prod. Os workflows não rodam na abertura de PR, só no merge.
- **Variáveis e segredos por GitHub Environment** (`dev`, `hom`, `prod`), nunca no nível do repositório. A role OIDC aceita só `environment:{dev,hom,prod}`.
- **Sufixo de ambiente em todo recurso** (`-dev`, `-hom`, `-prod`) e state do Terraform separado por ambiente (`<env>/<nome>/terraform.tfstate`).
- **Um único REST API** no Gateway, com um **stage por ambiente**. As stage variables apontam para a Lambda e para a NLB do ambiente.
- **Flag `destroy`** no `.pipes.yml` de cada repositório: com `true`, o pipeline derruba o ambiente em vez de subir.
- **Ordem de deploy:** gateway → RDS → EKS → app → Lambda → contrato. **Ordem de destroy:** app → Lambda → EKS → RDS. O gateway e o contrato não custam nada parados e ficam de pé.

## Consequências

- Custo controlado: só fica de pé o ambiente em uso.
- Recriar um ambiente exige seguir a ordem acima. O contrato precisa ser redeployado depois da app, porque o host da NLB muda.
- Um RDS recriado vem vazio. O Flyway reaplica schema e seed no primeiro start.
- A proteção da `main` (PR obrigatório, sem push direto) é configurada no GitHub, em *branch protection rules*.
