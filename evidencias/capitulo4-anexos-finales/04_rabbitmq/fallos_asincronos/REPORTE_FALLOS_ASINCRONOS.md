# Reporte de fallos asincronos RabbitMQ

| Campo | Valor |
|---|---|
| RUN_ID | `cap4-mq-failures-20260708T022708Z` |
| Fecha UTC | `2026-07-08T02:35:38+00:00` |
| Rama | `cap4-ajustes-final` |
| Commit | `acf46725bb4dfd49081eb1a18c1a7084e34f520e` |
| Ambiente | Docker Compose sandbox sobre EC2 AWS, core-api, checkout-service, Postgres separados y RabbitMQ |

## Mecanismos verificados

- Acknowledgement: listeners sin ACK manual; Spring AMQP usa acknowledgement automatico/default.
- Outbox: core-api persiste `OutboxEvent` y `OutboxPollingScheduler` reintenta publicacion cada 5000 ms.
- Inbox/idempotencia: checkout-service persiste `InboxEvent` con `event_id` unico y evita reprocesar duplicados.
- DLQ: `checkout-service.checkout-requested.q` declara `x-dead-letter-exchange=checkout.dlx` y routing key `checkout.requested.dlq` hacia `checkout-service.dlq`.
- Requeue: configurable por `rabbitmq.listener.default-requeue-rejected`; default `true`, se deshabilita solo para MQ-07 en sandbox.
- Retry consumidor/backoff: no hay retry/backoff explicito de Spring AMQP; se documenta como limitacion.

## Resultados

| Escenario | Estado | Evidencia |
|---|---|---|
| `MQ-01-consumidor-detenido` | `PASS` | `evidencias/capitulo4-anexos-finales/04_rabbitmq/fallos_asincronos/MQ-01-consumidor-detenido` |
| `MQ-02-broker-no-disponible` | `PASS` | `evidencias/capitulo4-anexos-finales/04_rabbitmq/fallos_asincronos/MQ-02-broker-no-disponible` |
| `MQ-03-mensaje-duplicado` | `PASS` | `evidencias/capitulo4-anexos-finales/04_rabbitmq/fallos_asincronos/MQ-03-mensaje-duplicado` |
| `MQ-04-reintento-publicacion` | `PASS` | `evidencias/capitulo4-anexos-finales/04_rabbitmq/fallos_asincronos/MQ-04-reintento-publicacion` |
| `MQ-05-cola-acumulada-drenaje` | `PASS` | `evidencias/capitulo4-anexos-finales/04_rabbitmq/fallos_asincronos/MQ-05-cola-acumulada-drenaje` |
| `MQ-06-requeue` | `PASS` | `evidencias/capitulo4-anexos-finales/04_rabbitmq/fallos_asincronos/MQ-06-requeue` |
| `MQ-07-dlq` | `PASS` | `evidencias/capitulo4-anexos-finales/04_rabbitmq/fallos_asincronos/MQ-07-dlq` |
