# Nota Técnica Final — Logging Implementado  
## DEVOPS-OBS-01 | Tesis e-commerce sandbox

---

## Lo que SÍ quedó implementado (OBSERVADO)

### 1. Logging stdout de Spring Boot (docker logs)

Ambos servicios (core-api, checkout-service) emiten logs estructurados a stdout con el patrón estándar de Spring Boot. Son capturables vía `docker logs <container>`.

**Niveles configurados:**
- `core-api`: ROOT=INFO, `com.tesis.ecommerce`=DEBUG, Spring Security=DEBUG
- `checkout-service`: ROOT=WARN, `com.tesis.ecommerce`=INFO

**Lo que se observa en los logs del checkout-service:**
```
INFO  CheckoutEventConsumer    : Received checkout.requested event: <uuid>
INFO  ProcessCheckoutUseCase   : Processing checkout for request: <uuid>
INFO  RabbitEventPublisher     : Published checkout.accepted event for checkout request: <uuid>
INFO  StripeSimulatedPaymentAdapter : Executing Stripe simulated payment for order: <uuid>, amount: 1299.99
INFO  StripeSimulatedPaymentAdapter : Stripe simulated payment approved for order: <uuid>, transaction: stripe_sim_<uuid>
INFO  RabbitEventPublisher     : Published payment.processed event for order: <uuid>, success: true
INFO  RabbitEventPublisher     : Published checkout.completed event for checkout request: <uuid>
INFO  ProcessCheckoutUseCase   : Checkout completed successfully for order: ORD-<hex>
INFO  CheckoutEventConsumer    : Checkout processed successfully for event: <uuid>
```

**Lo que se observa en core-api (DEBUG security):**
```
DEBUG FilterChainProxy : Securing <METHOD> <PATH>
DEBUG AnonymousAuthenticationFilter : Set SecurityContextHolder to anonymous SecurityContext
DEBUG FilterChainProxy : Secured <METHOD> <PATH>
```

**Evidencia capturada:** `evidencias/devops-obs-01/03_checkout_service_logs_observed.txt`

### 2. Spring Boot Actuator /actuator/health

Expuesto sin autenticación. Muestra estado de db (PostgreSQL), rabbit (RabbitMQ), ping y diskSpace. Observable desde cualquier herramienta de monitoreo o healthcheck externo.

---

## Lo que NO quedó implementado

| Característica | Estado | Razón |
|---|---|---|
| `micrometer-registry-prometheus` | NO en pom.xml | No se agregó la dependencia. /actuator/prometheus retorna 403. |
| Scraping Prometheus de backend Java | NO activo | Jobs comentados en prometheus.yml (dependen de micrometer) |
| Grafana con datos reales de backend | NO | Prometheus no scrapeó backend; dashboard existe pero sin series |
| Centralización de logs (ELK/Loki/Fluentd) | NO | No forma parte del stack implementado. Solo stdout. |
| Log shipping a storage externo | NO | Sin agente (Filebeat, Promtail, Fluent Bit) configurado |
| Alertas (Alertmanager) | NO | alertmanagers: [] en prometheus.yml |
| Trazas distribuidas (Jaeger/Zipkin) | NO | No implementado en este proyecto |
| Retención histórica de métricas | NO | Sin storage externo de series temporales |

---

## Cómo debe explicarse en tesis sin sobredimensionarlo

### Redacción recomendada para Capítulo 4:

> "La observabilidad operativa del entorno sandbox se logró mediante tres mecanismos complementarios: (1) el endpoint `/actuator/health` de Spring Boot Actuator, que expone el estado de todos los componentes críticos (base de datos, mensajería, proceso) en tiempo real; (2) los logs de aplicación en formato stdout, capturables vía `docker logs`, que registran el ciclo completo de cada evento de checkout incluyendo el procesamiento del pago simulado; y (3) la API de administración de RabbitMQ (puerto 15672/15674), que permite observar el estado de colas, contadores de mensajes y confirmaciones de entrega."
>
> "La infraestructura de Prometheus y Grafana fue diseñada, configurada y versionada en el repositorio (`deploy/compose/`), pero no alcanzó a operar con scraping activo de los servicios Java durante el período de validación, dado que la dependencia `micrometer-registry-prometheus` no fue incorporada a los servicios backend. El conjunto de consultas PromQL fue diseñado y documentado como parte del paquete de observabilidad (Anexo X), con la clasificación explícita entre métricas observadas y métricas de apoyo analítico pendientes de activación."
>
> "No se implementó centralización de logs, alertas automáticas ni trazabilidad distribuida. El alcance de observabilidad corresponde a un entorno académico sandbox orientado a la verificación funcional del flujo, no a monitoreo de producción."

---

## Conclusión operativa

El logging y la observabilidad **implementados y verificados** son suficientes para:
- Trazar el flujo completo de un checkout de extremo a extremo
- Verificar el estado del stack en cualquier momento
- Detectar errores en el procesamiento de mensajes (DLQ vacía = sin errores)
- Confirmar persistencia de datos en ambas bases de datos

Lo que **no puede afirmarse** como observado:
- Latencias HTTP medidas por Prometheus
- Memory/CPU trends de JVM
- Alert triggers basados en umbrales
