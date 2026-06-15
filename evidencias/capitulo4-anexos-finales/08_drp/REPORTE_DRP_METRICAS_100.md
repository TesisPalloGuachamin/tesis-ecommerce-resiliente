# Reporte DRP y metricas

RUN_ID: `cap4-drp-20260614-185235`

## 1. Proposito

Validar documental y tecnicamente el bloque DRP/metrica del Capitulo 4 mediante evidencia versionable, definiciones formales, formulas, fuentes, valores observados e interpretacion por escenario. El objetivo es analizar resiliencia en sandbox academico, no certificar operacion productiva.

## 2. Alcance

Este bloque no presenta alta disponibilidad productiva, operacion active-active, failover automatico movil ni SLA. El pago sigue siendo simulado y no existe pasarela real. La disponibilidad reportada corresponde a ventanas controladas de prueba y no debe extrapolarse a disponibilidad mensual, anual o empresarial.

## 3. Fuente final AWS principal

No se encontro fuente local versionable para los valores historicos AWS principal indicados inicialmente. Para cerrar el bloque con trazabilidad completa, se ejecuto un retest controlado:

- RUN_ID_AWS_PRINCIPAL: `cap4-drp-aws-main-20260614-191520`.
- Carpeta: `08_drp/aws_principal_retest/`.
- Incidente: detencion temporal de `tesis-core-api`.
- Recuperacion: inicio del mismo contenedor y validacion posterior.
- Persistencia: no se eliminaron volumenes y no se uso `docker compose down -v`.
- Estado final: entorno recuperado, health posterior y smoke posterior correctos.

## 4. Separacion DRP vs carga

CARGA-1000 no reemplaza la evidencia DRP. La prueba CARGA-1000 es una prueba de rendimiento/estabilidad funcional posterior: confirma que 1000/1000 checkouts finalizaron `COMPLETED`, con 0% de error HTTP, 0% de error funcional y p95 HTTP de 164.46 ms.

Las metricas DRP de AWS principal provienen del retest controlado. La disponibilidad de `64.08527196797435%` corresponde a la ventana DRP corta con caida inducida. La disponibilidad de `100.00%` corresponde solo a la ventana de CARGA-1000. Ninguna de las dos constituye SLA.

## 4.1 Verificacion final no destructiva del nuevo laboratorio

Despues del retest se observo timeout contra el host anterior. Con el nuevo laboratorio se ejecuto una verificacion no destructiva, sin detener contenedores ni repetir incidente DRP.

Resultado externo:

- `core-api /actuator/health`: HTTP 200.
- `checkout-service /api/actuator/health`: HTTP 200.
- Login: HTTP 200.
- Productos: HTTP 200.
- Carrito: HTTP 201.
- Checkout: HTTP 202 y polling hasta `COMPLETED`.
- Listing: HTTP 201 y consulta HTTP 200 con estado `ACTIVE`.
- Prometheus targets: HTTP 200.
- Grafana health: HTTP 200.

Resultado interno:

- Docker muestra servicios funcionales saludables.
- PostgreSQL core y checkout accesibles.
- RabbitMQ accesible; colas criticas de request/core en 0.
- Prometheus `up` devuelve `core-api`, `checkout-service` y `prometheus` en valor `1`.

Correccion operativa minima aplicada: Prometheus y Grafana existian como contenedores detenidos por reinicio/cambio del laboratorio; se levantaron con `docker start tesis-prometheus tesis-grafana`, sin modificar arquitectura, codigo ni volumenes.

Esta verificacion respalda que el timeout posterior corresponde al cambio/reinicio del laboratorio o URL anterior, no a una falla funcional persistente del sistema.

## 5. Clasificacion de escenarios

| Escenario | Clasificacion | Uso en el analisis |
|---|---|---|
| AWS principal | Observado principal DRP | Retest controlado versionable con RTO, RPO, MTTR, disponibilidad, error y p95. |
| CARGA-1000 | Metrica operativa complementaria posterior | Estabilidad funcional posterior. No calcula RTO, RPO ni MTTR. |
| AWS region alterna | Observado con limitaciones | Recuperacion DRP historica con RTO, RPO temporal, RPO funcional y MTTR. |
| Azure | Observado con alcance minimo y limitaciones | Recuperacion minima historica con metricas efectivas y restricciones documentadas. |
| Google Cloud | Documental | No se presenta como prueba ejecutada. |
| On-premise | Documental | No se presenta como prueba ejecutada. |

