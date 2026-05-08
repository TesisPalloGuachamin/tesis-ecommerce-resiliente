# Bitácora de Ejecución — ARQ-DIAG-01
## Bloque: Diagramas de Arquitectura Final
**Fecha**: 2026-05-07  
**Ejecutor**: Claude Code — ejecutor técnico ARQ-DIAG-01

---

## Auditoría realizada

### Código leído (solo lectura, sin modificación)

| Archivo | Propósito de la auditoría |
|---|---|
| `apps/core-api/src/.../controller/*.java` | Extraer endpoints HTTP reales (5 controllers, 14 endpoints) |
| `apps/core-api/src/.../usecase/*.java` | Catalogar 13 use cases reales |
| `apps/core-api/src/.../port/out/*.java` | Catalogar 9 puertos de salida |
| `apps/checkout-service/src/.../RabbitMqConfig.java` | Extraer exchange, queues, routing keys, DLX exactos |
| `apps/checkout-service/src/.../ProcessCheckoutUseCase.java` | Mapear flujo interno completo de checkout |
| `apps/checkout-service/src/.../CheckoutEventConsumer.java` | Confirmar qué queues consume checkout-service |
| `apps/checkout-service/src/.../StripeSimulatedPaymentAdapter.java` | Confirmar comportamiento simulado (in-process, stripe_sim_*) |
| `apps/core-api/src/.../CheckoutEventConsumer.java` | Confirmar que core-api consume checkout.completed y checkout.failed |
| `apps/core-api/src/.../CheckoutUseCase.java` | Confirmar publicación de checkout.requested |
| `apps/mobile/src/api.ts` | Verificar todos los endpoints que consume la app móvil |
| `apps/mobile/src/types.ts` | Verificar tipos: User, Product, Cart, CheckoutRequest, Listing |
| `apps/mobile/src/config.ts` | Confirmar API_BASE_URL via EXPO_PUBLIC_API_BASE_URL |
| `apps/core-api/src/main/resources/db/migration/V1__initial_schema.sql` | Confirmar tablas reales de core_db (7 tablas) |
| `apps/checkout-service/src/main/resources/db/migration/V1__initial_schema.sql` | Confirmar tablas reales de checkout_db (6 tablas) |
| `docker-compose.yml` | Confirmar topología de despliegue raíz |
| `deploy/compose/docker-compose.dev.yml` | Confirmar configuración Prometheus/Grafana |
| `evidencias/devops-obs-01/` | Confirmar estado observado/diseñado de observabilidad |
| `evidencias/devops-drp-02/` | Confirmar estado DRP (AWS, Azure) |

### Estado de runtime auditado (solo lectura)
- `docker ps`: 5 contenedores healthy, 0 reinicios
- `/actuator/health`: UP (db, rabbit, ping, diskSpace)
- `/actuator/prometheus`: 403 (confirmado: micrometer ausente)
- RabbitMQ Management API: 9 colas, exchange topology confirmada

---

## Decisiones de diseño de diagramas

| Decisión | Razón |
|---|---|
| Prometheus/Grafana aparecen en DEP-01 como "no corriendo" | Es la situación real: configurados pero sin micrometer en pom.xml |
| Stripe no aparece como contenedor externo | Es in-process dentro de checkout-service; no hay red |
| AWS aparece como infraestructura en DEP-01, no como dominio | La plataforma es el software, AWS es el runtime |
| DRP-ARCH-01 divide en 3 capas | Refleja la clasificación real: observado / diseñado / documental |
| C4-03 incluye Spring Security como componente | Es real y relevante (JwtAuthFilter bloquea actuator/metrics) |
| INT-01 es un diagrama de secuencia, no de componentes | El flujo async tiene orden temporal crítico para entenderlo |
| Outbox pattern mencionado en C4-03 como modelo | outbox_events existe en el schema, aunque el polling scheduler está implícito |

---

## Archivos generados

| # | Archivo | Formato | Descripción |
|---|---|---|---|
| 1 | C4-01_context.puml | PlantUML C4 | Diagrama de Contexto |
| 2 | C4-01_context.mmd | Mermaid | Idem — formato alternativo |
| 3 | C4-02_containers.puml | PlantUML C4 | Diagrama de Contenedores |
| 4 | C4-02_containers.mmd | Mermaid | Idem |
| 5 | C4-03_core_api_components.puml | PlantUML C4 | Componentes core-api |
| 6 | C4-04_checkout_service_components.puml | PlantUML C4 | Componentes checkout-service |
| 7 | INT-01_async_messaging.puml | PlantUML Sequence | Flujo checkout asíncrono |
| 8 | INT-01_async_messaging.mmd | Mermaid Sequence | Idem |
| 9 | INT-02_stripe_simulated.puml | PlantUML Component | Stripe adaptador simulado |
| 10 | INT-02_stripe_simulated.mmd | Mermaid | Idem |
| 11 | DEP-01_sandbox_deployment.puml | PlantUML Deployment | Topología de despliegue |
| 12 | DEP-01_sandbox_deployment.mmd | Mermaid | Idem |
| 13 | DRP-ARCH-01_resilience_view.puml | PlantUML Package | Vista DRP observado/diseñado/documental |
| 14 | DRP-ARCH-01_resilience_view.mmd | Mermaid | Idem |
| 15 | TRAZABILIDAD_diagrama_componente_evidencia.md | Markdown | Tabla de trazabilidad |
| 16 | ALCANCE_ARQUITECTONICO_resiliencia.md | Markdown | Nota técnica resiliencia |
| 17 | ESTADO_ARQUITECTURA.txt | Texto | Estado para coordinación |
| 18 | ESTADO_GLOBAL_suggestion.txt | Texto | Párrafo para ESTADO_GLOBAL |
| 19 | BITACORA_ARQ_DIAG_01.md | Markdown | Este archivo |
| 20 | 00_arq_diag01_summary.json | JSON | Resumen ejecutivo máquina |

---

## Rollback
No aplicado. Solo lectura de código y generación de nuevos archivos de documentación.
Ningún archivo de código ni infraestructura fue modificado.
