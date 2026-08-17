# O que é um contrato de API?

Este material acompanha uma demonstração curta de Contract Testing com Pact.

## A ideia central

Um contrato de API não é o DTO inteiro nem uma documentação aspiracional. É uma **expectativa executável** que um
consumer possui sobre uma interação com outro serviço.

Nesta PoC, o `user-service` expõe `GET /users/123` para dois consumers:

| Consumer               | Campos de que depende   |
|------------------------|-------------------------|
| `order-service`        | `id`, `name`            |
| `notification-service` | `id`, `email`, `active` |

O provider pode retornar uma resposta maior:

```json
{
  "id": 123,
  "name": "Michael",
  "email": "michael@email.com",
  "active": true
}
```

Mas cada consumer contrata somente a parte que utiliza. Por isso, remover `name` quebra o Order, enquanto o Notification
continua compatível.

## O que um contrato pode validar

Um contrato descreve uma interação observável:

```text
Given:  user 123 exists
When:   GET /users/123
Then:   200
        Content-Type: application/json
        body com id e name
```

Ele pode validar:

- método, rota e parâmetros;
- status e headers;
- body, tipos e matchers;
- cenários de erro relevantes;
- o estado que o provider precisa preparar para reproduzir a interação.

Regra prática: valide aquilo que o consumer realmente usa. Evite transformar o contrato em um snapshot gigante da API.

## Os dois lados dos testes Pact

Pact tem dois tipos de teste que trabalham juntos, mas com responsabilidades diferentes.

### 1. O teste Pact do consumer define o contrato

No consumer, o teste descreve somente a interação que aquele consumer precisa. Ele normalmente usa um provider simulado;
ele **não** chama o provider real.

Neste projeto:

- `UserClientContractTest` faz o Order declarar que `GET /users/123` precisa devolver `id` e `name`.
- O teste do Notification declara que a mesma rota precisa devolver `id`, `email` e `active`.

Quando o teste passa, o Pact gera um arquivo de contrato e o pipeline o publica no Broker. O consumer é dono da
expectativa: ele define quais campos, tipos, status e headers realmente utiliza.

### 2. A provider verification valida o contrato

No provider, `UserProviderPactVerificationTest` é um teste Pact de verificação. Ele:

1. busca os Pacts publicados no Broker;
2. inicia a API Spring Boot localmente;
3. prepara os dados exigidos pelo Provider State, como `user 123 exists`;
4. executa a request descrita em cada Pact contra a API local;
5. compara a response real com as regras definidas pelo consumer;
6. publica o resultado no Broker.

Portanto, **o consumer define o contrato; o provider prova que ainda o cumpre**. O provider não inventa as regras do
consumer, e o teste do consumer não prova que a implementação do provider está correta.

### O que falha na demonstração

Ao renomear `name` para `fullName`, o provider pode continuar saudável pelos seus próprios testes unitários e ainda
responder `200`. A provider verification, porém, falha para o Order porque o contrato publicado ainda exige `name`. O
Notification passa porque não depende desse campo.

## Onde o Pact entra

```text
Consumer test → gera e publica o Pact → Pact Broker → provider verification → can-i-deploy
```

- O **consumer** declara a expectativa em um teste.
- O **provider** executa os Pacts publicados contra sua API local.
- O **Broker** guarda os Pacts publicados e os resultados de verificação.
- O **can-i-deploy** informa se uma versão pode ser entregue sem quebrar consumers conhecidos.

Pact complementa testes unitários, integração e E2E. Ele responde rapidamente uma pergunta específica: **esta versão do
provider continua compatível com quem depende dela?**
