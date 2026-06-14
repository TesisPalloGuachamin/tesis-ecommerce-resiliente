# Reporte observabilidad Prometheus/Grafana

RUN_ID: `cap4-obs-20260614-152854`

## 1. Propósito

La observabilidad se usa para verificar estado operativo, health checks y métricas técnicas del prototipo en sandbox. No forma parte del dominio funcional de compra/venta, no representa monitoreo productivo empresarial y no define SLA.

## 2. Componentes observados

- `core-api`.
- `checkout-service`.
- Prometheus.
- Grafana.
- RabbitMQ como dependencia observada del flujo asincrono.
- PostgreSQL como dependencia indirecta de los servicios.

## 3. Diagnóstico inicial

Se verifico que `core-api`, `checkout-service`, RabbitMQ y PostgreSQL estaban activos. Prometheus y Grafana existian como contenedores del stack, pero estaban detenidos al inicio del diagnostico. Los endpoints de métricas Actuator de ambos servicios respondian HTTP 200, por lo que el problema no era ausencia de métricas en Spring Boot.

La observacion previa `activeTargets: 0` se explica por el stack de observabilidad detenido o consultado antes de que Prometheus estabilizara el scrape manager. Al levantar el contenedor existente y esperar la inicializacion, Prometheus descubrio los targets configurados.

## 4. Corrección aplicada

| Elemento | Antes | Despues | Motivo |
|---|---|---|---|
| `tesis-prometheus` | Contenedor detenido | Contenedor activo | Habilitar scrape de metricas existentes |
| `tesis-grafana` | Contenedor detenido | Contenedor activo | Validar health/API y datasource |
| `prometheus.yml` | Ya contenia scrape configs | Sin cambios | La configuracion era correcta |
| Codigo de servicios | Actuator/Micrometer disponible | Sin cambios | No era necesario modificar aplicacion |

Correccion ejecutada: levantar contenedores existentes con `docker start tesis-prometheus tesis-grafana`. Adicionalmente, se habilito acceso SSH temporal minimo para diagnostico desde la IP de trabajo, sin exponer credenciales en evidencias.

## 5. Evidencia de Prometheus

- Health/API Prometheus: HTTP 200 en `/api/v1/targets`, `/api/v1/status/config` y `/api/v1/query`.
- Targets activos observados: `3`.

| Job | Estado | Scrape URL |
|---|---|---|
| `checkout-service` | `up` | `http://checkout-service:8082/api/actuator/prometheus` |
| `core-api` | `up` | `http://core-api:8080/actuator/prometheus` |
| `prometheus` | `up` | `http://prometheus:9090/metrics` |

- PromQL `up`: `3` series.
- PromQL `up{job=~"core-api|checkout-service"}`: `2` series, ambas con valor `1`.
- PromQL `http_server_requests_seconds_count`: `4` series.
- PromQL `jvm_memory_used_bytes`: `16` series.

Evidencia asociada: `prometheus_targets_after.json`, `prometheus_query_up.json`, `prometheus_query_up_core_checkout.json`, `prometheus_query_http_metric.json`, `prometheus_query_jvm_metric.json`, `prometheus_status_config.json`.

## 6. Evidencia de métricas Actuator

| Servicio | Endpoint | HTTP | Evidencia |
|---|---|---:|---|
| `core-api` | `http://AWS_PRINCIPAL_HOST:8080/actuator/prometheus` | 200 | `actuator_core_api_metrics.txt` |
| `checkout-service` | `http://AWS_PRINCIPAL_HOST:8082/api/actuator/prometheus` | 200 | `actuator_checkout_service_metrics.txt` |

Las muestras incluyen metricas HTTP, JVM y proceso en formato Prometheus. Los dumps completos se acotaron para evitar archivos innecesariamente grandes.

## 7. Evidencia de Grafana

- Endpoint validado: `http://AWS_PRINCIPAL_HOST:3000/api/health`.
- Estado: HTTP 200, database `ok`, version `13.0.1`.
- Datasource provisionado: `Prometheus`, tipo `prometheus`, URL interna `http://prometheus:9090`, `isDefault: true`.
- No se incluye captura visual de dashboard porque el cierre solicitado se valida por health/API y datasource. El uso visual queda como soporte, no como criterio de cierre funcional.

## Evidencia visual complementaria

Se agregaron capturas visuales como apoyo documental del cierre de observabilidad. Estas capturas no sustituyen la evidencia principal, que sigue siendo Prometheus API, PromQL, `/actuator/prometheus`, Grafana health/API, logs y configuracion.

Capturas generadas:

- `capturas/obs_01_prometheus_targets_up.png`: Prometheus `/targets` con `checkout-service`, `core-api` y `prometheus` en estado `UP`.
- `capturas/obs_02_prometheus_query_up.png`: PromQL `up` con 3 series y valor `1`.
- `capturas/obs_03_prometheus_query_core_checkout.png`: PromQL `up{job=~"core-api|checkout-service"}` con 2 series y valor `1`.
- `capturas/obs_04_grafana_health.png`: evidencia visual equivalente de Grafana `/api/health`.
- `capturas/obs_05_grafana_datasource_prometheus.png`: evidencia visual del datasource Prometheus en Grafana.
- `capturas/obs_05_grafana_datasource_prometheus.json`: JSON sanitizado del datasource Prometheus.

## 8. Criterios de aceptación

| Criterio | Resultado | Estado |
|---|---|---|
| Prometheus responde HTTP 200 | `/api/v1/targets` y PromQL responden | CERRADO |
| `/api/v1/targets` muestra targets activos | 3 targets activos | CERRADO |
| `core-api` observable | Target `core-api` `up` | CERRADO |
| `checkout-service` observable | Target `checkout-service` `up` | CERRADO |
| `/actuator/prometheus` responde | HTTP 200 en ambos servicios | CERRADO |
| PromQL `up` devuelve resultados | 3 series | CERRADO |
| Grafana responde health/API | HTTP 200, database `ok` | CERRADO |
| No exponer credenciales/IP publica literal | Host enmascarado como `AWS_PRINCIPAL_HOST` | CERRADO |

## 9. Resultado final

Estado del bloque de observabilidad: `CERRADO`.

Prometheus tiene targets activos y servicios observables. `core-api` y `checkout-service` estan `UP`, sus endpoints Actuator Prometheus responden HTTP 200, PromQL devuelve series tecnicas y Grafana responde health/API.

## 10. Limitaciones

- Entorno sandbox academico.
- No es monitoreo productivo empresarial.
- No constituye SLA.
- No representa alta disponibilidad empresarial.
- El dashboard visual de Grafana no se usa como criterio de cierre; se valida health/API y datasource.
- Observabilidad usada como soporte de validacion tecnica del prototipo.
- La correccion fue operativa: levantar contenedores existentes, sin modificar codigo ni arquitectura.
