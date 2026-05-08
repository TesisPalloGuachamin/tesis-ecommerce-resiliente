# Nota Técnica: Alcance Arquitectónico de Resiliencia y DRP
## ARQ-DIAG-01 — Para Capítulo 4

---

## Línea oficial del proyecto

> Plataforma móvil resiliente para compra y venta, basada en arquitectura hexagonal,
> con DevOps y DRP, en entorno sandbox.
>
> Recuperación: controlada, medible y verificable.
> NO: alta disponibilidad enterprise, NO: active-active productivo.

---

## Lo que la arquitectura logra en resiliencia (OBSERVADO)

### 1. Idempotencia en checkout (nivel de servicio)
El `ProcessCheckoutUseCase` implementa el patrón **Inbox** mediante la tabla `inbox_events`.
Antes de procesar cualquier `checkout.requested`, verifica si `eventId` ya fue procesado.
Si sí → retorna el Order existente sin re-ejecutar el pago.

**Evidencia**: `apps/checkout-service/.../ProcessCheckoutUseCase.java` (líneas de verificación `inboxEventRepository.findByEventId`)

### 2. Dead Letter Queue (nivel de mensajería)
La cola `checkout-service.checkout-requested.q` tiene configurado:
- `x-dead-letter-exchange: checkout.dlx`
- `x-dead-letter-routing-key: checkout.requested.dlq`
- `x-message-ttl: 86400000` (24h)

Mensajes que no pueden ser procesados van a `checkout-service.dlq` sin pérdida.

**Evidencia**: `RabbitMqConfig.java` + `devops-obs-01/02_rabbitmq_messaging_observed.json` (DLQ = 0 mensajes)

### 3. Healthchecks Docker (nivel de contenedor)
Todos los contenedores tienen `healthcheck` configurado en el Compose.
Docker Engine reinicia automáticamente servicios que fallan el healthcheck.

**Evidencia**: `deploy/compose/docker-compose.dev.yml`, `docker-compose.yml`

### 4. Separación de dominios por base de datos (nivel de datos)
`core_db` y `checkout_db` son bases de datos independientes. Un fallo en `checkout_db` no afecta la API pública de productos ni el catálogo.

**Evidencia**: `devops-obs-01/04_db_state_observed.json`

### 5. Recuperación total F6 desde Docker Compose (nivel de infraestructura)
El stack completo puede reconstruirse desde cero usando el compose bundle e imágenes Docker existentes. Verificado en AWS EC2.

**Evidencia**: `pay-sim-01/checkout-flow-aws.json` + `devops-drp-02/aws-current-health.json`

---

## Lo que la arquitectura tiene DISEÑADO (sin observar en sandbox)

| Característica | Estado | Qué falta para activar |
|---|---|---|
| Métricas JVM/HTTP via Prometheus | Diseñado | Agregar `micrometer-registry-prometheus` a pom.xml |
| Dashboard Grafana con datos de backend | Diseñado | Prometheus scrapeando + Grafana levantado |
| Recuperación AWS región alterna | Runbook listo | Ejecutar drill + medir RTO/RPO |
| Recuperación Azure | Terraform validado | Ejecutar `terraform apply` + drill |
| Alertas automáticas | No configuradas | Alertmanager + reglas |

---

## Lo que la arquitectura NO tiene (DOCUMENTAL o fuera de alcance)

| Característica | Por qué no está |
|---|---|
| Active-active productivo | Fuera del alcance sandbox académico |
| Failover automático | Requiere orquestador (Kubernetes, ECS) |
| Réplicas de base de datos | Fuera de alcance; un solo contenedor por DB |
| Google Cloud recovery | Solo mencionado como alternativa, sin IaC |
| Logging centralizado (ELK/Loki) | No implementado en el stack |
| Trazabilidad distribuida (Jaeger/Zipkin) | No implementado |
| Circuit breaker (Resilience4j) | No implementado en esta versión |
| Pagos reales Stripe | Adaptador simulado; sin API keys |

---

## Redacción sugerida para Capítulo 4

> "La resiliencia de la plataforma se abordó en tres dimensiones complementarias, diferenciadas explícitamente por su nivel de verificación. En el plano **observado**, se implementaron y verificaron: el patrón Inbox para idempotencia en el procesamiento de checkouts, Dead Letter Queue para mensajes no procesables, healthchecks Docker en todos los contenedores, separación de dominios por base de datos independiente, y recuperación total del stack desde Docker Compose en entorno EC2 AWS. En el plano **diseñado**, se elaboró la infraestructura IaC para recuperación en región alterna AWS y en Azure (Terraform validado), junto con la configuración de observabilidad Prometheus/Grafana para métricas de backend. En el plano **documental**, se registraron rutas de recuperación hacia Google Cloud y fallback degradado a infraestructura propia, como alternativas de último recurso sin métricas observadas. Esta clasificación explícita permite presentar las capacidades reales del sistema sin sobredimensionar el alcance sandbox de la investigación."

---

## Componentes de resiliencia por capa hexagonal

| Capa | Componente | Tipo de resiliencia |
|---|---|---|
| Domain | InboxEvent | Idempotencia funcional |
| Domain | Order, PaymentAttempt | Trazabilidad transaccional |
| Application | ProcessCheckoutUseCase | Orquestación con rollback |
| Infrastructure in | CheckoutEventConsumer | Consumer con DLQ backup |
| Infrastructure out | RabbitMqConfig | DLX + TTL configurados |
| Infrastructure out | StripeSimulatedPaymentAdapter | Adaptador intercambiable (PaymentPort) |
| Deployment | docker-compose healthchecks | Reinicio automático |
| Deployment | Terraform IaC | Reproducibilidad de infraestructura |
