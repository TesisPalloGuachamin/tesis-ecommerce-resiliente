# Clasificacion de escenarios DRP

RUN_ID: `cap4-drp-20260614-185235`

| Escenario | Tipo de validacion | Nivel de evidencia | Metricas disponibles | Limitacion | Uso en analisis |
|---|---|---|---|---|---|
| AWS principal | Observado principal DRP | Retest controlado versionable en `08_drp/aws_principal_retest/`. | RTO, RPO temporal, RPO funcional, MTTR, disponibilidad observada en ventana DRP, tasa de error funcional y latencia p95 HTTP. | Incidente controlado por parada de `core-api`; no destruccion de volumenes; no SLA. | Escenario principal DRP para recuperacion controlada del servicio critico. |
| CARGA-1000 | Metrica operativa complementaria posterior | `06_carga/CARGA-1000/k6_summary.json`. | 1000/1000 checkouts `COMPLETED`, 0% error HTTP, 0% error funcional, p95 164.46 ms, disponibilidad 100% en ventana de carga. | No es DRP, no calcula RTO/RPO/MTTR y no es SLA. | Evidencia de estabilidad funcional posterior. |
| AWS region alterna | Observado con limitaciones | Recuperacion historica documentada en `drp-aws-alt-20260518T231958Z`. | RTO, MTTR, RPO temporal, RPO funcional y smoke posterior. | No operacion activa continua, no active-active, no repeticion durante campana final. | Evidencia de recuperacion alterna controlada con limitaciones academicas. |
| Azure | Observado con alcance minimo y limitaciones | Recuperacion historica documentada en `drp-azure-20260518T233644Z`. | RTO efectivo, MTTR efectivo, RPO temporal efectivo, RPO funcional y smoke posterior. | Alcance minimo; restricciones de region/politicas; no entorno activo continuo. | Evidencia auxiliar multi-cloud minima, no equivalente a AWS principal. |
| Google Cloud | Documental | Documentacion de escenario, sin prueba operativa ejecutada. | No aplica como metrica operativa. | No hay despliegue, smoke, backup restaurado ni medicion. | Referencia documental para discusion de portabilidad/DRP. |
| On-premise | Documental | Documentacion de fallback/portabilidad, sin prueba operativa ejecutada. | No aplica como metrica operativa. | Depende de infraestructura externa no observada. | Referencia documental de contingencia, no evidencia operativa. |

## Criterios de clasificacion

- `Observado principal DRP`: evidencia DRP/recovery del escenario principal, separada de carga.
- `Metrica operativa complementaria posterior`: evidencia de estabilidad/rendimiento que no reemplaza metricas DRP.
- `Observado con limitaciones`: ejecucion real o historica con mediciones, pero no continua ni equivalente a produccion.
- `Observado con alcance minimo y limitaciones`: ejecucion real acotada para demostrar viabilidad tecnica parcial.
- `Documental`: descrito en documentacion sin ejecucion operativa ni mediciones.
