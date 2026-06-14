# Resumen para tesis - Bloque de carga/rendimiento

RUN_ID: `cap4-load-20260614-124116`

La campaña de carga/rendimiento se ejecutó con `k6` sobre el flujo backend de checkout simulado en AWS principal sandbox. La prueba evaluó autenticación, consulta de catálogo, preparación de carrito, creación de checkout y polling de estado hasta `COMPLETED`.

| Escenario | VUs | Operaciones objetivo | Checkouts `COMPLETED` | Requests/s | Checkouts/s | Error HTTP | Error funcional | p95 HTTP | Estado |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---|
| CARGA-200 | 10 | 200 | 200 | 23.68 | 1.88 | 0.00% | 0.00% | 184.61 ms | PASS |
| CARGA-500 | 25 | 500 | 500 | 30.96 | 1.94 | 0.00% | 0.00% | 168.22 ms | PASS |
| CARGA-1000 | 50 | 1000 | 1000 | 52.80 | 1.95 | 0.00% | 0.00% | 164.46 ms | PASS |

En esta campaña observada no se evidenció degradación del p95 bajo los tres niveles evaluados; sin embargo, este resultado se limita al entorno, datos y ventana de prueba. Los percentiles deben presentarse como mediciones experimentales del prototipo y no como SLA productivo.

Bloque de carga/rendimiento: `CERRADO`.
