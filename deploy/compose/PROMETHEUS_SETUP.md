# Prometheus y Grafana - Observabilidad Backend

## Estado reproducible

- `core-api` expone metricas Prometheus en `/actuator/prometheus`.
- `checkout-service` expone metricas Prometheus en `/api/actuator/prometheus`.
- Prometheus scrapea `core-api`, `checkout-service` y self-monitoring.
- Grafana provisiona automaticamente el datasource `Prometheus`.
- Grafana carga automaticamente el dashboard `Tesis Backend Services Overview`.

## Archivos versionados

- `deploy/compose/prometheus/prometheus.yml`
- `deploy/compose/grafana/provisioning/datasources/prometheus.yml`
- `deploy/compose/grafana/provisioning/dashboards/dashboards.yml`
- `deploy/compose/grafana/dashboards/services-overview.json`
- `deploy/compose/docker-compose.dev.yml`
- `deploy/compose/docker-compose.ec2.yml`

## Levantar observabilidad

Desde `deploy/compose`:

```bash
docker compose -f docker-compose.dev.yml up -d prometheus grafana
```

En EC2:

```bash
docker compose -f docker-compose.ec2.yml up -d prometheus grafana
```

## Endpoints

- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`
- Core API health: `http://localhost:8080/actuator/health`
- Core API Prometheus: `http://localhost:8080/actuator/prometheus`
- Checkout Service health: `http://localhost:8082/api/actuator/health`
- Checkout Service Prometheus: `http://localhost:8082/api/actuator/prometheus`

## Targets esperados en Prometheus

- `prometheus` -> `UP`
- `core-api` -> `UP`
- `checkout-service` -> `UP`

## Dashboard minimo

Dashboard provisionado:

- Nombre: `Tesis Backend Services Overview`
- UID: `services-overview`
- Ruta repo: `deploy/compose/grafana/dashboards/services-overview.json`

Paneles incluidos:

- Request rate by service
- 5xx error rate
- HTTP latency p95
- JVM heap usage
- Prometheus targets up
- Service uptime
- Process CPU usage

## PromQL base probado

Request rate por servicio:

```promql
sum by (job) (rate(http_server_requests_seconds_count{job=~"core-api|checkout-service"}[5m]))
```

Errores HTTP 5xx por servicio:

```promql
sum by (job) (rate(http_server_requests_seconds_count{job=~"core-api|checkout-service",status=~"5.."}[5m])) or (sum by (job) (rate(http_server_requests_seconds_count{job=~"core-api|checkout-service"}[5m])) * 0)
```

Latencia HTTP p95:

```promql
histogram_quantile(0.95, sum by (le, job) (rate(http_server_requests_seconds_bucket{job=~"core-api|checkout-service"}[5m])))
```

JVM heap usado:

```promql
100 * sum by (job) (jvm_memory_used_bytes{job=~"core-api|checkout-service",area="heap"}) / sum by (job) (jvm_memory_max_bytes{job=~"core-api|checkout-service",area="heap"})
```

Targets backend arriba:

```promql
up{job=~"core-api|checkout-service"}
```

Uptime por servicio:

```promql
process_uptime_seconds{job=~"core-api|checkout-service"}
```

CPU del proceso:

```promql
process_cpu_usage{job=~"core-api|checkout-service"}
```

## Nota tecnica

La latencia p95 usa `http_server_requests_seconds_bucket`; por eso los servicios habilitan histograma de requests HTTP con:

```yaml
management:
  metrics:
    distribution:
      percentiles-histogram:
        http.server.requests: true
```
