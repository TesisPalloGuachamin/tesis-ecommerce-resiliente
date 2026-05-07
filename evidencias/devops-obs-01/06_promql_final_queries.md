# PromQL — Set Final Consolidado  
## DEVOPS-OBS-01 | Tesis e-commerce sandbox

---

## Categoría 1: OBSERVADAS (ejecutables con Prometheus self-monitoring activo)

Estas consultas son ejecutables en `http://localhost:9090` cuando Prometheus está levantado con la configuración actual (solo self-monitoring). Producen datos reales del propio proceso Prometheus.

### 1.1 Disponibilidad de targets monitoreados
```promql
up
```
- **Qué mide**: 1 = target alcanzable, 0 = caído
- **En el entorno**: retorna `up{job="prometheus", instance="prometheus:9090"} = 1`
- **Uso en tesis**: Acredita que Prometheus opera y scraped sus propias métricas

### 1.2 Tasa de muestras ingresadas
```promql
rate(prometheus_tsdb_head_samples_appended_total[5m])
```
- **Qué mide**: Muestras por segundo que Prometheus está registrando
- **Uso en tesis**: Muestra que el sistema de series temporales está activo

### 1.3 Tamaño del TSDB (time-series database)
```promql
prometheus_tsdb_head_chunks
```
- **Qué mide**: Número de chunks activos en la cabeza del TSDB

---

## Categoría 2: APOYO ANALÍTICO (ejecutables si micrometer-registry-prometheus estuviera habilitado)

Estas consultas son **diseñadas** para el entorno real una vez que se agregue `micrometer-registry-prometheus` a los `pom.xml`. No producen datos en la configuración actual.

### 2.1 Tasa de requests HTTP al core-api
```promql
rate(http_server_requests_seconds_count{job="core-api"}[1m])
```
- **Qué mediría**: Requests por segundo por endpoint y método
- **Filtro útil**: `{uri="/api/v1/listings", method="POST"}`

### 2.2 Latencia P99 del core-api
```promql
histogram_quantile(0.99,
  rate(http_server_requests_seconds_bucket{job="core-api"}[5m])
)
```
- **Qué mediría**: Tiempo de respuesta en percentil 99

### 2.3 Tasa de requests por status HTTP
```promql
rate(http_server_requests_seconds_count{job="core-api", status="201"}[1m])
```
- **Filtros de interés**: `status="200"`, `status="201"`, `status="403"`, `status="500"`

### 2.4 Disponibilidad de servicios Java
```promql
up{job=~"core-api|checkout-service"}
```
- **Qué mediría**: 1 = servicio alcanzable por Prometheus, 0 = caído

### 2.5 Memoria JVM en uso
```promql
jvm_memory_used_bytes{job="core-api", area="heap"}
```
- **Qué mediría**: Bytes de heap usados por la JVM del core-api

### 2.6 Pool de conexiones DB activas
```promql
hikaricp_connections_active{job="core-api"}
```
- **Qué mediría**: Conexiones de base de datos en uso activo

### 2.7 Tasa de errores checkout-service
```promql
rate(http_server_requests_seconds_count{job="checkout-service", status=~"5.."}[1m])
```

### 2.8 Duración media de procesamiento de mensajes RabbitMQ
```promql
rate(spring_rabbitmq_listener_seconds_sum{job="checkout-service"}[5m])
/ rate(spring_rabbitmq_listener_seconds_count{job="checkout-service"}[5m])
```
- **Qué mediría**: Tiempo promedio de procesamiento de eventos checkout

### 2.9 Eventos de log por nivel (logback)
```promql
increase(logback_events_total{job="core-api", level="ERROR"}[10m])
```
- **Qué mediría**: Errores logueados en los últimos 10 minutos

### 2.10 Uptime de los servicios Java
```promql
(time() - process_start_time_seconds{job=~"core-api|checkout-service"}) / 3600
```
- **Qué mediría**: Horas de uptime continuo

---

## Resumen de clasificación

| Consulta | Clasificación | Estado en entorno actual |
|---|---|---|
| `up` | Observada | Ejecutable (Prometheus self) |
| `rate(prometheus_tsdb_head_samples_appended_total[5m])` | Observada | Ejecutable (Prometheus self) |
| `prometheus_tsdb_head_chunks` | Observada | Ejecutable (Prometheus self) |
| `rate(http_server_requests_seconds_count[1m])` | Apoyo analítico | Requiere micrometer-prometheus |
| `histogram_quantile(0.99, ...)` | Apoyo analítico | Requiere micrometer-prometheus |
| `up{job=~"core-api|checkout-service"}` | Apoyo analítico | Requiere micrometer-prometheus |
| `jvm_memory_used_bytes` | Apoyo analítico | Requiere micrometer-prometheus |
| `hikaricp_connections_active` | Apoyo analítico | Requiere micrometer-prometheus |
| `spring_rabbitmq_listener_seconds` | Apoyo analítico | Requiere micrometer-prometheus |
| `logback_events_total` | Apoyo analítico | Requiere micrometer-prometheus |

---

## Nota para Capítulo 4

En la tesis, las consultas de Categoría 1 deben presentarse como **ejecutadas y verificadas**.  
Las de Categoría 2 deben presentarse como **diseñadas para el entorno extendido**, con la  
aclaración explícita de que requieren la dependencia `micrometer-registry-prometheus`  
y no fueron observadas en el entorno sandbox de esta investigación.

No afirmar métricas de latencia ni request rate como observadas si no hay datos de scraping real.
