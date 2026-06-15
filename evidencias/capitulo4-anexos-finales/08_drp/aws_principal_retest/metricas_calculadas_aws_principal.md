# Metricas calculadas AWS principal retest

RUN_ID: `cap4-drp-20260614-185235`
RUN_ID_AWS_PRINCIPAL: `cap4-drp-aws-main-20260614-191520`

## Fuente

Retest controlado AWS principal con detencion temporal de `tesis-core-api`. No se eliminaron volumenes, no se ejecuto `docker compose down -v` y no se modifico arquitectura.

## Valores observados

| Metrica | Formula | Valor observado |
|---|---|---:|
| RTO | `t_servicio_restaurado - t_inicio_incidente` | 31.506 s |
| MTTR | `t_fin_recuperacion - t_inicio_incidente` | 69.801 s |
| RPO temporal | `t_inicio_incidente - t_ultimo_respaldo_valido` | 0.000 s |
| RPO funcional | `checkoutIds_esperados - checkoutIds_recuperados` | 0 checkoutIds faltantes |
| Disponibilidad observada | `((T_total - T_indisponibilidad) / T_total) * 100` | 64.085272% |
| Tasa de error funcional | `(errores_funcionales / operaciones_funcionales) * 100` | 0.000000% |
| Latencia p95 HTTP observada | `percentil_95(latencias_http_exitosas)` | 0.604543 s |

## Trazabilidad

- Checkout previo: `5a683cab-3ab5-4ced-b433-b6e0f24ecc61` estado `COMPLETED`.
- Checkout posterior: `4a0c8485-904a-4b16-afa6-cdf93819f612` estado `COMPLETED`.
- Verificacion del checkout previo despues de recuperar: `COMPLETED`.

## Limitaciones

- Incidente controlado por parada de `core-api`, no destruccion de volumenes.
- Disponibilidad solo de la ventana de retest, no SLA.
- Pago simulado, sin pasarela real.
- No active-active, no failover automatico movil, no alta disponibilidad empresarial.