Detalle: `clasificacion_escenarios_drp.md`.

## 6. Definicion formal de metricas

Las definiciones y formulas formales se consolidan en `metricas_drp_definiciones_formulas.md`.

| Metrica | Formula |
|---|---|
| RTO | `RTO = t_servicio_restaurado - t_inicio_incidente` |
| RPO temporal | `RPO_temporal = t_inicio_incidente - t_ultimo_respaldo_valido` |
| RPO funcional | `RPO_funcional = operaciones_esperadas - operaciones_recuperadas` |
| MTTR | `MTTR = t_fin_recuperacion - t_inicio_incidente` |
| Disponibilidad observada en ventana | `Disponibilidad = ((T_total - T_indisponibilidad) / T_total) * 100` |
| Tasa de error | `Error_rate = (errores / total_operaciones) * 100` |
| Latencia p95 | `p95 = percentil_95(tiempos_respuesta)` |

## 7. Resultados por escenario

### AWS principal - DRP observado por retest

| Metrica | Fuente | Valor observado | Interpretacion |
|---|---|---|---|
| RTO | `08_drp/aws_principal_retest/metricas_calculadas_aws_principal.json` | `31.506 s` | Tiempo entre la detencion del servicio critico y health `UP` posterior. |
| RPO temporal | `08_drp/aws_principal_retest/timestamps_aws_principal.json` | `0.000 s` | Punto recuperable operativo registrado inmediatamente antes del incidente; no hubo destruccion de datos. |
| RPO funcional | `08_drp/aws_principal_retest/smoke_before_after.json` y `db_integrity_check.txt` | `0 checkoutIds faltantes` | Checkout previo persistente y checkout posterior `COMPLETED`. |
| MTTR | `08_drp/aws_principal_retest/metricas_calculadas_aws_principal.json` | `69.801 s` | Tiempo total hasta terminar recuperacion y smoke posterior. |
| Disponibilidad observada en ventana DRP | `08_drp/aws_principal_retest/metricas_calculadas_aws_principal.json` | `64.08527196797435%` | Ventana corta de retest con caida inducida; no SLA. |
| Tasa de error funcional | `08_drp/aws_principal_retest/metricas_calculadas_aws_principal.json` | `0.000000%` | No hubo fallos funcionales en smoke pre/post; el health fallido durante incidente fue esperado. |
| Latencia p95 HTTP observada | `08_drp/aws_principal_retest/metricas_calculadas_aws_principal.json` | `0.6045430860984193 s` | p95 de muestras HTTP exitosas del retest; no SLA. |

### CARGA-1000 - complemento operativo posterior

| Metrica | Fuente | Valor observado | Interpretacion |
|---|---|---|---|
| Checkouts completados | `06_carga/CARGA-1000/k6_summary.json`. | `1000 / 1000` | Estabilidad funcional posterior. |
| Tasa de error HTTP | `06_carga/CARGA-1000/k6_summary.json`. | `0 / 27042 = 0.00%` | Sin errores HTTP en carga. |
| Tasa de error funcional | `06_carga/CARGA-1000/k6_summary.json`. | `0 / 1000 = 0.00%` | Todos los checkouts finalizaron `COMPLETED`. |
| Latencia p95 HTTP | `06_carga/CARGA-1000/k6_summary.json`. | `164.46 ms` | p95 de carga funcional ampliada, no p95 DRP. |
| Disponibilidad observada en carga | `06_carga/CARGA-1000/k6_summary.json`. | `100.00%` en la ventana de carga. | No es disponibilidad DRP ni SLA. |

### AWS region alterna

