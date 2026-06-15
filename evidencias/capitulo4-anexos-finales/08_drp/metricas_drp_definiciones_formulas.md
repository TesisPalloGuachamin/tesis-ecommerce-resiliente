# Definiciones y formulas DRP

RUN_ID: `cap4-drp-20260614-185235`

Las metricas siguientes se usan para interpretar resiliencia en un sandbox academico. No constituyen SLA, certificacion de alta disponibilidad ni garantia productiva.

| Metrica | Definicion | Formula | Fuente de datos | Valor objetivo | Criterio de interpretacion |
|---|---|---|---|---|---|
| RTO | Tiempo entre el inicio del incidente y la restauracion observable del servicio critico. | `RTO = t_servicio_restaurado - t_inicio_incidente` | Timestamps de runbook DRP, logs de recuperacion, smoke posterior y health checks. | Recuperar el servicio critico dentro de una ventana academica controlada. | Menor valor indica recuperacion mas rapida; solo aplica cuando existe incidente o recuperacion ejecutada. |
| RPO temporal | Ventana temporal potencial de perdida entre el incidente y el ultimo punto recuperable. | `RPO_temporal = t_inicio_incidente - t_ultimo_respaldo_valido` | Timestamp de backup, timestamp de inicio de recuperacion/incidente. | Reducir la ventana temporal de datos potencialmente no recuperables. | Menor valor indica menor ventana potencial de perdida; requiere punto recuperable identificado. |
| RPO funcional | Perdida funcional de operaciones verificables. | `RPO_funcional = operaciones_esperadas - operaciones_recuperadas` | CheckoutIds, ordenes simuladas, consultas en DB y reportes de smoke/carga. | `0` operaciones verificables perdidas en el flujo evaluado. | Cero indica que las operaciones trazadas fueron recuperadas o completadas; no equivale a garantia global de datos. |
| MTTR | Tiempo total de reparacion/restablecimiento operativo desde el incidente hasta el fin de la recuperacion. | `MTTR = t_fin_recuperacion - t_inicio_incidente` | Runbook, logs de reinicio, health posterior, smoke funcional posterior. | Restablecer operacion dentro de una ventana academica controlada. | Puede coincidir con RTO en recuperaciones simples; no debe extrapolarse fuera del escenario. |
| Disponibilidad observada en ventana | Porcentaje de disponibilidad dentro de una ventana controlada de observacion. | `Disponibilidad = ((T_total - T_indisponibilidad) / T_total) * 100` | k6, health checks, Prometheus/API, logs de disponibilidad durante la ventana. | Mantener los servicios disponibles durante la ventana evaluada. | Solo describe la ventana de prueba; no es disponibilidad mensual, anual ni SLA. |
| Tasa de error | Porcentaje de solicitudes u operaciones fallidas durante la campana. | `Error_rate = (errores / total_operaciones) * 100` | k6, logs HTTP, reportes de smoke, PromQL o respuestas API. | `<= 1%` HTTP para carga; `<= 5%` funcional para checkout. | Debe interpretarse segun el tipo de prueba: HTTP, funcional o smoke. |
| Latencia p95 | Percentil 95 de tiempos de respuesta observados. | `p95 = percentil_95(tiempos_respuesta)` | k6, Prometheus o logs, segun evidencia disponible. | Reportar e interpretar; no usar como SLA productivo. | Indica que 95% de respuestas estuvieron por debajo de ese valor en la ventana observada. |

## Notas metodologicas

- RTO y MTTR solo se calculan como metricas operativas cuando existe incidente o recuperacion ejecutada con timestamps.
- RPO temporal requiere un punto de respaldo valido.
- RPO funcional se calcula con operaciones trazables, especialmente checkoutIds u ordenes simuladas.
- Disponibilidad observada en ventana no debe convertirse en SLA mensual, anual ni empresarial.
- GCP y on-premise no tienen metricas operativas en este proyecto; son escenarios documentales.
