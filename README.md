# mock-ifood

API mock do iFood para desenvolvimento local e integrações do **motor de scoring**.

O objetivo é simular uma fonte “realista” de renda e comportamento de entregador (gig economy), incluindo **instabilidade** (delay/erro/timeout) para exercitar resiliência (ex: Apache Camel).

## Como rodar

Pré-requisitos: Java 21 e Maven.

```bash
mvn spring-boot:run
```

- Swagger UI: `http://localhost:8080/swagger`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- Health: `http://localhost:8080/health`

## Endpoints (v1)

### iFood

- `GET /ifood/earnings/{userId}` histórico de ganhos (com `summary`)
- `GET /ifood/performance/{userId}` (opcional/avançado) comportamento

Query params úteis:

- `profile`: `A_STABLE` | `B_VARIABLE` | `C_IRREGULAR` (força perfil)
- `from` / `to` (ISO date): janela do histórico de ganhos

## Exemplo: ganhos

```bash
curl "http://localhost:8080/ifood/earnings/123?from=2024-01-01&to=2024-01-10&profile=A_STABLE"
```

## Instabilidade (chaos)

Config em `application.yml`:

- `mock.ifood.chaos.enabled`: liga/desliga
- `mock.ifood.chaos.delay-ms-min` / `delay-ms-max`: latência artificial
- `mock.ifood.chaos.error-rate`: probabilidade de erro (500/503)
- `mock.ifood.chaos.timeout-rate` + `timeout-ms`: simula timeout (sleep longo)