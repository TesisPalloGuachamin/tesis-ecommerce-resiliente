# Reporte CARGA-1000 - Prueba de carga funcional

- RUN_ID: `cap4-load-20260614-124116`
- Herramienta: `k6 v2.0.0`
- Fecha/hora: ver `health_before_after.txt` y `k6_run.log`
- Entorno: AWS principal observado
- Base URL: `http://AWS_PRINCIPAL_HOST:8080`
- VUs configurados: `50`
- Iteraciones/checkouts objetivo: `1000`
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
| Iteraciones configuradas | 1000 |
| Iteraciones completadas | 1000 |
| Checkouts intentados | 1000 |
| Checkouts `COMPLETED` | 1000 |
| Requests HTTP | 27042 |
| Tasa requests/s | 52.81 |
| Error HTTP | 0.00% |
| Error funcional | 0.00% |
| Latencia HTTP promedio | 107.05 ms |
| Latencia HTTP p90 | 97.45 ms |
| Latencia HTTP p95 | 164.46 ms |
| Latencia HTTP p99 | 313.61 ms |
| Latencia HTTP maxima | 5203.03 ms |
| Latencia funcional checkout p95 | 25894.35 ms |
| Duracion calculada por k6 | 512.11 s |
| Estado | PASS |

## Criterios de aceptacion

- HTTP error rate <= 1%: observado 0.00%.
- Checkout COMPLETED >= 95%: observado 1000/1000 (100.00%).
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

El escenario CARGA-1000 completo 1000 de 1000 checkouts simulados con estado final `COMPLETED`, sin errores HTTP observados. El p95 HTTP fue 164.46 ms; este dato se reporta como observacion de rendimiento del sandbox y no como SLA productivo. La latencia funcional p95 del checkout fue 25894.35 ms por el procesamiento asincrono mediante RabbitMQ.

## Limitaciones

- Entorno AWS Academy/sandbox observado; los resultados no representan benchmark productivo ni SLA.
- El pago es simulado y no usa SDK real, credenciales ni transacciones financieras externas.
- El checkout es asincrono; por ello se amplio la ventana de polling para validar estado final COMPLETED sin modificar arquitectura.

## Resultado

Estado del escenario: `PASS`.
