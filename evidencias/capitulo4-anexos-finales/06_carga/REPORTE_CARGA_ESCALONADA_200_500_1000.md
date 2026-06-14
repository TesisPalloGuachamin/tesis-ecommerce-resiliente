# Reporte de carga/rendimiento escalonado 200/500/1000

RUN_ID: `cap4-load-20260614-124116`

## 1. Propósito de la prueba

Esta campaña corresponde a una prueba de carga funcional ampliada en entorno sandbox. Su propósito es observar la consistencia del flujo de checkout simulado bajo ejecuciones repetidas y con diferentes niveles de concurrencia.

La prueba no constituye un benchmark productivo, no certifica capacidad empresarial y no define un SLA. Los resultados se interpretan como evidencia experimental del prototipo observado en AWS principal sandbox.

## 2. Herramienta utilizada

- Herramienta: `k6 v2.0.0`.
- Tipo de ejecución: `shared-iterations`.
- Comando general usado:

```bash
k6 run --summary-export <escenario>/k6_summary.json evidencias/capitulo4-anexos-finales/06_carga/scripts/cap4_k6_load_200_500_1000.js
```

- Script usado: `evidencias/capitulo4-anexos-finales/06_carga/scripts/cap4_k6_load_200_500_1000.js`.
- Resúmenes JSON raw:
  - `evidencias/capitulo4-anexos-finales/06_carga/CARGA-200/k6_summary.json`
  - `evidencias/capitulo4-anexos-finales/06_carga/CARGA-500/k6_summary.json`
  - `evidencias/capitulo4-anexos-finales/06_carga/CARGA-1000/k6_summary.json`

Los archivos `k6_summary.json` quedan como evidencia anexa reproducible de las métricas agregadas por escenario.

## 3. Entorno de ejecución

- Entorno: AWS principal sandbox.
- Base URL enmascarada: `http://AWS_PRINCIPAL_HOST:8080`.
- RUN_ID: `cap4-load-20260614-124116`.
- Fecha/hora: registrada en `health_before_after.txt` y `k6_run.log` de cada escenario.
- Servicios involucrados directamente:
  - `core-api`
  - `checkout-service`
  - RabbitMQ
  - PostgreSQL `core_db`
  - PostgreSQL `checkout_db`
- Mobile/backend: la app móvil no participa directamente en esta prueba; se evalúan endpoints backend consumibles por el cliente.
- Pago: pago simulado, sin pasarela real, sin SDK real de Stripe, sin credenciales externas y sin transacciones financieras.

## 4. Endpoints evaluados

Los endpoints se ejecutaron sobre el host enmascarado `AWS_PRINCIPAL_HOST`:

- Autenticación/token: `POST /api/v1/auth/register`.
- Catálogo/producto: `GET /api/v1/products`.
- Carrito/preparación de compra: `POST /api/v1/cart/items`.
- Checkout asíncrono: `POST /api/v1/checkout`.
- Consulta/polling de estado final: `GET /api/v1/checkout/{requestId}` hasta estado `COMPLETED`.
- Health antes/después:
  - `GET /actuator/health` en `core-api`.
  - `GET /api/actuator/health` en `checkout-service`.
- Observabilidad complementaria:
  - RabbitMQ Management API para colas críticas.
  - Prometheus `/api/v1/targets`, con limitación indicada en la sección 10.

Los tokens, usuarios sensibles, contraseñas, secretos y host público fueron enmascarados en los reportes finales.

## 5. Diseño de escenarios

| Escenario | Usuarios concurrentes (VUs) | Operaciones objetivo | Tipo de ejecución | Rol académico |
|---|---:|---:|---|---|
| CARGA-200 | 10 | 200 checkouts simulados | `shared-iterations` | Baseline funcional |
| CARGA-500 | 25 | 500 checkouts simulados | `shared-iterations` | Carga media |
| CARGA-1000 | 50 | 1000 checkouts simulados | `shared-iterations` | Carga ampliada |

Se eligieron tres niveles para observar el comportamiento del flujo completo bajo incremento gradual de operaciones: una línea base, un punto medio y un escenario ampliado. Para el cierre documental se reporta una ejecución controlada válida por escenario, con la limitación académica de que no se trata de repetición estadística ni benchmark productivo.

## 6. Criterios de aceptación

El bloque se considera cerrado cuando se cumplen los criterios siguientes:

- HTTP error rate <= 1%.
- Error funcional de checkout <= 5%.
- Checkout `COMPLETED` >= 95%.
- Cola crítica RabbitMQ `checkout-service.checkout-requested.q` sin mensajes pendientes después del drenaje.
- Servicios `core-api` y `checkout-service` responden health antes/después.
- p95 reportado e interpretado como métrica observada del sandbox, no como SLA productivo.

## 7. Resultados por escenario

### CARGA-200

| Métrica | Valor |
|---|---:|
| VUs | 10 |
| Duración | 106.53 s |
| Iteraciones objetivo | 200 |
| Iteraciones completadas | 200 |
| Checkouts intentados | 200 |
| Checkouts `COMPLETED` | 200 |
| HTTP requests | 2523 |
| Requests/s | 23.68 |
| Checkouts/s | 1.88 |
| Error HTTP | 0.00% |
| Error funcional | 0.00% |
| Latencia HTTP promedio | 108.01 ms |
| Latencia HTTP p90 | 169.09 ms |
| Latencia HTTP p95 | 184.61 ms |
| Latencia HTTP p99 | 429.28 ms |
| Latencia HTTP máxima | 972.55 ms |
| Estado | PASS |

Evidencia asociada:

- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-200/k6_summary.json`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-200/k6_run.log`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-200/health_before_after.txt`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-200/rabbitmq_before_after.json`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-200/db_counts.txt`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-200/logs_errors_summary.txt`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-200/reporte_CARGA_200.md`

### CARGA-500

| Métrica | Valor |
|---|---:|
| VUs | 25 |
| Duración | 257.18 s |
| Iteraciones objetivo | 500 |
| Iteraciones completadas | 500 |
| Checkouts intentados | 500 |
| Checkouts `COMPLETED` | 500 |
| HTTP requests | 7963 |
| Requests/s | 30.96 |
| Checkouts/s | 1.94 |
| Error HTTP | 0.00% |
| Error funcional | 0.00% |
| Latencia HTTP promedio | 99.42 ms |
| Latencia HTTP p90 | 104.67 ms |
| Latencia HTTP p95 | 168.22 ms |
| Latencia HTTP p99 | 201.79 ms |
| Latencia HTTP máxima | 1514.49 ms |
| Estado | PASS |

Evidencia asociada:

- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-500/k6_summary.json`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-500/k6_run.log`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-500/health_before_after.txt`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-500/rabbitmq_before_after.json`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-500/db_counts.txt`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-500/logs_errors_summary.txt`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-500/reporte_CARGA_500.md`

### CARGA-1000

| Métrica | Valor |
|---|---:|
| VUs | 50 |
| Duración | 512.11 s |
| Iteraciones objetivo | 1000 |
| Iteraciones completadas | 1000 |
| Checkouts intentados | 1000 |
| Checkouts `COMPLETED` | 1000 |
| HTTP requests | 27042 |
| Requests/s | 52.80 |
| Checkouts/s | 1.95 |
| Error HTTP | 0.00% |
| Error funcional | 0.00% |
| Latencia HTTP promedio | 107.05 ms |
| Latencia HTTP p90 | 97.45 ms |
| Latencia HTTP p95 | 164.46 ms |
| Latencia HTTP p99 | 313.61 ms |
| Latencia HTTP máxima | 5203.03 ms |
| Estado | PASS |

Evidencia asociada:

- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-1000/k6_summary.json`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-1000/k6_run.log`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-1000/health_before_after.txt`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-1000/rabbitmq_before_after.json`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-1000/db_counts.txt`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-1000/logs_errors_summary.txt`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-1000/reporte_CARGA_1000.md`

## 8. Comparación 200 vs 500 vs 1000

| Escenario | VUs | Operaciones objetivo | Checkouts intentados | Checkouts COMPLETED | HTTP requests | Requests/s | Checkouts/s | Error HTTP | Error funcional | p95 HTTP | Duración | Estado |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---|
| CARGA-200 | 10 | 200 | 200 | 200 | 2523 | 23.68 | 1.88 | 0.00% | 0.00% | 184.61 ms | 106.53 s | PASS |
| CARGA-500 | 25 | 500 | 500 | 500 | 7963 | 30.96 | 1.94 | 0.00% | 0.00% | 168.22 ms | 257.18 s | PASS |
| CARGA-1000 | 50 | 1000 | 1000 | 1000 | 27042 | 52.80 | 1.95 | 0.00% | 0.00% | 164.46 ms | 512.11 s | PASS |

En esta campaña observada no se evidenció degradación del p95 bajo los tres niveles evaluados; sin embargo, este resultado se limita al entorno, datos y ventana de prueba. No debe interpretarse como que a mayor carga mejora el rendimiento. El comportamiento puede estar influenciado por calentamiento del entorno, caching, comportamiento del script, polling, estado del consumidor y condiciones propias del sandbox.

Los tres escenarios mantuvieron 0.00% de error HTTP y 0.00% de error funcional. Todos los checkouts intentados finalizaron `COMPLETED`, y la cola crítica RabbitMQ quedó en 0 después del drenaje. La duración total creció con el volumen de operaciones, de 106.53 s en CARGA-200 a 512.11 s en CARGA-1000.

## 9. Evidencia asociada

Por escenario se conserva:

- `k6_summary.json`: resumen raw JSON de k6.
- `k6_run.log`: salida textual de ejecución k6.
- `health_before_after.txt`: health checks antes/después.
- `rabbitmq_before_after.json`: estado de colas RabbitMQ antes/después y drenaje.
- `db_counts.txt`: conteos de base de datos antes/después.
- `logs_errors_summary.txt`: extracto acotado de logs de error/warning.
- `reporte_CARGA_*.md`: reporte individual del escenario.

Reporte comparativo:

- `evidencias/capitulo4-anexos-finales/06_carga/REPORTE_CARGA_ESCALONADA_200_500_1000.md`

Checklist:

- `evidencias/capitulo4-anexos-finales/11_reporte_final/CHECKLIST_CARGA_CERRADA.md`

## 10. Limitaciones

- Entorno sandbox académico AWS Academy.
- No es benchmark productivo.
- No constituye SLA.
- No debe interpretarse como alta disponibilidad empresarial.
- Pago simulado, sin pasarela real y sin transacciones financieras.
- Una ejecución controlada válida por escenario, con limitación académica.
- Prometheus estaba detenido al inicio y se levantó el contenedor existente para capturar targets.
- Prometheus reportó `activeTargets: 0`; esto queda como limitación del bloque de observabilidad, no como falla del bloque de carga.
- El flujo de checkout es asíncrono; se usó polling para validar estado final `COMPLETED`.

## 11. Conclusión del bloque

Bloque de carga/rendimiento: CERRADO.

Los escenarios CARGA-200, CARGA-500 y CARGA-1000 cumplen los criterios definidos: error HTTP <= 1%, error funcional <= 5%, checkout `COMPLETED` >= 95%, health disponible antes/después y cola crítica RabbitMQ drenada.
