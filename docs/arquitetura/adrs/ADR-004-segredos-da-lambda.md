# ADR-004 — Segredos da Lambda por variável de ambiente

- **Status:** Aceita
- **Data:** 2026-10

## Contexto

A Lambda de autenticação precisa consultar o RDS, que não é público. Por isso ela roda dentro da VPC, nas subnets privadas. Lá dentro ela não tem rota para a internet, então também não alcança a API do Secrets Manager.

Para ler o segredo em runtime haveria duas opções pagas: um **NAT Gateway** (~US$ 32/mês + tráfego) ou um **VPC Interface Endpoint** do Secrets Manager (~US$ 7/mês por AZ). Isso vale por ambiente.

## Decisão

A Lambda **não chama o Secrets Manager em runtime**. O Terraform lê os segredos no deploy e os injeta como **variáveis de ambiente** da função:

- credenciais do RDS (lidas do segredo `<id>/credentials`);
- chave privada RSA para assinar o JWT;
- **hash SHA-256** do `client_secret` administrativo (o segredo em si nunca chega à Lambda).

Para alcançar o banco, a Lambda anexa o security group de clientes do RDS (`<id>-clients`), o mesmo usado pelos nós do EKS.

## Consequências

- Custo zero de rede extra.
- As variáveis de ambiente da Lambda são criptografadas em repouso (KMS gerenciado pela AWS). Ainda assim, quem tem `lambda:GetFunctionConfiguration` consegue lê-las, então o acesso à conta precisa ser restrito.
- Os valores ficam no state do Terraform. O bucket S3 do state é privado e criptografado.
- Trocar uma senha ou rotacionar a chave exige um redeploy da Lambda. Por isso não há rotação automática da senha do RDS.
- Evolução: com orçamento, um VPC Endpoint do Secrets Manager permitiria ler em runtime com cache e habilitar rotação automática.
