# Matriz DRP final

RUN_ID: `cap4-drp-20260614-185235`

La fuente final de AWS principal es el retest controlado `cap4-drp-aws-main-20260614-191520`, guardado en `08_drp/aws_principal_retest/`. CARGA-1000 se conserva solo como metrica operativa complementaria posterior y no se usa para calcular RTO, RPO ni MTTR.

## Escenarios

| Escenario | Clasificacion | Evidencia principal | Interpretacion |
|---|---|---|---|
| AWS principal | Observado principal DRP | `08_drp/aws_principal_retest/metricas_calculadas_aws_principal.json`, `timestamps_aws_principal.json`, `smoke_before_after.json`. | Retest controlado de recuperacion del servicio critico `core-api`. No active-active, no failover automatico, no SLA. |
| AWS principal - verificacion final | Verificacion no destructiva post-DRP | `08_drp/aws_principal_retest/verificacion_final_externa_post_drp.txt`, `smoke_final_externo_post_drp.json`, `verificacion_interna_post_drp.txt`. | Confirma accesibilidad externa e interna del nuevo laboratorio despues del retest, sin repetir incidente destructivo. |
| CARGA-1000 | Metrica operativa complementaria posterior | `06_carga/CARGA-1000/k6_summary.json`. | Estabilidad funcional posterior bajo carga. No reemplaza DRP. |
| AWS region alterna | Observado con limitaciones | `evidencias/drp-aws-alt-01/drp-aws-alt-20260518T231958Z/drp-aws-alt-metrics.json`. | Recuperacion alterna historica con metricas DRP. No active-active ni operacion continua. |
| Azure | Observado con alcance minimo y limitaciones | `evidencias/drp-azure-01/drp-azure-20260518T233644Z/drp-azure-metrics.json`. | Recuperacion minima multi-cloud observada con restricciones. No entorno equivalente a AWS principal. |
| Google Cloud | Documental | Documentacion del proyecto. | No existe prueba operativa ni metrica ejecutada. |
| On-premise | Documental | Documentacion del proyecto. | No existe prueba operativa ni metrica ejecutada. |

## AWS principal - observado principal DRP

| Metrica | Formula aplicada | Fuente | Valor observado | Valor objetivo | Criterio | Interpretacion | Limitacion |
|---|---|---|---|---|---|---|---|
| RTO | `t_servicio_restaurado - t_inicio_incidente` | `aws_principal_retest/metricas_calculadas_aws_principal.json` | `31.506 s` | Restaurar servicio critico dentro de ventana academica controlada. | Menor valor indica recuperacion mas rapida. | El servicio `core-api` fue detenido y restaurado de forma controlada. | No se destruyeron volumenes; no es failover automatico. |
| RPO temporal | `t_inicio_incidente - t_ultimo_respaldo_valido` | `aws_principal_retest/timestamps_aws_principal.json` | `0.000 s` | Reducir ventana temporal de perdida. | Requiere punto recuperable identificado antes del incidente. | Se uso timestamp operativo previo como punto recuperable porque no se destruyeron datos. | No equivale a restauracion desde backup destructivo. |
| RPO funcional | `checkoutIds_esperados - checkoutIds_recuperados` | `aws_principal_retest/smoke_before_after.json`, `db_integrity_check.txt` | `0 checkoutIds faltantes` | `0` operaciones trazables perdidas. | PASS si checkout previo y posterior quedan `COMPLETED`. | Checkout previo persistio y checkout posterior completo. | Alcance limitado a smoke DRP controlado. |
| MTTR | `t_fin_recuperacion - t_inicio_incidente` | `aws_principal_retest/metricas_calculadas_aws_principal.json` | `69.801 s` | Restablecer operacion dentro de ventana academica controlada. | Menor valor indica reparacion mas rapida. | Incluye recuperacion y smoke posterior. | No representa MTTR empresarial. |
| Disponibilidad observada en ventana DRP | `((T_total - T_indisponibilidad) / T_total) * 100` | `aws_principal_retest/metricas_calculadas_aws_principal.json` | `64.08527196797435%` | Reportar disponibilidad solo de la ventana DRP. | No convertir a SLA mensual/anual. | Corresponde a ventana corta de retest con caida inducida. | No es disponibilidad CARGA-1000 ni SLA productivo. |
| Tasa de error funcional | `(errores_funcionales / operaciones_funcionales) * 100` | `aws_principal_retest/metricas_calculadas_aws_principal.json` | `0.000000%` | `0%` en smoke funcional critico. | PASS si no hay fallos funcionales en pre/post smoke. | El fallo esperado de health durante incidente se usa para disponibilidad, no como error funcional. | Alcance smoke, no carga. |
| Latencia p95 HTTP observada | `percentil_95(latencias_http_exitosas)` | `aws_principal_retest/metricas_calculadas_aws_principal.json` | `0.6045430860984193 s` | Reportar e interpretar. | No usar como SLA. | p95 calculado sobre muestras HTTP exitosas del retest. | Muestra acotada de retest. |

