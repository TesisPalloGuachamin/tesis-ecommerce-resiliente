# Auditoria de evidencia DRP

RUN_ID: `cap4-drp-20260614-185235`

No se encontro fuente local versionable para los valores historicos AWS principal indicados inicialmente. Por rigor metodologico, esos valores no se usan como fuente final. Se ejecuto un retest controlado AWS principal con RUN_ID_AWS_PRINCIPAL `cap4-drp-aws-main-20260614-191520`.

## Alcance de la auditoria

La revision diferencia evidencia operativa, evidencia observada con limitaciones, evidencia de alcance minimo y evidencia documental. No se reclasifica ningun escenario como alta disponibilidad productiva, active-active, failover automatico ni SLA.

| Escenario | Evidencia encontrada | Metricas encontradas | Nivel de validacion | Brecha | Accion requerida |
|---|---|---|---|---|---|
| AWS principal | `08_drp/aws_principal_retest/` con plan, timestamps, health, smoke, DB, RabbitMQ, Prometheus, logs y metricas calculadas. | RTO 31.506 s, MTTR 69.801 s, RPO temporal 0.000 s, RPO funcional 0 checkoutIds faltantes, disponibilidad observada 64.085272%, tasa de error funcional 0.000000%, latencia p95 HTTP 0.604543 s. | Observado principal DRP. | No se usa la metrica historica sin fuente local. El retest no destruye volumenes; simula caida controlada de `core-api`. | Usar retest como fuente versionable final de AWS principal. |
| AWS principal - verificacion final nuevo laboratorio | `08_drp/aws_principal_retest/verificacion_nuevo_laboratorio_sanitizada.txt`, `verificacion_final_externa_post_drp.txt`, `smoke_final_externo_post_drp.json`, `verificacion_interna_post_drp.txt`. | Health externo core-api 200, checkout-service 200, login 200, productos 200, carrito 201, checkout 202 y `COMPLETED`, listing 201 y `ACTIVE`, Prometheus targets 200, Grafana health 200. | Verificacion no destructiva post-DRP. | Prometheus/Grafana existian pero estaban detenidos por reinicio/cambio de laboratorio; se levantaron contenedores existentes con `docker start`, sin modificar arquitectura ni datos. | Confirma accesibilidad externa posterior y explica el timeout previo como condicion del laboratorio/URL anterior. |
| CARGA-1000 | `06_carga/CARGA-1000/k6_summary.json`. | 1000/1000 checkouts `COMPLETED`, error HTTP 0.00%, error funcional 0.00%, p95 HTTP 164.46 ms, disponibilidad observada 100% durante ventana de carga. | Metrica operativa complementaria posterior. | No es DRP y no reemplaza RTO, RPO ni MTTR. | Mantener separada como estabilidad funcional posterior. |
| AWS region alterna | `evidencias/drp-aws-alt-01/drp-aws-alt-20260518T231958Z/drp-aws-alt-metrics.json` y archivos asociados. | RTO 452 s, MTTR 452 s, RPO temporal 2009 s, RPO funcional 0 faltantes sobre 1000 checkoutIds, smoke 1/1 completado. | Observado con limitaciones. | No se repitio durante la campana final. No representa operacion active-active. | Usar como metrica historica observada y limitar su interpretacion a recuperacion alterna controlada. |
| Azure | `evidencias/drp-azure-01/drp-azure-20260518T233644Z/drp-azure-metrics.json` y archivos asociados. | RTO efectivo 534.517 s, MTTR efectivo 534.517 s, RPO temporal efectivo 4911 s, RPO funcional 0 faltantes y 0 no completados, smoke 1/1 completado. | Observado con alcance minimo y limitaciones. | La ejecucion tuvo restricciones de region/politicas y alcance minimo. No se repitio durante la campana final. | Usar como evidencia auxiliar de recuperacion minima, no como entorno activo continuo. |
| Google Cloud | Documentacion y referencias de arquitectura/DRP. | No aplica como metrica operativa. | Documental. | No existe prueba operativa ejecutada ni infraestructura observada en esta campana. | Mantener solo como escenario documental. |
| On-premise | Documentacion de portabilidad/runbook como escenario de fallback. | No aplica como metrica operativa. | Documental. | No existe prueba operativa ejecutada ni infraestructura observada en esta campana. | Mantener solo como escenario documental. |

## Resultado de la busqueda historica

Se busco en el repo y evidencias existentes cualquier fuente local para: `131`, `356`, `123`, `93.24324324324324`, `0.6360755134494491`, `0.061936746406679545`, `RTO`, `RPO`, `MTTR`, `disponibilidad` y `checkoutIds`. La documentacion historica describe el ensayo, pero no contiene una carpeta versionable con los valores exactos. Por tanto, la fuente final para AWS principal es el retest controlado.

## Decision de reejecucion

Se ejecuto retest controlado AWS principal porque no habia fuente local versionable para los valores historicos. La prueba detuvo temporalmente solo `tesis-core-api`, no elimino volumenes, no ejecuto `docker compose down -v`, no modifico arquitectura y el entorno quedo recuperado al final.

## Verificacion final no destructiva

Posterior al retest se observo timeout contra el host anterior. Con el nuevo laboratorio se realizo una verificacion no destructiva: health externo, flujo funcional pequeno, Prometheus, Grafana y verificacion interna por SSH. El backend quedo accesible externamente. La evidencia respalda que el timeout anterior estuvo asociado al cambio/reinicio del laboratorio o URL anterior, no a una falla funcional persistente del prototipo.
