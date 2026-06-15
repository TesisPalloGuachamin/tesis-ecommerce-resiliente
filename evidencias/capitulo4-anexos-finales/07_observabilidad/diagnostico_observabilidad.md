# Diagnostico de observabilidad Prometheus/Grafana

RUN_ID: `cap4-obs-20260614-152854`

## Estado inicial observado

- `core-api`: contenedor activo y health `UP`.
- `checkout-service`: contenedor activo y health `UP`.
- RabbitMQ y PostgreSQL: contenedores activos como dependencias del prototipo.
- `tesis-prometheus`: contenedor existente, pero detenido al inicio del diagnostico.
- `tesis-grafana`: contenedor existente, pero detenido al inicio del diagnostico.
- Los endpoints `/actuator/prometheus` de `core-api` y `/api/actuator/prometheus` de `checkout-service` respondian HTTP 200 antes de la correccion.

## Causa tecnica de `activeTargets: 0`

La causa verificable fue operativa: Prometheus y Grafana estaban detenidos. La configuracion de Prometheus no estaba vacia y ya contenia `scrape_configs` para `core-api`, `checkout-service` y `prometheus`, pero el stack de observabilidad no estaba activo de forma estable al momento de la captura previa.

Durante la campana de carga se habia observado respuesta HTTP de Prometheus con `activeTargets: 0`. Con el diagnostico actual se confirma que, tras levantar el contenedor existente y esperar la inicializacion del scrape manager, Prometheus pobló 3 targets activos. Por tanto, el problema no era ausencia de Actuator/Micrometer ni falta de `scrape_configs`, sino observabilidad detenida o consultada antes de estabilizar targets.

## Configuracion validada

- Archivo Prometheus: `/home/ec2-user/tesis-ecommerce/deploy/compose/prometheus/prometheus.yml`.
- Jobs configurados: `prometheus`, `core-api`, `checkout-service`.
- Targets internos: `prometheus:9090`, `core-api:8080`, `checkout-service:8082`.
- Red Docker observada: `compose_tesis-network`, con alias DNS para `core-api`, `checkout-service`, `prometheus` y `grafana`.
- Dependencias Spring validadas en repositorio: `spring-boot-starter-actuator` y `micrometer-registry-prometheus`.

## Correccion minima aplicada

- Se habilito acceso SSH temporal minimo para la IP de trabajo mediante regla `/32`, sin registrar credenciales en evidencias.
- Se levantaron contenedores existentes: `docker start tesis-prometheus tesis-grafana`.
- No se modifico codigo de aplicacion.
- No se modifico arquitectura.
- No se agregaron servicios nuevos.
- No se repitieron pruebas de carga ni pruebas funcionales.

## Resultado posterior

- Prometheus `/api/v1/targets`: 3 targets activos.
- Target `checkout-service`: `up` en `http://checkout-service:8082/api/actuator/prometheus`.
- Target `core-api`: `up` en `http://core-api:8080/actuator/prometheus`.
- Target `prometheus`: `up` en `http://prometheus:9090/metrics`.
- PromQL `up`: 3 series.
- PromQL `up{job=~"core-api|checkout-service"}`: 2 series.
- PromQL HTTP: 4 series.
- PromQL JVM: 16 series.
- Grafana `/api/health`: HTTP 200, database `ok`.

## Evidencias generadas

- `prometheus_targets_after.json`
- `prometheus_query_up.json`
- `prometheus_query_up_core_checkout.json`
- `prometheus_query_http_metric.json`
- `prometheus_query_jvm_metric.json`
- `prometheus_status_config.json`
- `actuator_core_api_metrics.txt`
- `actuator_checkout_service_metrics.txt`
- `grafana_health.txt`
- `logs_observabilidad.txt`
