# Tabla de metricas consolidadas Capitulo 4

| Metrica | Fuente | Valor observado | Criterio | Interpretacion | Limitacion |
|---|---|---|---|---|---|
| Checkouts completados funcionales | `01_funcionales_compra/reporte_compra_COMPLETED_100.md`; `04_rabbitmq/reporte_rabbitmq_100.md` | Checkout funcional trazable `COMPLETED` | Debe existir request/response/logs/DB | Flujo de compra y mensajeria completado | Evidencia puntual funcional, no carga |
| Listings activos funcionales | `02_funcionales_venta/reporte_venta_ACTIVE_100.md` | Listing trazable `ACTIVE` | Creacion HTTP 201 y consulta HTTP 200 | Flujo de publicacion/venta validado | Evidencia puntual funcional |
| Pruebas backend iniciales | `05_pruebas_backend/reporte_pruebas_backend_100.md` | `core-api` 11 PASS; `checkout-service` 4 PASS | Build Maven PASS | Pruebas unitarias/backend reproducibles | No mide cobertura hasta evidencia JaCoCo posterior |
| Cobertura JaCoCo `core-api` | `05_pruebas_backend/REPORTE_COBERTURA_JACOCO_BACKEND.md` | 29 PASS; lineas 50.76%; instrucciones 46.67% | Meta orientativa lineas >= 35% | Cobertura academica reforzada | Sin enforcement de umbral; no certifica calidad productiva |
| Cobertura JaCoCo `checkout-service` | `05_pruebas_backend/REPORTE_COBERTURA_JACOCO_BACKEND.md` | 13 PASS; lineas 54.19%; instrucciones 29.63% | Meta orientativa lineas >= 50% | Cobertura academica reforzada | Sin enforcement de umbral; no mide pruebas externas |
| CARGA-200 | `06_carga/REPORTE_CARGA_ESCALONADA_200_500_1000.md` | 200/200 checkouts `COMPLETED`; 2523 requests; p95 184.61 ms | Error HTTP <= 1%; error funcional <= 5%; completados >= 95% | PASS en baseline funcional | Una ejecucion controlada en sandbox |
| CARGA-500 | `06_carga/REPORTE_CARGA_ESCALONADA_200_500_1000.md` | 500/500 checkouts `COMPLETED`; 7963 requests; p95 168.22 ms | Error HTTP <= 1%; error funcional <= 5%; completados >= 95% | PASS en carga media | Una ejecucion controlada en sandbox |
| CARGA-1000 | `06_carga/REPORTE_CARGA_ESCALONADA_200_500_1000.md` | 1000/1000 checkouts `COMPLETED`; 27042 requests; p95 164.46 ms | Error HTTP <= 1%; error funcional <= 5%; completados >= 95% | PASS en carga funcional ampliada | No es benchmark productivo ni metrica DRP |
| p95 carga 1000 | `06_carga/CARGA-1000/k6_summary.json` | 164.46 ms | Reportar e interpretar | Latencia observada bajo carga funcional ampliada | No se usa como SLA |
| Error HTTP carga | `06_carga/REPORTE_CARGA_ESCALONADA_200_500_1000.md` | 0.00% en CARGA-200/500/1000 | <= 1% | Sin errores HTTP observados en las tres ventanas | No extrapolar a disponibilidad mensual/anual |
| Error funcional carga | `06_carga/REPORTE_CARGA_ESCALONADA_200_500_1000.md` | 0.00% en CARGA-200/500/1000 | <= 5% | Todos los checkouts finalizaron `COMPLETED` | Pago simulado |
| Targets Prometheus UP | `07_observabilidad/REPORTE_OBSERVABILIDAD_PROMETHEUS_GRAFANA.md`; `prometheus_targets_after.json` | 3 targets UP: `core-api`, `checkout-service`, `prometheus` | Prometheus responde y targets activos | Observabilidad tecnica operativa | No es monitoreo productivo empresarial |
| PromQL `up` | `07_observabilidad/prometheus_query_up.json` | 3 series; core/checkout 2 series con valor 1 | Query PromQL devuelve resultados | Servicios observables por Prometheus | Ventana sandbox |
| RTO AWS principal | `08_drp/matriz_drp_final.md`; `aws_principal_retest/metricas_calculadas_aws_principal.json` | 31.506 s | `RTO = t_servicio_restaurado - t_inicio_incidente` | Recuperacion controlada del servicio critico | No es failover automatico |
| MTTR AWS principal | `08_drp/matriz_drp_final.md`; `aws_principal_retest/metricas_calculadas_aws_principal.json` | 69.801 s | `MTTR = t_fin_recuperacion - t_inicio_incidente` | Tiempo total hasta recuperacion y smoke posterior | No es MTTR empresarial |
| RPO temporal AWS principal | `08_drp/matriz_drp_final.md`; `aws_principal_retest/timestamps_aws_principal.json` | 0.000 s | `RPO_temporal = t_inicio_incidente - t_ultimo_respaldo_valido` | No se destruyeron datos; punto recuperable inmediato | No equivale a restauracion desde backup antiguo |
| RPO funcional AWS principal | `08_drp/matriz_drp_final.md`; `smoke_before_after.json`; `db_integrity_check.txt` | 0 checkoutIds faltantes | `operaciones_esperadas - operaciones_recuperadas` | Sin perdida funcional en smoke DRP | Alcance acotado a IDs trazados |
| Disponibilidad DRP ventana controlada | `08_drp/matriz_drp_final.md`; `metricas_calculadas_aws_principal.json` | 64.08527196797435% | `((T_total - T_indisponibilidad) / T_total) * 100` | Disponibilidad solo de la ventana DRP con caida inducida | No SLA mensual/anual |
| Error funcional DRP AWS principal | `08_drp/matriz_drp_final.md` | 0.000000% | `(errores_funcionales / operaciones_funcionales) * 100` | Smoke pre/post sin fallo funcional | No incluye el health fallido esperado del incidente |
| p95 DRP AWS principal | `08_drp/matriz_drp_final.md` | 0.6045430860984193 s | Percentil 95 de muestras HTTP exitosas | Latencia del retest DRP | Muestra acotada |
| RTO AWS alterna | `08_drp/matriz_drp_final.md`; `drp-aws-alt-metrics.json` | 452 s | Formula RTO | Recuperacion alterna historica observada | No se repitio en campana final; no active-active |
| MTTR AWS alterna | `08_drp/matriz_drp_final.md`; `drp-aws-alt-metrics.json` | 452 s | Formula MTTR | Restablecimiento historico observado | Limitaciones de escenario alterno |
| RPO AWS alterna | `08_drp/matriz_drp_final.md`; `drp-aws-alt-metrics.json` | RPO temporal 2009 s; RPO funcional 0 | Formulas RPO temporal y funcional | Sin perdida funcional para IDs trazados | No representa replicacion continua |
| RTO Azure | `08_drp/matriz_drp_final.md`; `drp-azure-metrics.json` | RTO efectivo 534.517 s | Formula RTO efectiva | Recuperacion minima observada | Alcance minimo con restricciones |
| MTTR Azure | `08_drp/matriz_drp_final.md`; `drp-azure-metrics.json` | MTTR efectivo 534.517 s | Formula MTTR efectiva | Restablecimiento minimo observado | No entorno activo continuo |
| RPO Azure | `08_drp/matriz_drp_final.md`; `drp-azure-metrics.json` | RPO temporal 4911 s; RPO funcional 0 | Formulas RPO temporal y funcional | Sin perdida funcional observada en alcance minimo | No comparable como par productivo |

## Lectura correcta de las metricas

- Las metricas de carga son operativas y complementarias; no calculan RTO, RPO ni MTTR.
- Las metricas DRP pertenecen a ventanas de recuperacion controladas.
- La disponibilidad reportada nunca debe extrapolarse a SLA productivo.
- GCP y on-premise no tienen metricas operativas en este proyecto.
