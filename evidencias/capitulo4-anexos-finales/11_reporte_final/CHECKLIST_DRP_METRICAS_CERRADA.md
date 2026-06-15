# Checklist DRP y metricas cerrado

RUN_ID: `cap4-drp-20260614-185235`

## Referencia de versionamiento

Las evidencias DRP/metricas quedaron versionadas en el commit:

- Commit completo: `6bbb6f3c26c47470160fcebd0484e6d63a862dcc`
- Commit corto: `6bbb6f3`
- Fecha/hora: `2026-06-14T22:01:08-05:00`
- Rama: `cap4-cierre-tecnico`
- RUN_ID principal: `cap4-drp-20260614-185235`
- RUN_ID_AWS_PRINCIPAL: `cap4-drp-aws-main-20260614-191520`

| Elemento | Estado | Evidencia | Observacion |
|---|---|---|---|
| Clasificacion de escenarios | CERRADO | `08_drp/clasificacion_escenarios_drp.md` | Diferencia AWS principal DRP, CARGA-1000 complementaria, AWS alterna, Azure, GCP y on-premise. |
| Definiciones de metricas | CERRADO | `08_drp/metricas_drp_definiciones_formulas.md` | Incluye RTO, RPO temporal, RPO funcional, MTTR, disponibilidad, error rate y latencia p95. |
| Formulas | CERRADO | `08_drp/metricas_drp_definiciones_formulas.md`, `08_drp/matriz_drp_final.md` | Cada metrica tiene formula e interpretacion. |
| Fuentes de datos | CERRADO | `08_drp/auditoria_evidencia_drp.md`, `08_drp/aws_principal_retest/`, `08_drp/matriz_drp_final.md` | AWS principal usa retest versionable; AWS alterna, Azure y CARGA-1000 tienen fuente local. |
| AWS principal DRP separado de carga | CERRADO | `08_drp/aws_principal_retest/metricas_calculadas_aws_principal.json`, `08_drp/matriz_drp_final.md`, `08_drp/REPORTE_DRP_METRICAS_100.md` | RTO 31.506 s, MTTR 69.801 s, RPO temporal 0.000 s, RPO funcional 0, disponibilidad 64.085272%, error 0.000000%, p95 0.604543 s. |
| Verificacion final nuevo laboratorio | CERRADO | `08_drp/aws_principal_retest/verificacion_nuevo_laboratorio_sanitizada.txt`, `verificacion_final_externa_post_drp.txt`, `smoke_final_externo_post_drp.json`, `verificacion_interna_post_drp.txt` | Health externo, login, productos, checkout `COMPLETED`, listing `ACTIVE`, Prometheus y Grafana validados sin repetir prueba destructiva. |
| Correccion operativa minima | CERRADO | `08_drp/aws_principal_retest/verificacion_interna_post_drp.txt`, `08_drp/REPORTE_DRP_METRICAS_100.md` | Se levantaron solo contenedores existentes `tesis-prometheus` y `tesis-grafana`, detenidos por reinicio/cambio de laboratorio. |
| CARGA-1000 como complemento operativo | CERRADO | `06_carga/CARGA-1000/k6_summary.json`, `08_drp/matriz_drp_final.md` | 1000/1000 checkouts `COMPLETED`, 0% error HTTP, 0% error funcional, p95 164.46 ms. No calcula RTO/RPO/MTTR. |
| AWS alterna | CERRADO | `evidencias/drp-aws-alt-01/drp-aws-alt-20260518T231958Z/drp-aws-alt-metrics.json` | Observado con limitaciones; RTO 452 s, MTTR 452 s, RPO temporal 2009 s, RPO funcional 0. |
| Azure | CERRADO | `evidencias/drp-azure-01/drp-azure-20260518T233644Z/drp-azure-metrics.json` | Observado con alcance minimo; RTO efectivo 534.517 s, MTTR efectivo 534.517 s, RPO temporal 4911 s, RPO funcional 0. |
| GCP documental | CERRADO | `08_drp/clasificacion_escenarios_drp.md` | No presentado como prueba operativa. |
| On-premise documental | CERRADO | `08_drp/clasificacion_escenarios_drp.md` | No presentado como prueba operativa. |
| Disponibilidad no-SLA | CERRADO | `08_drp/matriz_drp_final.md`, `08_drp/REPORTE_DRP_METRICAS_100.md` | 64.085272% corresponde a ventana DRP retest; 100% corresponde a ventana CARGA-1000. Ninguna es SLA. |
| RPO temporal vs funcional | CERRADO | `08_drp/metricas_drp_definiciones_formulas.md`, `08_drp/matriz_drp_final.md` | Temporal asociado a punto recuperable/tiempo; funcional asociado a checkoutIds u operaciones trazables. |
| Numero de ejecuciones | CERRADO | `08_drp/REPORTE_DRP_METRICAS_100.md` | AWS principal: 1 retest controlado; CARGA-1000: 1 ejecucion complementaria; AWS alterna/Azure: 1 ejecucion historica cada una; GCP/on-premise: 0. |
| Limitaciones | CERRADO | `08_drp/REPORTE_DRP_METRICAS_100.md` | Sandbox academico, pago simulado, no active-active, no HA empresarial, no failover automatico. |

## Estado general

Bloque DRP/metricas: `CERRADO`.

La fuente final de AWS principal es versionable, fue reforzada con verificacion final no destructiva del nuevo laboratorio y esta separada de CARGA-1000. No se presenta SLA, active-active, alta disponibilidad empresarial ni failover movil automatico.
