# g52-app-tech-challenge

API de gerenciamento de ordens de serviço para uma oficina automotiva (clientes, veículos, peças, insumos, serviços e ordens de serviço), desenvolvida em Spring Boot pelo Grupo 52 como parte do tech challenge.

## Documentação da API

A especificação Swagger/OpenAPI é mantida no repositório [`doc-api-tech-challenge-v1`](https://github.com/Teplotax/doc-api-tech-challenge-v1) e publicada via **GitHub Pages**:

👉 https://teplotax.github.io/doc-api-tech-challenge-v1/

## Como executar a aplicação

A aplicação roda em containers Docker (app, Keycloak para autenticação e MailPit para e-mails) orquestrados via `docker-compose.yml`. Existem três scripts na raiz do projeto para isso. Antes de tudo, dê permissão de execução a eles (necessário apenas uma vez):

```bash
chmod +x build-and-run.sh run.sh stop.sh
```

### `build-and-run.sh`

Builda as imagens (incluindo a imagem da aplicação, a partir do `Dockerfile.multistage`) e em seguida sobe todos os containers. Use este script na primeira execução ou sempre que tiver alterado o código/dependências da aplicação:

```bash
./build-and-run.sh
```

### `run.sh`

Sobe os containers já existentes sem rebuildar nada. Use quando não houver alterações no código desde a última build:

```bash
./run.sh
```

Ambos os scripts aceitam os mesmos parâmetros do `docker compose up`. Por exemplo, para rodar em background:

```bash
./run.sh -d
```

### `stop.sh`

Para e remove os containers (equivalente a `docker compose down`):

```bash
./stop.sh
```

## Serviços disponíveis após subir a aplicação

| Serviço | URL | Descrição |
|---|---|---|
| MailPit | http://localhost:8025 | Visualização dos e-mails enviados pela aplicação (ex.: aprovações de ordem de serviço) |
| H2 Console | http://localhost:8080/h2-console | Console do banco em memória (JDBC URL: `jdbc:h2:mem:testdb`, usuário `sa`, sem senha) |
| Keycloak | http://localhost:8180 | Servidor de autenticação (realm `g52`, usuário admin: `admin` / `admin`) |
| API | http://localhost:8080 | Aplicação Spring Boot |


## Stack e arquitetura

- **Java 21** e **Spring Boot**, com Maven como gerenciador de build (`app/pom.xml`)
- **Spring Data JPA** com banco **H2** em memória para o ambiente local/docker
- **Spring Security + OAuth2 Resource Server**, validando JWTs emitidos pelo **Keycloak**
- **Spring Mail**, com **MailPit** como servidor SMTP de desenvolvimento
- Geração de PDF via **openhtmltopdf**
- Observabilidade via **Actuator** e **Micrometer/Prometheus** (`/actuator/health`, `/actuator/prometheus`, etc.)
- Código organizado em camadas seguindo princípios de Clean Architecture: `controller` → `service` → `gateway` (interface + implementação) → `gateway/database` (entidades JPA e repositórios), com `dto`s de request/response e `domain` representando as entidades de negócio

## Recursos da API

- `Cliente`
- `Veiculo` (e `Marca`/`Modelo`)
- `Peca`
- `Insumo`
- `Servico`
- `OrdemDeServico`
- `ApprovalLink` (aprovação de ordens de serviço por link enviado por e-mail)

A especificação completa dos endpoints está disponível no Swagger hospedado no GitHub Pages, linkado no início deste README.

## Configuração

Os perfis de configuração ficam em `app/src/main/resources`:

- `application.yaml`: configurações comuns (porta, mail, OAuth2, actuator)
- `application-local.yaml`: perfil para execução local com H2 em memória
- `application-docker.yaml`: perfil usado pelo container da aplicação no `docker-compose.yml`

Principais variáveis de ambiente usadas no `docker-compose.yml`: `MAIL_HOST`, `MAIL_PORT`, `APPROVAL_SECRET`, `APP_BASE_URL`, `APPROVAL_TTL_MINUTES` e `KEYCLOAK_JWK_SET_URI`.

## Repositórios do projeto

Este repositório contém apenas o **ECS Service** (a aplicação em si). A solução completa do projeto **G52 | Tech Challenge** está distribuída nos seguintes repositórios:

| Recurso | Tipo      | Link Repositório |
|---|-----------|---|
| ECS Cluster | Infra     | https://github.com/Teplotax/g52-infra-ecs-tech-challenge |
| Load Balancer | Infra     | https://github.com/Teplotax/g52-infra-lb-tech-challenge |
| ECS Service | App       | https://github.com/Teplotax/g52-app-tech-challenge |
| API Gateway | Infra     | https://github.com/Teplotax/g52-infra-gateway-tech-challenge |
| API Gateway | Resources | https://github.com/Teplotax/g52-api-tech-challenge-v1-ext |
| API Gateway | Doc       | https://github.com/Teplotax/doc-api-tech-challenge-v1 |