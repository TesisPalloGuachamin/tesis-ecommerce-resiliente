# Tabla maestra de trazabilidad Capitulo 4

Esta tabla conecta metodologia, componente implementado, evidencia, resultado observado, metrica, objetivo especifico y commit de respaldo. Los resultados se interpretan dentro del alcance de prototipo academico en sandbox.

| Codigo | Seccion sugerida Cap. 4 | Elemento validado | Evidencia | Resultado | Metrica asociada | Objetivo especifico | Commit |
|---|---|---|---|---|---|---|---|
| CORE4-COMPRA | 4.2 Validacion funcional de compra | Compra hasta `COMPLETED` | `01_funcionales_compra/reporte_compra_COMPLETED_100.md`; `compra_COMPLETED_100_flow.json`; DB y logs asociados | Checkout trazable queda `COMPLETED` | `1/1` flujo funcional cerrado; DB/logs trazables | OE2/OE3 | Tecnico `228f160`; documental `bd521df` |
| CORE4-VENTA | 4.2 Validacion funcional de venta | Listing hasta `ACTIVE` | `02_funcionales_venta/reporte_venta_ACTIVE_100.md`; `venta_ACTIVE_100_flow.json`; DB y logs asociados | `POST /api/v1/listings` 201; `GET /api/v1/listings/{id}` 200; listing `ACTIVE` | `1/1` listing activo | OE2/OE3 | Tecnico `228f160`; documental `bd521df` |
| CORE4-TESTS | 4.3 Pruebas backend | Pruebas unitarias/backend iniciales | `05_pruebas_backend/reporte_pruebas_backend_100.md`; logs Maven; Surefire | `core-api`: 11 PASS; `checkout-service`: 4 PASS | 15 pruebas PASS | OE3 | Tecnico `228f160`; documental `bd521df` |
| CORE4-RABBITMQ | 4.3 Integracion asincrona | Publicacion, consumo, colas, logs y persistencia RabbitMQ | `04_rabbitmq/reporte_rabbitmq_100.md`; `rabbitmq_queues_100.json`; `db_trace_100_raw.txt`; logs | Evento de checkout publicado y consumido; DB confirma `COMPLETED`; cola critica sin backlog | Trazabilidad por request/checkoutId; cola critica en 0 | OE2/OE3 | Tecnico `228f160`; documental `bd521df` |
| LOAD-200 | 4.4 Carga/rendimiento | CARGA-200 | `06_carga/CARGA-200/`; `REPORTE_CARGA_ESCALONADA_200_500_1000.md` | 200/200 checkouts `COMPLETED`; 0.00% error HTTP y funcional | 10 VUs; 2523 requests; p95 184.61 ms; 106.53 s | OE3/OE4 | Tecnico `0466555`; documental `00a195f` |
| LOAD-500 | 4.4 Carga/rendimiento | CARGA-500 | `06_carga/CARGA-500/`; `REPORTE_CARGA_ESCALONADA_200_500_1000.md` | 500/500 checkouts `COMPLETED`; 0.00% error HTTP y funcional | 25 VUs; 7963 requests; p95 168.22 ms; 257.18 s | OE3/OE4 | Tecnico `0466555`; documental `00a195f` |
| LOAD-1000 | 4.4 Carga/rendimiento | CARGA-1000 | `06_carga/CARGA-1000/`; `REPORTE_CARGA_ESCALONADA_200_500_1000.md` | 1000/1000 checkouts `COMPLETED`; 0.00% error HTTP y funcional | 50 VUs; 27042 requests; p95 164.46 ms; 512.11 s | OE3/OE4 | Tecnico `0466555`; documental `00a195f` |
| OBS-01 | 4.5 Observabilidad | Prometheus/Grafana | `07_observabilidad/REPORTE_OBSERVABILIDAD_PROMETHEUS_GRAFANA.md`; targets, PromQL, Actuator, Grafana API | Prometheus HTTP 200; 3 targets `UP`; Grafana health HTTP 200 | `core-api`, `checkout-service`, `prometheus` UP; PromQL `up` 3 series | OE3/OE4 | Tecnico `4198dac`; documental `703b82a` |
| JACOCO-01 | 4.3 Pruebas backend complementarias | Cobertura JaCoCo backend | `05_pruebas_backend/REPORTE_COBERTURA_JACOCO_BACKEND.md`; `cobertura_jacoco/` | Pruebas PASS con cobertura reforzada | `core-api`: 29 PASS, lineas 50.76%; `checkout-service`: 13 PASS, lineas 54.19% | OE3 | Tecnico `2bdbead` |
| MOBILE-01 | 4.6 Integracion mobile-backend | Endpoints consumidos desde app y validacion funcional cliente movil/web | `03_mobile_backend/REPORTE_MOBILE_BACKEND_100.md`; inventario, flujo, errores, logs | Login/productos/carrito/checkout/listing validados; checkout `COMPLETED`; listing `ACTIVE` | TypeScript PASS; Expo Doctor 16/18; flujo HTTP equivalente PASS | OE2/OE3 | Tecnico `40bf682`; documental `c33bd0c` |
| DRP-AWS-MAIN | 4.7 DRP y metricas | AWS principal observado DRP | `08_drp/aws_principal_retest/`; `REPORTE_DRP_METRICAS_100.md`; `matriz_drp_final.md` | Recuperacion controlada del servicio critico con verificacion externa/interna PASS | RTO 31.506 s; MTTR 69.801 s; RPO temporal 0.000 s; RPO funcional 0; disponibilidad ventana DRP 64.08527196797435% | OE4 | Tecnico `6bbb6f3`; documental `3471c01` |
| DRP-AWS-ALT | 4.7 DRP y metricas | AWS region alterna observada con limitaciones | `08_drp/matriz_drp_final.md`; fuente historica `drp-aws-alt-metrics.json` | Recuperacion alterna historica observada, sin active-active | RTO 452 s; MTTR 452 s; RPO temporal 2009 s; RPO funcional 0 | OE4 | Tecnico `6bbb6f3`; documental `3471c01` |
| DRP-AZURE | 4.7 DRP y metricas | Azure observado con alcance minimo y limitaciones | `08_drp/matriz_drp_final.md`; fuente historica `drp-azure-metrics.json` | Recuperacion minima observada, no equivalente a AWS principal | RTO efectivo 534.517 s; MTTR efectivo 534.517 s; RPO temporal 4911 s; RPO funcional 0 | OE4 | Tecnico `6bbb6f3`; documental `3471c01` |
| DRP-DOC | 4.7 DRP y metricas | GCP y on-premise documentales | `08_drp/clasificacion_escenarios_drp.md`; `REPORTE_DRP_METRICAS_100.md` | Escenarios no ejecutados, usados solo como analisis documental | No aplica metrica operativa | OE4 | Tecnico `6bbb6f3`; documental `3471c01` |

## Restricciones de interpretacion

- No presentar SLA productivo, disponibilidad mensual/anual ni garantia empresarial.
- No presentar active-active ni alta disponibilidad productiva.
- No presentar failover automatico movil.
- No presentar pagos reales ni integracion financiera real.
- No presentar GCP/on-premise como pruebas operativas ejecutadas.
