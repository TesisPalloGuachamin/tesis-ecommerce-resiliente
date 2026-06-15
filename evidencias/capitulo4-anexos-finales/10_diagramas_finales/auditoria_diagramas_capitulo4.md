# Auditoria de diagramas Capitulo 4

| Diagrama | Fuente encontrada | Correccion aplicada | Estado | Observacion |
|---|---|---|---|---|
| C4-01 Contexto del sistema | `evidencias/arq-diag-01/C4-01_context.mmd`; `C4-01_context.puml` | Se genero fuente final `fuentes/C4-01-contexto-sistema.mmd`; typo "incia checkout" corregido a "inicia checkout"; host generico `AWS principal sandbox`; pago simulado sin pasarela real | CERRADO | Renderizado en PNG/SVG |
| C4-02 Contenedores | `evidencias/arq-diag-01/C4-02_containers.mmd`; `C4-02_containers.puml` | Se agrego observabilidad operativa verificada; mobile queda como cliente configurado por `EXPO_PUBLIC_API_BASE_URL`, no failover; RabbitMQ como desacoplamiento asincrono | CERRADO | Renderizado en PNG/SVG |
| C4-03 Componentes `core-api` | `evidencias/arq-diag-01/C4-03_core_api_components.puml` | Se genero fuente final Mermaid equivalente, alineada con controladores, casos de uso, dominio, puertos y adapters reales | CERRADO | Renderizado en PNG/SVG |
| C4-04 Componentes `checkout-service` | `evidencias/arq-diag-01/C4-04_checkout_service_components.puml` | Se genero fuente final Mermaid equivalente; pago queda como `StripeSimulatedPaymentAdapter` in-process, sin API real | CERRADO | Renderizado en PNG/SVG |
| INT-01 Mensajeria RabbitMQ | `evidencias/arq-diag-01/INT-01_async_messaging.mmd`; `INT-01_async_messaging.puml` | Se mantuvo flujo asincrono core-api -> RabbitMQ -> checkout-service -> RabbitMQ -> core-api; se aclara que RabbitMQ desacopla, no da resiliencia automatica por si mismo | CERRADO | Renderizado en PNG/SVG |
| INT-02 Pago simulado | `evidencias/arq-diag-01/INT-02_stripe_simulated.mmd`; `INT-02_stripe_simulated.puml` | Se cambio el enfoque a adaptador conceptual/simulado; se explicita sin SDK, credenciales, llamada externa ni transaccion financiera | CERRADO | Renderizado en PNG/SVG |
| DEP-01 Despliegue sandbox | `evidencias/arq-diag-01/DEP-01_sandbox_deployment.mmd`; `DEP-01_sandbox_deployment.puml` | Se reemplazo "configurado/no corriendo" por Prometheus/Grafana verificados como observabilidad operativa; se usa "AWS principal sandbox" | CERRADO | Renderizado en PNG/SVG |
| DRP-ARCH-01 Resiliencia/DRP | `evidencias/arq-diag-01/DRP-ARCH-01_resilience_view.mmd`; `DRP-ARCH-01_resilience_view.puml` | Se elimino host literal; se reemplazo "Produccion sandbox" por clasificacion formal: AWS principal observado, AWS alterna con limitaciones, Azure alcance minimo, GCP/on-premise documental | CERRADO | Renderizado en PNG/SVG |

## Hallazgos principales

- Las fuentes originales contenian elementos desactualizados de observabilidad y DRP.
- Existia un typo en C4-01: "incia checkout".
- La vista DRP original contenia host literal y lenguaje de "Produccion sandbox"; la version final usa `AWS principal sandbox` y no expone host.
- Las versiones finales no agregan Kubernetes, EKS/AKS, API Gateway, Lambda, Cognito, DynamoDB, S3, CloudTrail, SNS ni servicios no implementados.
- Los diagramas finales representan el prototipo academico observado, no una arquitectura empresarial productiva.
