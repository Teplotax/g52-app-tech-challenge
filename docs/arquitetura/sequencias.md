# Diagramas de sequência

## 1. Autenticação do cliente por CPF

O cliente informa o CPF e recebe um JWT com `roles: ["CLIENTE"]`, que só permite consultar e aprovar as próprias OS.

```mermaid
sequenceDiagram
    autonumber
    actor C as Cliente
    participant GW as API Gateway
    participant L as Lambda de autenticação
    participant DB as RDS PostgreSQL

    C->>GW: POST /hom/auth {"cpf": "555.632.710-64"}
    GW->>L: AWS_PROXY (stage variable authFunction)
    L->>L: valida formato e dígitos do CPF
    alt CPF inválido
        L-->>C: 400 CPF inválido
    end
    L->>DB: SELECT ... FROM clientes WHERE documento = :cpf
    DB-->>L: cliente (ativo, tipo_documento)
    alt não encontrado
        L-->>C: 401 Cliente não encontrado
    else inativo ou CNPJ
        L-->>C: 403 Forbidden
    end
    L->>L: assina JWT RS256 (sub, cpf, roles=CLIENTE, exp=1h)
    L-->>GW: 200 {access_token, token_type, expires_in}
    GW-->>C: JWT
```

## 2. Autenticação da equipe (`client_credentials`)

A equipe da oficina usa o grant OAuth2 `client_credentials` e recebe um JWT com `roles: ["ADMIN"]`, que acessa todas as rotas.

```mermaid
sequenceDiagram
    autonumber
    actor E as Equipe da oficina
    participant GW as API Gateway
    participant L as Lambda de autenticação

    E->>GW: POST /hom/auth/token<br/>grant_type=client_credentials&client_id&client_secret
    GW->>L: AWS_PROXY
    L->>L: compara SHA-256 do client_secret (tempo constante)
    alt credencial inválida
        L-->>E: 401 Unauthorized
    end
    L->>L: assina JWT RS256 (sub=client_id, roles=ADMIN)
    L-->>E: 200 {access_token, token_type, expires_in}
```

## 3. Abertura de ordem de serviço

A equipe abre uma OS com o token administrativo. O Gateway valida o token antes de chegar ao cluster, e a aplicação valida de novo (defesa em profundidade).

```mermaid
sequenceDiagram
    autonumber
    actor E as Equipe da oficina
    participant GW as API Gateway
    participant AZ as Lambda Authorizer
    participant APP as App (EKS)
    participant DB as RDS PostgreSQL
    participant NR as New Relic

    E->>GW: POST /hom/ordensDeServico<br/>Authorization: Bearer JWT · x-correlationid
    GW->>AZ: valida JWT (cache de 5 min por token)
    AZ->>AZ: assinatura RS256, exp, iss, aud
    alt token inválido
        AZ-->>E: 401
    else role sem acesso à rota
        AZ-->>E: 403
    end
    AZ-->>GW: policy Allow (rotas da role)
    GW->>APP: HTTP_PROXY → NLB (stage variable appHost)
    APP->>APP: CorrelationIdFilter (MDC + header)
    APP->>APP: Spring Security valida JWT (JWKS) e role ADMIN
    APP->>DB: valida cliente e veículo
    APP->>DB: INSERT ordens_de_servico (status RECEBIDA)<br/>+ status_changes + reserva de estoque (transação)
    APP-)NR: evento OrdemStatusAlterado (RECEBIDA)
    APP-)NR: trace, métricas e logs com correlationId
    APP-->>GW: 201 Created (OS)
    GW-->>E: 201 + x-correlationid
```

## 4. Aprovação do orçamento pelo cliente

```mermaid
sequenceDiagram
    autonumber
    actor E as Equipe
    actor C as Cliente
    participant APP as App (EKS)
    participant DB as RDS
    participant M as SMTP (MailPit)

    E->>APP: POST /ordensDeServico/{id}/solicitarAprovacao (ADMIN)
    APP->>DB: status AGUARDANDO_APROVACAO
    APP->>APP: gera PDF do orçamento + link com token HMAC
    APP->>M: e-mail com orçamento e link
    alt falha no SMTP ou no PDF
        APP-)APP: evento IntegracaoFalha (alerta no New Relic)
    end
    C->>APP: GET /aprovacao/{id}?token=... (página do link, sem login)
    alt pelo link do e-mail
        C->>APP: POST /aprovacao/{id}?token=... (serviços escolhidos)
    else pela API
        C->>APP: POST /ordensDeServico/{id}/aprovar (token CLIENTE, só a própria OS)
    end
    APP->>DB: reserva estoque → APROVADA ou AGUARDANDO_AQUISICAO
```
