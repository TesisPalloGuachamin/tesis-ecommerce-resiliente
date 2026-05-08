# Tabla de Trazabilidad — ARQ-DIAG-01
## Diagrama ↔ Componente Implementado ↔ Evidencia / Bloque PASS

| Diagrama | Componentes representados | Ubicación en código | Bloque(s) PASS | Evidencia verificable |
|---|---|---|---|---|
| **C4-01 Contexto** | Sistema completo, comprador/vendedor, Stripe simulado, AWS infraestructura | — (vista de nivel 1) | MOBILE-INT-01, BACKEND-SELL-01, PAY-SIM-01 | evidencias/pay-sim-01/checkout-flow-aws.json |
| **C4-02 Contenedores** | core-api, checkout-service, RabbitMQ, core_db, checkout_db, app móvil | apps/, deploy/compose/, docker-compose.yml | DEVOPS-REBUILD-01, DEVOPS-IAC-01, BQ-QA-SL-01 | evidencias/bq-qa-sl-01/01_health_check.json |
| **C4-03 Componentes core-api** | AuthController, ProductController, CartController, CheckoutController, ListingController, CheckoutEventConsumer (in), JPA adapters, EventPublisherAdapter, JwtProviderAdapter, PasswordEncoderAdapter | apps/core-api/src/main/java/.../ | MF-QA-01 a MF-QA-08, BQ-QA-MO-01, BQ-QA-SL-01 | evidencias/bq-qa-sl-01/04_api_post_create_listing.json |
| **C4-04 Componentes checkout-service** | CheckoutEventConsumer, ProcessCheckoutUseCase, StripeSimulatedPaymentAdapter, RabbitEventPublisher, JPA adapters, InboxEvent (idempotencia) | apps/checkout-service/src/main/java/.../ | PAY-SIM-01, BQ-QA-MO-01, BQ-QA-RL-01 | evidencias/pay-sim-01/checkout-service-stripe-simulated-logs.txt |
| **INT-01 Comunicación async** | ecommerce.checkout.exchange (topic), 9 colas declaradas, routing keys, DLX/DLQ, flujo checkout.requested→accepted→payment.processed→completed | apps/checkout-service/.../RabbitMqConfig.java, CheckoutEventConsumer.java, RabbitEventPublisher.java | BQ-QA-MO-01, PAY-SIM-01, DEVOPS-OBS-01 | evidencias/devops-obs-01/02_rabbitmq_messaging_observed.json |
| **INT-02 Stripe simulado** | StripeSimulatedPaymentAdapter, PaymentPort (interface), in-process call | apps/checkout-service/.../payment/StripeSimulatedPaymentAdapter.java | PAY-SIM-01 | evidencias/pay-sim-01/stripe-simulated-adapter-test.xml |
| **DEP-01 Despliegue sandbox** | 5 contenedores Docker, puertos, volúmenes, healthchecks, EC2 AWS | docker-compose.yml, deploy/compose/docker-compose.ec2.yml, infra/environments/demo/ | DEVOPS-REBUILD-01, DEVOPS-IAC-01, DEVOPS-F6-01 | evidencias/devops-drp-02/aws-current-health.json |
| **DRP-ARCH-01 Vista resiliencia** | AWS mismo-región (observado), AWS alterna (diseñado), Azure (Terraform-validado), GCP (documental), fallback propio (documental) | infra/environments/demo/, infra/environments/azure-drp/, docs/devops/ | DEVOPS-DRP-01, DEVOPS-F6-01, DEVOPS-DRP-02 | evidencias/devops-obs-01/08_drp_matrix_final.json |

---

## Contratos de API verificados y trazables

| Endpoint | Método | Autenticación | HTTP Status | Evidencia |
|---|---|---|---|---|
| `/api/v1/auth/register` | POST | Público | 200 | bq-qa-sl-01/02_register_user.json |
| `/api/v1/auth/login` | POST | Público | 200 | mobile-sell-01/01_login_response.json |
| `/api/v1/products` | GET | Bearer JWT | 200 | mobile-int-01/api-flow.json |
| `/api/v1/cart` | GET | Bearer JWT | 200 | backend-sell-01/cart-add-smoke.json |
| `/api/v1/cart/items` | POST | Bearer JWT | 200 | backend-sell-01/cart-add-smoke.json |
| `/api/v1/checkout` | POST | Bearer JWT | 201 | backend-sell-01/checkout-create-smoke.json |
| `/api/v1/checkout/{requestId}` | GET | Bearer JWT | 200 | backend-sell-01/checkout-final-smoke.json |
| `/api/v1/listings` | POST | Bearer JWT | 201 | bq-qa-sl-01/04_api_post_create_listing.json |
| `/api/v1/listings` | GET | Bearer JWT | 200 | bq-qa-sl-01/06_api_get_listings_after.json |
| `/api/v1/listings/{listingId}` | GET | Bearer JWT | 200 | bq-qa-sl-01/05_api_get_listing_by_id.json |
| `/actuator/health` | GET | Público | 200 | bq-qa-sl-01/01_health_check.json |

---

## Eventos de mensajería verificados y trazables

| Routing Key | Publicador | Consumidor | Queue | Mensajes observados | Evidencia |
|---|---|---|---|---|---|
| `checkout.requested` | core-api (EventPublisherAdapter) | checkout-service (CheckoutEventConsumer) | checkout-service.checkout-requested.q | 2 deliver | devops-obs-01/02_rabbitmq_messaging_observed.json |
| `checkout.accepted` | checkout-service (RabbitEventPublisher) | (sin consumidor externo en sandbox) | checkout-service.checkout-accepted.q | 2 published | devops-obs-01/03_checkout_service_logs_observed.txt |
| `payment.processed` | checkout-service | (sin consumidor externo) | checkout-service.payment-processed.q | 2 published | devops-obs-01/03_checkout_service_logs_observed.txt |
| `checkout.completed` | checkout-service | core-api (CheckoutEventConsumer) | core-api.checkout-completed.q | 2 deliver | devops-obs-01/02_rabbitmq_messaging_observed.json |
| `checkout.failed` | checkout-service | core-api | core-api.checkout-failed.q | 0 (sin fallos) | devops-obs-01/02_rabbitmq_messaging_observed.json |

---

## Nota de uso para Capítulo 4

- Los archivos `.puml` se renderizan con PlantUML (plugin IntelliJ, PlantUML online, o CLI).
- Los archivos `.mmd` se renderizan con Mermaid (GitHub native, Mermaid Live Editor, Obsidian).
- Todos los componentes de los diagramas corresponden a código real verificable en el repositorio.
- No hay componentes inventados ni flujos hipotéticos sin implementación.