## AWS principal - verificacion final no destructiva

| Elemento | Fuente | Resultado observado | Interpretacion | Limitacion |
|---|---|---|---|---|
| Nuevo laboratorio | `verificacion_nuevo_laboratorio_sanitizada.txt` | Host verificado como `AWS_PRINCIPAL_HOST`; SSH y Docker accesibles. | El timeout posterior al retest se explica por cambio/reinicio del laboratorio o URL anterior. | No se guarda host literal ni credenciales. |
| Health externo | `verificacion_final_externa_post_drp.txt` | `core-api` HTTP 200 y `checkout-service` HTTP 200. | Backend accesible externamente. | Verificacion no destructiva. |
| Flujo externo | `smoke_final_externo_post_drp.json` | Login 200, productos 200, carrito 201, checkout `COMPLETED`, listing `ACTIVE`. | Flujo funcional principal disponible despues del retest. | No es nueva prueba DRP destructiva. |
| Observabilidad | `verificacion_final_externa_post_drp.txt`, `verificacion_interna_post_drp.txt` | Prometheus targets HTTP 200 con targets `up`; Grafana health HTTP 200. | Observabilidad accesible tras levantar contenedores existentes. | Correccion operativa minima: `docker start tesis-prometheus tesis-grafana`. |
| RabbitMQ/DB | `verificacion_interna_post_drp.txt` | DB core/checkout accesibles y conteos consistentes; colas criticas de request/core en 0. | Persistencia y mensajeria principal verificadas. | Existen colas auxiliares historicas con mensajes acumulados; no bloquean el smoke externo validado. |

## CARGA-1000 - metrica operativa complementaria posterior

| Metrica | Formula aplicada | Fuente | Valor observado | Valor objetivo | Criterio | Interpretacion | Limitacion |
|---|---|---|---|---|---|---|---|
| Checkouts completados | `checkouts_completed / checkouts_attempted` | `06_carga/CARGA-1000/k6_summary.json` | `1000 / 1000` | `>= 95%` completados | PASS funcional de carga. | Estabilidad funcional posterior bajo carga. | No reemplaza RPO funcional DRP. |
| Tasa de error HTTP | `(errores_http / requests_http) * 100` | `06_carga/CARGA-1000/k6_summary.json` | `0 / 27042 * 100 = 0.00%` | `<= 1%` | PASS de carga. | Sin errores HTTP en la ventana CARGA-1000. | No mide recuperacion ante incidente. |
| Tasa de error funcional | `(checkouts_fallidos / checkouts_intentados) * 100` | `06_carga/CARGA-1000/k6_summary.json` | `0 / 1000 * 100 = 0.00%` | `<= 5%` | PASS funcional. | Todos los checkouts finalizaron `COMPLETED`. | Pago simulado, sin pasarela real. |
| Latencia p95 HTTP | `percentil_95(http_req_duration)` | `06_carga/CARGA-1000/k6_summary.json` | `164.46 ms` | Reportar e interpretar. | No usar como SLA. | p95 bajo carga funcional ampliada. | No es p95 DRP. |
| Disponibilidad observada en carga | `((T_total - T_indisponibilidad) / T_total) * 100` | `06_carga/CARGA-1000/k6_summary.json` | `100.00%` durante la ventana de carga | Mantener operacion durante carga. | No extrapolar a SLA. | Solo ventana de carga posterior. | No es disponibilidad DRP ni disponibilidad mensual/anual. |

## AWS region alterna