| Metrica | Fuente | Valor observado | Interpretacion |
|---|---|---|---|
| RTO | `evidencias/drp-aws-alt-01/drp-aws-alt-20260518T231958Z/drp-aws-alt-metrics.json` | `452 s` | Recuperacion alterna historica observada. |
| RPO temporal | Mismo JSON. | `2009 s` | Ventana temporal potencial desde backup historico. |
| RPO funcional | Mismo JSON. | `1000 - 1000 = 0` checkoutIds faltantes. | No se observo perdida funcional para los IDs trazados. |
| MTTR | Mismo JSON. | `452 s` | Restablecimiento observado en recuperacion alterna. |
| Disponibilidad smoke | Mismo JSON. | `1 / 1 = 100.00%` | Validacion minima posterior; no SLA. |
| Tasa de error smoke | Mismo JSON. | `0 / 1 = 0.00%` | Smoke posterior sin fallo. |
| Latencia p95 | Evidencia DRP alterna. | No consolidada en el JSON DRP historico. | No se inventa ni se extrapola. |

### Azure

| Metrica | Fuente | Valor observado | Interpretacion |
|---|---|---|---|
| RTO efectivo | `evidencias/drp-azure-01/drp-azure-20260518T233644Z/drp-azure-metrics.json` | `534.517 s` | Recuperacion minima efectiva observada en region exitosa. |
| RTO wall-clock contextual | Mismo JSON. | `1240.517 s` | Incluye intentos bloqueados por politica/region; se reporta como contexto, no como RTO efectivo principal. |
| RPO temporal efectivo | Mismo JSON. | `4911 s` | Ventana temporal potencial desde backup historico. |
| RPO funcional | Mismo JSON. | `0` faltantes y `0` no completados. | No se observo perdida funcional en el alcance minimo. |
| MTTR efectivo | Mismo JSON. | `534.517 s` | Restablecimiento efectivo observado. |
| Disponibilidad smoke | Mismo JSON. | `1 / 1 = 100.00%` | Validacion minima posterior; no SLA. |
| Tasa de error smoke | Mismo JSON. | `0 / 1 = 0.00%` | Smoke posterior sin fallo. |
| Latencia p95 | Evidencia DRP Azure. | No consolidada en el JSON DRP historico. | No se inventa ni se extrapola. |

### Google Cloud y on-premise

GCP y on-premise se clasifican como documentales. No tienen RTO, RPO, MTTR, disponibilidad, tasa de error ni latencia operativa en este proyecto. No deben presentarse como escenarios ejecutados.

## 8. Numero de ejecuciones

| Escenario | Ejecuciones operativas usadas | Observacion |
|---|---:|---|
| AWS principal DRP | 1 retest controlado (`cap4-drp-aws-main-20260614-191520`). | Fuente versionable local. |
| CARGA-1000 | 1 ejecucion final de carga funcional ampliada. | Complemento operativo posterior, no DRP. |
| AWS region alterna | 1 ejecucion DRP historica principal (`drp-aws-alt-20260518T231958Z`). | Observada con limitaciones. |
| Azure | 1 ejecucion DRP historica minima (`drp-azure-20260518T233644Z`). | Observada con alcance minimo y restricciones. |
| Google Cloud | 0 ejecuciones operativas. | Documental. |
| On-premise | 0 ejecuciones operativas. | Documental. |

## 9. Interpretacion

Se puede concluir que AWS principal tiene evidencia versionable de recuperacion controlada del servicio critico, que CARGA-1000 queda separada como estabilidad funcional posterior, y que AWS alterna/Azure se mantienen como escenarios observados con limitaciones. GCP y on-premise permanecen documentales.

No se puede concluir que exista active-active, alta disponibilidad productiva, failover automatico movil, SLA mensual/anual ni continuidad empresarial.

## 10. Relacion con objetivos

El bloque aporta evidencia para OE4 al clasificar escenarios de recuperacion y continuidad. Tambien apoya OE2/OE3 al relacionar resiliencia con arquitectura desplegada, mensajeria, persistencia, observabilidad y automatizacion DevOps/IaC.

## 11. Estado final

Estado del bloque DRP/metricas: `CERRADO`.

El cierre usa evidencia versionable para AWS principal, verificacion final no destructiva del nuevo laboratorio y metricas separadas de carga/rendimiento. No se presentan SLA, active-active, alta disponibilidad empresarial ni failover movil automatico.
