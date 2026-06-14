# Reporte compra COMPLETED 100%

RUN_ID: `cap4-core4-20260614-162647`

## Criterio de cierre

El bloque se considera cerrado porque el flujo nuevo de compra queda en estado final `COMPLETED` y se puede rastrear el mismo `requestId` en request/response, RabbitMQ, logs y base de datos.

## Evidencia principal

| Evidencia | Archivo |
|---|---|
| Request/response API saneado | `compra_COMPLETED_100_flow.json` |
| Consulta de base de datos | `db_compra_COMPLETED_100.txt` |
| Logs relevantes | `logs_compra_COMPLETED_100.txt` |
| Estado de colas RabbitMQ | `../04_rabbitmq/rabbitmq_queues_100.json` |
| IDs trazables | `../04_rabbitmq/trace_ids_100.txt` |

## Autenticacion

| Campo | Valor observado |
|---|---|
| Endpoint | `POST /api/v1/auth/register` |
| HTTP status | `201` |
| Token | `***MASKED***` |
| Usuario de prueba | `cap4-core4-20260614-162647-buyer-1781456224@example.com` |

## Catalogo

| Campo | Valor observado |
|---|---|
| Endpoint | `GET /api/v1/products` |
| HTTP status | `200` |
| Producto seleccionado | `Laptop Dell XPS 13` |
| SKU | `PROD001` |
| productId | `3521ffba-1fac-4988-b9be-02216bd3360f` |
| Precio | `1299.99` |

## Carrito

| Campo | Valor observado |
|---|---|
| Endpoint | `POST /api/v1/cart/items` |
| HTTP status | `201` |
| cartId | `51d68b64-0c92-4708-8fd6-566f6e2e1827` |
| Cantidad | `1` |
| Total | `1299.99` |

## Checkout

| Campo | Valor observado |
|---|---|
| Endpoint creacion | `POST /api/v1/checkout` |
| Request | `{"cartId":"51d68b64-0c92-4708-8fd6-566f6e2e1827"}` |
| HTTP status creacion | `202` |
| requestId | `bf08ff9a-ed24-4baf-8643-1d8c740107ee` |
| Endpoint consulta | `GET /api/v1/checkout/bf08ff9a-ed24-4baf-8643-1d8c740107ee` |
| HTTP status consulta | `200` |
| Estado final | `COMPLETED` |
| Total | `1299.99` |

## Base de datos

| Base | Evidencia observada |
|---|---|
| `core_db.checkout_requests` | `request_id=bf08ff9a-ed24-4baf-8643-1d8c740107ee`, `status=COMPLETED`, `total_amount=1299.99` |
| `core_db.outbox_events` | `event_type=checkout.requested`, `published=t` |
| `checkout_db.orders` | `order_id=43abeb10-c105-4814-888a-4d84ca570194`, `status=COMPLETED`, `total_amount=1299.99` |
| `checkout_db.payment_attempts` | `payment_attempt=c60bd7ba-067a-4727-b647-2af3be85a53a`, `status=SUCCESS`, `amount=1299.99` |
| `checkout_db.inbox_events` | `event_id=ef74b7fc-21ae-4365-a0d5-515a8c92e741`, `event_type=checkout.requested`, `processed=t` |

## Logs

| Servicio | Evidencia observada |
|---|---|
| `core-api` | Recibe `checkout.completed` y actualiza `Checkout completed for request: bf08ff9a-ed24-4baf-8643-1d8c740107ee` |
| `checkout-service` | Consume `checkout.requested`, ejecuta `StripeSimulatedPaymentAdapter`, publica `payment.processed` y `checkout.completed` |

## Resultado

Estado: `CERRADO`.

Observacion: el pago es simulado; no existe llamada real a Stripe ni transaccion financiera.