| Metrica | Formula aplicada | Fuente | Valor observado | Valor objetivo | Criterio | Interpretacion | Limitacion |
|---|---|---|---|---|---|---|---|
| RTO | `t_servicio_restaurado - t_inicio_incidente` | `drp-aws-alt-metrics.json`. | `452 s` | Recuperar servicio en ventana academica controlada. | Menor valor indica recuperacion mas rapida. | Recuperacion alterna observada historicamente. | No se repitio en campana final. |
| RPO temporal | `t_inicio_incidente - t_ultimo_respaldo_valido` | `drp-aws-alt-metrics.json`. | `2009 s` | Reducir ventana temporal de perdida. | Requiere backup valido. | Ventana temporal calculada desde backup historico. | No representa replicacion continua. |
| RPO funcional | `operaciones_esperadas - operaciones_recuperadas` | `drp-aws-alt-metrics.json`. | `1000 - 1000 = 0` checkoutIds faltantes. | `0` faltantes. | PASS si los checkoutIds trazados se recuperan. | No hubo perdida funcional para los IDs observados. | Alcance limitado a 1000 checkoutIds historicos. |
| MTTR | `t_fin_recuperacion - t_inicio_incidente` | `drp-aws-alt-metrics.json`. | `452 s` | Restablecer operacion en ventana academica. | Puede coincidir con RTO si el servicio queda operativo al finalizar recuperacion. | Reparacion/restablecimiento observado en recuperacion alterna. | No active-active. |
| Disponibilidad observada en smoke | `(smoke_completed / smoke_requested) * 100` | `drp-aws-alt-metrics.json`. | `1 / 1 * 100 = 100.00%` | Smoke posterior exitoso. | Solo valida disponibilidad posterior minima. | Servicio recuperado respondio al smoke. | No es disponibilidad mensual/anual. |
| Tasa de error smoke | `(smoke_failed / smoke_requested) * 100` | `drp-aws-alt-metrics.json`. | `0 / 1 * 100 = 0.00%` | `0%` en smoke posterior. | PASS si smoke critico completa. | Sin fallo en smoke posterior. | Smoke unico, no campana de carga. |
| Latencia p95 | `percentil_95(tiempos_respuesta)` | Evidencia DRP alterna. | No consolidada en el JSON DRP historico. | Reportar si existe. | No inventar latencia sin fuente. | No se usa para comparar con AWS principal. | No se recalculo en esta campana. |

## Azure

| Metrica | Formula aplicada | Fuente | Valor observado | Valor objetivo | Criterio | Interpretacion | Limitacion |
|---|---|---|---|---|---|---|---|
| RTO efectivo | `t_servicio_restaurado - t_inicio_recuperacion_efectivo` | `drp-azure-metrics.json`. | `534.517 s` | Recuperar servicio en ventana academica. | Usar valor efectivo de region exitosa. | Recuperacion minima observada en Azure. | Hubo intentos bloqueados por politica/region; wall-clock total registrado como contexto. |
| RTO wall-clock contextual | `t_servicio_restaurado - t_inicio_con_intentos_bloqueados` | `drp-azure-metrics.json`. | `1240.517 s` | Informativo. | No mezclar con RTO efectivo. | Mide impacto de intentos bloqueados. | No es comparable directamente con AWS alterna. |
| RPO temporal efectivo | `t_inicio_recuperacion_efectivo - t_ultimo_respaldo_valido` | `drp-azure-metrics.json`. | `4911 s` | Reducir ventana temporal de perdida. | Requiere backup valido. | Ventana temporal observada en recuperacion minima. | Alcance minimo. |
| RPO funcional | `operaciones_esperadas - operaciones_recuperadas` | `drp-azure-metrics.json`. | `0` checkoutIds faltantes y `0` no completados. | `0` faltantes. | PASS si las operaciones trazadas no se pierden. | No se observo perdida funcional para IDs verificados. | No implica cobertura total de datos fuera del flujo. |
| MTTR efectivo | `t_fin_recuperacion - t_inicio_recuperacion_efectivo` | `drp-azure-metrics.json`. | `534.517 s` | Restablecer operacion minima. | Usar solo como alcance minimo. | Restablecimiento efectivo en region exitosa. | No entorno activo continuo. |
| Disponibilidad observada en smoke | `(smoke_completed / smoke_requested) * 100` | `drp-azure-metrics.json`. | `1 / 1 * 100 = 100.00%` | Smoke posterior exitoso. | Validacion minima posterior. | Servicio respondio al smoke. | No es SLA. |
| Tasa de error smoke | `(smoke_failed / smoke_requested) * 100` | `drp-azure-metrics.json`. | `0 / 1 * 100 = 0.00%` | `0%` en smoke posterior. | PASS si smoke critico completa. | Sin fallo en smoke posterior. | Smoke unico. |
| Latencia p95 | `percentil_95(tiempos_respuesta)` | Evidencia DRP Azure. | No consolidada en el JSON DRP historico. | Reportar si existe. | No inventar latencia sin fuente. | No se usa para comparacion productiva. | No se recalculo en esta campana. |

## Google Cloud

| Metrica | Formula aplicada | Fuente | Valor observado | Valor objetivo | Criterio | Interpretacion | Limitacion |
|---|---|---|---|---|---|---|---|
| RTO/RPO/MTTR/disponibilidad/error/latencia | No aplica como metrica operativa. | Documentacion del proyecto. | Documental. | No aplica. | No presentar como prueba ejecutada. | Escenario de analisis documental. | Sin despliegue ni medicion. |

## On-premise

| Metrica | Formula aplicada | Fuente | Valor observado | Valor objetivo | Criterio | Interpretacion | Limitacion |
|---|---|---|---|---|---|---|---|
| RTO/RPO/MTTR/disponibilidad/error/latencia | No aplica como metrica operativa. | Documentacion del proyecto. | Documental. | No aplica. | No presentar como prueba ejecutada. | Escenario de fallback documental. | Sin infraestructura observada. |
