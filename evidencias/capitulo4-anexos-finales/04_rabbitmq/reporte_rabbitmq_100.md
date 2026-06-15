# Reporte RabbitMQ publicacion-consumo-cola-logs-DB 100%

RUN_ID: `cap4-core4-20260614-162647`

## Criterio de cierre

El bloque se considera cerrado porque el checkout `bf08ff9a-ed24-4baf-8643-1d8c740107ee` se puede seguir desde publicacion en `core-api`, entrega por RabbitMQ, consumo en `checkout-service`, pago simulado, publicacion de `checkout.completed`, consumo de resultado por `core-api` y persistencia final `COMPLETED`.

## Evidencia principal

| Evidencia | Archivo |
|---|---|
| Flujo API y cola antes/despues | `../01_funcionales_compra/compra_COMPLETED_100_flow.json` |
| Consultas DB | `db_trace_100_raw.txt` |
| Logs filtrados | `logs_rabbitmq_100.txt` |
| Estado de colas | `rabbitmq_queues_100.json` |
| IDs trazables | `trace_ids_100.txt` |

## Trazabilidad

| Campo | Valor |
|---|---|
| requestId API/core_db | `bf08ff9a-ed24-4baf-8643-1d8c740107ee` |
| eventId/inbox checkout-service | `ef74b7fc-21ae-4365-a0d5-515a8c92e741` |
| orderId checkout-service | `43abeb10-c105-4814-888a-4d84ca570194` |
| paymentAttempt | `c60bd7ba-067a-4727-b647-2af3be85a53a` |
| estado final | `COMPLETED` |

## Publicacion

| Validacion | Evidencia |
|---|---|
| `core-api` crea checkout request | `core_db.checkout_requests` contiene `request_id=bf08ff9a-ed24-4baf-8643-1d8c740107ee` |
| `core-api` publica evento | `core_db.outbox_events` contiene `event_type=checkout.requested`, `published=t` |
| Cola recibe evento | `checkout-service.checkout-requested.q` registra publicacion y consumo |

## Cola RabbitMQ

La cola critica `checkout-service.checkout-requested.q` queda con `messages=0`, `messages_ready=0`, `messages_unacknowledged=0` y `consumers=1`. Las colas informativas sin consumidor dedicado pueden conservar mensajes, sin afectar el cierre funcional del checkout.

## Consumo

| Validacion | Evidencia |
|---|---|
| `checkout-service` consume evento | Log: `Received checkout.requested event: ef74b7fc-21ae-4365-a0d5-515a8c92e741` |
| Pago simulado ejecutado | Log: `StripeSimulatedPaymentAdapter` ejecuta y aprueba pago simulado |
| Evento `payment.processed` | Log: `Published payment.processed event for order: 43abeb10-c105-4814-888a-4d84ca570194, success: true` |
| Evento `checkout.completed` | Log: `Published checkout.completed event for checkout request: bf08ff9a-ed24-4baf-8643-1d8c740107ee` |
| `core-api` consume resultado | Log: `Checkout completed for request: bf08ff9a-ed24-4baf-8643-1d8c740107ee` |

## Persistencia

| Base | Evidencia |
|---|---|
| `core_db.checkout_requests` | `status=COMPLETED`, `total_amount=1299.99` |
| `core_db.outbox_events` | `checkout.requested`, `published=t` |
| `checkout_db.orders` | `order_id=43abeb10-c105-4814-888a-4d84ca570194`, `status=COMPLETED` |
| `checkout_db.payment_attempts` | `status=SUCCESS`, `attempt_number=1` |
| `checkout_db.inbox_events` | `processed=t` |

## Resultado

Estado: `CERRADO`.

Observacion: RabbitMQ usa broker real; el pago sigue siendo simulado y no involucra Stripe real.
