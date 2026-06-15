# Reporte CARGA-500 - Prueba de carga funcional

- RUN_ID: `cap4-load-20260614-124116`
- Herramienta: `k6 v2.0.0`
- Fecha/hora: ver `health_before_after.txt` y `k6_run.log`
- Entorno: AWS principal observado
- Base URL: `http://AWS_PRINCIPAL_HOST:8080`
- VUs configurados: `25`
- Iteraciones/checkouts objetivo: `500`
- Tipo de ejecucion: `shared-iterations`

## Endpoints evaluados

- `POST /api/v1/auth/register`
- `GET /api/v1/products`
- `POST /api/v1/cart/items`
- `POST /api/v1/checkout`
- `GET /api/v1/checkout/{requestId}`

## Metricas observadas

| Metrica | Valor |
|---|---:|
| Iteraciones configuradas | 500 |
| Iteraciones completadas | 500 |
| Checkouts intentados | 500 |
| Checkouts `COMPLETED` | 500 |
| Requests HTTP | 7963 |
| Tasa requests/s | 30.96 |
| Error HTTP | 0.00% |
| Error funcional | 0.00% |
| Latencia HTTP promedio | 99.42 ms |
| Latencia HTTP p90 | 104.67 ms |
| Latencia HTTP p95 | 168.22 ms |
| Latencia HTTP p99 | 201.79 ms |
| Latencia HTTP maxima | 1514.49 ms |
| Latencia funcional checkout p95 | 13656.00 ms |
| Duracion calculada por k6 | 257.18 s |
| Estado | PASS |

## Criterios de aceptacion

- HTTP error rate <= 1%: observado 0.00%.
- Checkout COMPLETED >= 95%: observado 500/500 (100.00%).
- Cola critica RabbitMQ sin mensajes pendientes despues del drenaje: si.
- p95 reportado e interpretado como observacion tecnica del sandbox, no como SLA productivo.

## Evidencia asociada

- `k6_summary.json`
- `k6_run.log`
- `health_before_after.txt`
- `rabbitmq_before_after.json`
- `db_counts.txt`
- `logs_errors_summary.txt`

## Interpretacion tecnica

El escenario CARGA-500 completo 500 de 500 checkouts simulados con estado final `COMPLETED`, sin errores HTTP observados. El p95 HTTP fue 168.22 ms; este dato se reporta como observacion de rendimiento del sandbox y no como SLA productivo. La latencia funcional p95 del checkout fue 13656.00 ms por el procesamiento asincrono mediante RabbitMQ.

## Limitaciones

- Entorno AWS Academy/sandbox observado; los resultados no representan benchmark productivo ni SLA.
- El pago es simulado y no usa SDK real, credenciales ni transacciones financieras externas.
- El checkout es asincrono; por ello se amplio la ventana de polling para validar estado final COMPLETED sin modificar arquitectura.

## Resultado

Estado del escenario: `PASS`.
