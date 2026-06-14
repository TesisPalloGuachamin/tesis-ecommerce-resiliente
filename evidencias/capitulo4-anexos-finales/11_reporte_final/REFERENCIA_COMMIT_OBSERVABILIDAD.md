# Referencia de commit — Bloque de observabilidad Capítulo 4

## Identificación

- RUN_ID: `cap4-obs-20260614-152854`
- Commit completo: `4198dac811bedd2059a38d796943c5b9227b8fcc`
- Commit corto: `4198dac`
- Fecha/hora del commit: `2026-06-14T18:03:37-05:00`
- Rama: `cap4-cierre-tecnico`
- Autor/committer: `ChristopherPalloArias <christopherpallo2000@gmail.com>`

## Alcance del commit

Este commit versiona exclusivamente el bloque de observabilidad Prometheus/Grafana del Capítulo 4:

1. Diagnóstico de observabilidad.
2. Prometheus targets.
3. PromQL.
4. Actuator metrics.
5. Grafana health/API.
6. Datasource Prometheus.
7. Logs de observabilidad.
8. Reporte principal.
9. Checklist de observabilidad cerrada.
10. Capturas visuales complementarias.

## Evidencia principal asociada

- `evidencias/capitulo4-anexos-finales/07_observabilidad/`
- `evidencias/capitulo4-anexos-finales/07_observabilidad/REPORTE_OBSERVABILIDAD_PROMETHEUS_GRAFANA.md`
- `evidencias/capitulo4-anexos-finales/07_observabilidad/diagnostico_observabilidad.md`
- `evidencias/capitulo4-anexos-finales/07_observabilidad/prometheus_targets_after.json`
- `evidencias/capitulo4-anexos-finales/07_observabilidad/prometheus_query_up.json`
- `evidencias/capitulo4-anexos-finales/07_observabilidad/actuator_core_api_metrics.txt`
- `evidencias/capitulo4-anexos-finales/07_observabilidad/actuator_checkout_service_metrics.txt`
- `evidencias/capitulo4-anexos-finales/07_observabilidad/grafana_health.txt`
- `evidencias/capitulo4-anexos-finales/07_observabilidad/capturas/`
- `evidencias/capitulo4-anexos-finales/11_reporte_final/CHECKLIST_OBSERVABILIDAD_CERRADA.md`

## Resultado observado

- Prometheus: HTTP 200.
- Targets activos: 3.
- Targets `UP`: `core-api`, `checkout-service`, `prometheus`.
- `core-api /actuator/prometheus`: HTTP 200.
- `checkout-service /api/actuator/prometheus`: HTTP 200.
- PromQL `up`: 3 series.
- PromQL `up{job=~"core-api|checkout-service"}`: 2 series con valor `1`.
- Grafana `/api/health`: HTTP 200.
- Datasource Prometheus: provisionado hacia `http://prometheus:9090`.

## Exclusiones

Este commit no incluye:
- compra/venta/backend/RabbitMQ;
- carga/rendimiento;
- mobile-backend;
- DRP;
- XP;
- diagramas;
- ZIPs;
- `_diagnostico_raw`;
- `capitulo4-cierre-tecnico/`.

## Nota metodológica

El commit constituye una referencia de trazabilidad para el cierre técnico del bloque de observabilidad. La observabilidad se interpreta como soporte de validación operativa en entorno sandbox, no como monitoreo productivo empresarial, certificación de SLA ni garantía de alta disponibilidad.
