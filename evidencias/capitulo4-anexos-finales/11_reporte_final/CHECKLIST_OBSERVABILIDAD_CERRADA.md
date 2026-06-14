# Checklist observabilidad Prometheus/Grafana cerrada

RUN_ID: `cap4-obs-20260614-152854`

| Elemento | Evidencia principal | Resultado observado | Estado | Observacion |
|---|---|---|---|---|
| Prometheus health | `07_observabilidad/prometheus_targets_after.json`; `07_observabilidad/prometheus_status_config.json` | API Prometheus responde HTTP 200 | CERRADO | Contenedor existente levantado |
| Targets activos | `07_observabilidad/prometheus_targets_after.json` | 3 activeTargets: `core-api`, `checkout-service`, `prometheus` | CERRADO | Todos en estado `up` |
| core-api observable | `07_observabilidad/actuator_core_api_metrics.txt`; `07_observabilidad/prometheus_query_up_core_checkout.json` | `/actuator/prometheus` HTTP 200; PromQL `up=1` | CERRADO | Métricas HTTP/JVM disponibles |
| checkout-service observable | `07_observabilidad/actuator_checkout_service_metrics.txt`; `07_observabilidad/prometheus_query_up_core_checkout.json` | `/api/actuator/prometheus` HTTP 200; PromQL `up=1` | CERRADO | Métricas HTTP/JVM disponibles |
| PromQL | `07_observabilidad/prometheus_query_up.json`; `07_observabilidad/prometheus_query_http_metric.json`; `07_observabilidad/prometheus_query_jvm_metric.json` | `up`, HTTP y JVM devuelven series | CERRADO | Consultas básicas ejecutadas |
| Grafana health/API | `07_observabilidad/grafana_health.txt` | `/api/health` HTTP 200; database `ok`; datasource Prometheus provisionado | CERRADO | Sin captura visual requerida |
| Evidencia visual complementaria | `07_observabilidad/capturas/obs_01_prometheus_targets_up.png`; `07_observabilidad/capturas/obs_02_prometheus_query_up.png`; `07_observabilidad/capturas/obs_03_prometheus_query_core_checkout.png`; `07_observabilidad/capturas/obs_04_grafana_health.png`; `07_observabilidad/capturas/obs_05_grafana_datasource_prometheus.png` | Capturas de Prometheus targets, PromQL, Grafana health y datasource | CERRADO | Apoyo visual; la evidencia principal sigue siendo API/PromQL/logs/configuracion |

## Estado general

Bloque de observabilidad Prometheus/Grafana: `CERRADO`.

No se tocaron compra, venta, pruebas backend, RabbitMQ, carga, DRP, mobile, XP ni diagramas. No se hizo commit, push ni ZIP.
