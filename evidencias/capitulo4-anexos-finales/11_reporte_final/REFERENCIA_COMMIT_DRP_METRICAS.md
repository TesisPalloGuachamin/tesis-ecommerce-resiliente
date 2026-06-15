# Referencia de commit - Bloque DRP y metricas Capitulo 4

## Identificacion

- RUN_ID principal: `cap4-drp-20260614-185235`
- RUN_ID_AWS_PRINCIPAL: `cap4-drp-aws-main-20260614-191520`
- Commit completo: `6bbb6f3c26c47470160fcebd0484e6d63a862dcc`
- Commit corto: `6bbb6f3`
- Fecha/hora del commit: `2026-06-14T22:01:08-05:00`
- Rama: `cap4-cierre-tecnico`

## Alcance del commit

Este commit versiona exclusivamente el bloque DRP/metricas del Capitulo 4:

1. Auditoria de evidencia DRP.
2. Definiciones y formulas de metricas.
3. Clasificacion de escenarios DRP.
4. Matriz final DRP.
5. Reporte DRP/metricas.
6. Retest AWS principal.
7. Verificacion externa e interna post-DRP.
8. Checklist DRP/metricas cerrado.

## Evidencia principal asociada

- `evidencias/capitulo4-anexos-finales/08_drp/`
- `evidencias/capitulo4-anexos-finales/08_drp/REPORTE_DRP_METRICAS_100.md`
- `evidencias/capitulo4-anexos-finales/08_drp/matriz_drp_final.md`
- `evidencias/capitulo4-anexos-finales/08_drp/metricas_drp_definiciones_formulas.md`
- `evidencias/capitulo4-anexos-finales/08_drp/clasificacion_escenarios_drp.md`
- `evidencias/capitulo4-anexos-finales/08_drp/aws_principal_retest/`
- `evidencias/capitulo4-anexos-finales/11_reporte_final/CHECKLIST_DRP_METRICAS_CERRADA.md`

## Resultado observado

### AWS principal

- Tipo: recuperacion operativa controlada del servicio critico.
- RTO: `31.506 s`.
- MTTR: `69.801 s`.
- RPO temporal: `0.000 s`.
- RPO funcional: `0 checkoutIds faltantes`.
- Disponibilidad observada en ventana DRP: `64.08527196797435%`.
- Tasa de error funcional: `0.000000%`.
- Latencia p95 HTTP observada: `0.6045430860984193 s`.
- Verificacion externa final: PASS.
- Verificacion interna final: PASS.

### Complemento operativo CARGA-1000

- `1000/1000` checkouts `COMPLETED`.
- Error HTTP: `0.00%`.
- Error funcional: `0.00%`.
- p95: `164.46 ms`.
- Disponibilidad: `100.00%` solo en ventana de carga.
- No se usa para RTO, RPO ni MTTR.

### Otros escenarios

- AWS alterna: observado con limitaciones.
- Azure: observado con alcance minimo y limitaciones.
- GCP: documental.
- On-premise: documental.

## Exclusiones

Este commit no incluye:
- core4;
- carga/rendimiento;
- observabilidad;
- JaCoCo;
- mobile-backend;
- XP;
- diagramas;
- tablas generales;
- ZIPs;
- `_diagnostico_raw`;
- `.exit`;
- `capitulo4-cierre-tecnico/`.

## Nota metodologica

Las metricas corresponden a una ventana controlada de laboratorio academico. No representan SLA productivo, alta disponibilidad empresarial, active-active ni failover automatico movil. La disponibilidad reportada se interpreta solo dentro de la ventana de prueba observada.
