# Pact Contract Testing PoC

Uma PoC pequena e executável de Consumer-Driven Contract Testing com Pact.
Ela demonstra como contratos publicados por consumers independentes são verificados pelo provider antes de um deploy.

## O cenário

O `user-service` expõe `GET /users/{id}` para dois consumers que têm necessidades diferentes:

| Participante           | Tecnologia         | Campos que contrata         |
|------------------------|--------------------|-----------------------------|
| `order-service`        | Java               | `id`, `name`                |
| `notification-service` | Python             | `id`, `email`, `active`     |
| `user-service`         | Java + Spring Boot | Provider dos dois contratos |

```text
order-service ───────────┐
                          ├── publishes Pacts ──> Pact Broker
notification-service ────┘                              │
                                                         ▼
                                              user-service verifies Pacts
                                                         │
                                                         ▼
                                                    can-i-deploy
```

Uma alteração de `name` para `fullName`, por exemplo, quebra apenas o contrato do `order-service`.
O `notification-service` permanece compatível porque nunca contratou aquele campo.

O teste Pact do **consumer** define a expectativa e gera o contrato. A **provider verification** busca os Pacts
publicados e executa essas interações contra a API local real do provider. Em resumo: o consumer define; o provider
prova que cumpre. Veja [a explicação detalhada](docs/contratos-de-api.md#os-dois-lados-dos-testes-pact).

Para a apresentação em inglês, use o [deck V2](docs/contract-testing-5min-en-v2.pdf),
a [fala](docs/presentation-speech-en.md) e o [guia hands-on](docs/hands-on-5min-en.md).

## Estrutura

```text
consumer-order-java/            Consumer Java e seu teste Pact
consumer-notification-python/   Consumer Python e seu teste Pact
provider-user-java/             Provider Spring Boot e provider states
docs/                           Roteiro e assets da apresentação
scripts/
  pact/                         Operações do ciclo de contrato e deploy
  tools/                        Geração de assets da apresentação
.github/workflows/              Pipeline de contratos ordenada
infra/hosted-broker/            Referência de configuração do Broker hospedado
```

O provider depende da interface `UserRepository`. Para manter a PoC autocontida, `InMemoryUserRepository` é a
implementação usada pela aplicação. O Provider State insere o usuário necessário antes de cada interação Pact, de modo
que o teste de contrato exercita a API HTTP e uma fronteira de persistência realista, sem exigir banco de dados.

## Broker hospedado e GitHub Actions

O Broker é executado fora deste repositório, na VPS, e é acessado em `https://pact.schemint.dev`.

Configure estes **Repository Secrets** no GitHub:

```text
PACT_BROKER_BASE_URL
PACT_BROKER_USERNAME
PACT_BROKER_PASSWORD
```

Nenhuma credencial deve ser adicionada a arquivos versionados. Veja a configuração base
em [`infra/hosted-broker`](infra/hosted-broker/).

## Pipeline

O workflow **Contract verification** é executado em pushes para `main` ou manualmente em **Actions → Run workflow**.

```text
Publish Order contract
        ↓
Publish Notification contract
        ↓
Verify User provider and gate deployment
```

Cada participante é versionado pelo SHA do commit. O provider publica os resultados da verificação no Broker e, em
seguida, o `can-i-deploy` consulta a matriz de compatibilidade.

O fluxo é propositalmente sequencial e usa um único runner, evitando corridas entre publicação e verificação. Nesta
PoC ele republica os dois contratos em cada execução; em um produto real, cada consumer publica somente quando o seu
contrato muda e uma alteração no provider apenas verifica os Pacts que já estão no Broker.

## Execução manual (opcional)

Pré-requisitos: Docker, JDK 21 (com `javac`), Python 3.13+ e Git.

Para a demonstração, basta fazer o push em `main` e acompanhar o workflow no GitHub Actions. Os comandos abaixo são
úteis somente para executar o ciclo Pact manualmente.

### Broker local (opcional)

```bash
docker compose up -d --wait
./scripts/pact/publish-java-pact.sh
./scripts/pact/publish-python-pact.sh
./scripts/pact/verify-provider.sh
```

O Broker local fica em <http://localhost:9292>. Ao encerrar o ensaio, execute:

```bash
docker compose down
```

### Broker hospedado

Para verificar contra `https://pact.schemint.dev`, exporte as credenciais apenas na sessão atual do terminal. Obtenha
o usuário e a senha no gerenciador de segredos; eles não devem entrar em `.env` versionado, comandos gravados ou logs.

```bash
export PACT_BROKER_BASE_URL='https://pact.schemint.dev'
export PACT_BROKER_USERNAME='seu-usuario-do-broker'
read -rs PACT_BROKER_PASSWORD
export PACT_BROKER_PASSWORD

./scripts/pact/verify-provider.sh
./scripts/pact/can-i-deploy.sh
```

Use os scripts `publish-*-pact.sh` quando quiser gerar e publicar os contratos dos consumers. Use
`verify-provider.sh` para uma mudança no provider: ele fornece URL, autenticação, versão e publicação do resultado da
verificação. Por isso, ele é o comando correto para a demo, em vez de executar `./mvnw test` na raiz.

## Roteiro de demonstração

1. Comece no estado verde: abra o Broker e o último workflow verde do Actions.
2. No contrato `order-service → user-service`, destaque que ele exige `id` e `name`.
3. Na IDE, renomeie `name` para `fullName` em `UserResponse` e ajuste o accessor no
   `UserControllerTest`; o controller mantém o valor no construtor posicional.
4. Faça commit e push para `main`, ou execute `verify-provider.sh` com as variáveis do Broker exportadas.
5. Mostre que a verificação falha para `order-service`, enquanto `notification-service` continua compatível.
6. No Broker, abra a matriz e mostre o bloqueio do `can-i-deploy`.

Antes da apresentação, compartilhe [`docs/contratos-de-api.md`](docs/contratos-de-api.md). O roteiro cronometrado está
em [`docs/presentation-script.md`](docs/presentation-script.md) e o guia hands-on
em [`docs/hands-on-5min.md`](docs/hands-on-5min.md); os assets estão
em [`docs/presentation-assets`](docs/presentation-assets/).

## Official references

- [Matching rules](https://docs.pact.io/implementation_guides/javascript/docs/matching)
- [Pact Broker overview and Matrix](https://docs.pact.io/pact_broker/overview)
- [can-i-deploy](https://docs.pact.io/pact_broker/can_i_deploy)
- [Deployments and releases](https://docs.pact.io/pact_broker/recording_deployments_and_releases)
- [Pending Pacts](https://docs.pact.io/pact_broker/advanced_topics/pending_pacts)
- [Webhooks](https://docs.pact.io/pact_broker/webhooks)
- [Plugins and message interactions](https://docs.pact.io/plugins/quick_start)
