# Checklist bloque de carga/rendimiento cerrado

RUN_ID: `cap4-load-20260614-124116`

## Referencia de versionamiento

Las evidencias de carga/rendimiento quedaron versionadas en el commit:

- Commit completo: `04665551ca2f91bb47df98f2d2ee1eae8d7a98d1`
- Commit corto: `0466555`
- Fecha/hora: `2026-06-14T14:24:50-05:00`
- Rama: `cap4-cierre-tecnico`
- RUN_ID: `cap4-load-20260614-124116`

## Referencia del bloque

- Reporte principal: `evidencias/capitulo4-anexos-finales/06_carga/REPORTE_CARGA_ESCALONADA_200_500_1000.md`
- Script k6: `evidencias/capitulo4-anexos-finales/06_carga/scripts/cap4_k6_load_200_500_1000.js`
- Herramienta: `k6 v2.0.0`
- Entorno: AWS principal sandbox
- Base URL en reportes: `AWS_PRINCIPAL_HOST`

## Estado por escenario

| Escenario | Evidencia principal | Resultado observado | Estado | Observación |
|---|---|---|---|---|
| CARGA-200 | `06_carga/CARGA-200/reporte_CARGA_200.md`; `06_carga/CARGA-200/k6_summary.json`; `06_carga/CARGA-200/k6_run.log`; `06_carga/CARGA-200/health_before_after.txt`; `06_carga/CARGA-200/rabbitmq_before_after.json`; `06_carga/CARGA-200/db_counts.txt`; `06_carga/CARGA-200/logs_errors_summary.txt` | 200/200 checkouts `COMPLETED`; 2523 requests HTTP; error HTTP 0.00%; error funcional 0.00%; p95 HTTP 184.61 ms | CERRADO | 10 VUs, 106.53 s, cola crítica RabbitMQ drenada y sin DLQ |
| CARGA-500 | `06_carga/CARGA-500/reporte_CARGA_500.md`; `06_carga/CARGA-500/k6_summary.json`; `06_carga/CARGA-500/k6_run.log`; `06_carga/CARGA-500/health_before_after.txt`; `06_carga/CARGA-500/rabbitmq_before_after.json`; `06_carga/CARGA-500/db_counts.txt`; `06_carga/CARGA-500/logs_errors_summary.txt` | 500/500 checkouts `COMPLETED`; 7963 requests HTTP; error HTTP 0.00%; error funcional 0.00%; p95 HTTP 168.22 ms | CERRADO | 25 VUs, 257.18 s, cola crítica RabbitMQ drenada y sin DLQ |
| CARGA-1000 | `06_carga/CARGA-1000/reporte_CARGA_1000.md`; `06_carga/CARGA-1000/k6_summary.json`; `06_carga/CARGA-1000/k6_run.log`; `06_carga/CARGA-1000/health_before_after.txt`; `06_carga/CARGA-1000/rabbitmq_before_after.json`; `06_carga/CARGA-1000/db_counts.txt`; `06_carga/CARGA-1000/logs_errors_summary.txt` | 1000/1000 checkouts `COMPLETED`; 27042 requests HTTP; error HTTP 0.00%; error funcional 0.00%; p95 HTTP 164.46 ms | CERRADO | 50 VUs, 512.11 s, cola crítica RabbitMQ drenada y sin DLQ |

## Criterios de cierre

- HTTP error rate <= 1%: cumplido en los tres escenarios.
- Error funcional de checkout <= 5%: cumplido en los tres escenarios.
- Checkout `COMPLETED` >= 95%: cumplido en los tres escenarios.
- Cola crítica RabbitMQ sin mensajes pendientes después del drenaje: cumplido.
- Health checks de servicios antes/después: evidenciados en `health_before_after.txt`.
- Percentil p95 reportado e interpretado como métrica observada, no como SLA productivo.

## Limitaciones

- Sandbox académico; no benchmark productivo.
- No certifica SLA ni alta disponibilidad empresarial.
- Pago simulado; no se usó pasarela real ni SDK real de Stripe.
- Una ejecución controlada válida por escenario.
- Prometheus estaba detenido al inicio y se levantó el contenedor existente.
- `activeTargets: 0` queda como limitación del bloque de observabilidad, no como falla de carga.

## Estado general

Bloque de carga/rendimiento: `CERRADO`.

No se ejecutaron pruebas DRP destructivas, no se modificó arquitectura, no se implementaron pagos reales, no se generó ZIP, no se hizo commit y no se hizo push.
