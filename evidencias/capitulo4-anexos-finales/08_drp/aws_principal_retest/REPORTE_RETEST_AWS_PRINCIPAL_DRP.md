# Reporte retest AWS principal DRP

RUN_ID: `cap4-drp-20260614-185235`
RUN_ID_AWS_PRINCIPAL: `cap4-drp-aws-main-20260614-191520`

## Resultado

Se ejecuto un retest DRP controlado en AWS principal mediante detencion temporal de `tesis-core-api` y recuperacion del mismo contenedor. El entorno no quedo caido.

## Evidencias

- `timestamps_aws_principal.json`
- `health_before_incident_after.txt`
- `smoke_before_after.json`
- `db_integrity_check.txt`
- `rabbitmq_check.txt`
- `prometheus_check.txt`
- `logs_drp_aws_principal.txt`
- `metricas_calculadas_aws_principal.md`
- `metricas_calculadas_aws_principal.json`

## Metricas

| Metrica | Valor |
|---|---:|
| RTO | 31.506 s |
| MTTR | 69.801 s |
| RPO temporal | 0.000 s |
| RPO funcional | 0 checkoutIds faltantes |
| Disponibilidad observada | 64.085272% |
| Tasa de error funcional | 0.000000% |
| Latencia p95 HTTP observada | 0.604543 s |

## Cierre operativo

- Pre-smoke: `COMPLETED`.
- Post-smoke: `COMPLETED`.
- Checkout previo verificado despues de recuperacion: `COMPLETED`.
- El contenedor `tesis-core-api` fue reiniciado y los health checks posteriores respondieron correctamente.

## Limitaciones

Este retest es una prueba controlada de sandbox. No representa SLA, active-active, alta disponibilidad empresarial ni failover automatico movil.
