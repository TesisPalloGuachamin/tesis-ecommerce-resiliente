# Referencia de commit - Diagramas finales Capitulo 4

## Identificacion

- Commit completo: `2ee8d1091251472d26e08642bb70bea61d1c9437`
- Commit corto: `2ee8d10`
- Fecha/hora del commit: `2026-06-14T22:48:11-05:00`
- Rama: `cap4-cierre-tecnico`

## Alcance del commit

Este commit versiona exclusivamente el bloque de diagramas finales del Capitulo 4:

1. Fuentes corregidas `.mmd`.
2. Renderizados `.png`.
3. Renderizados `.svg`.
4. Auditoria de diagramas.
5. Tabla de figuras.
6. Reporte de diagramas.
7. Checklist de diagramas cerrado.

## Evidencia principal asociada

- `evidencias/capitulo4-anexos-finales/10_diagramas_finales/`
- `evidencias/capitulo4-anexos-finales/10_diagramas_finales/fuentes/`
- `evidencias/capitulo4-anexos-finales/10_diagramas_finales/renderizados/`
- `evidencias/capitulo4-anexos-finales/10_diagramas_finales/auditoria_diagramas_capitulo4.md`
- `evidencias/capitulo4-anexos-finales/10_diagramas_finales/tabla_figuras_capitulo4.md`
- `evidencias/capitulo4-anexos-finales/10_diagramas_finales/REPORTE_DIAGRAMAS_CAPITULO4.md`
- `evidencias/capitulo4-anexos-finales/11_reporte_final/CHECKLIST_DIAGRAMAS_CAPITULO4_CERRADA.md`

## Diagramas incluidos

- C4-01 - Contexto del sistema.
- C4-02 - Contenedores.
- C4-03 - Componentes `core-api`.
- C4-04 - Componentes `checkout-service`.
- INT-01 - Mensajeria asincrona RabbitMQ.
- INT-02 - Adaptador conceptual de pago simulado.
- DEP-01 - Despliegue sandbox.
- DRP-ARCH-01 - Vista de resiliencia/DRP.

## Correcciones aplicadas

- Correccion de typo "incia checkout" a "inicia checkout".
- Uso de `AWS principal sandbox`.
- Prometheus/Grafana representados como observabilidad operativa verificada.
- Pago representado como adaptador conceptual/simulado.
- RabbitMQ representado como desacoplamiento asincrono, no como resiliencia automatica.
- Mobile representado como cliente configurado por `EXPO_PUBLIC_API_BASE_URL`, no como failover automatico.
- DRP clasificado por escenarios: AWS principal observado, AWS alterna con limitaciones, Azure con alcance minimo y GCP/on-premise documental.
- Exclusion de servicios no implementados como si fueran parte del prototipo.

## Nota metodologica

Los diagramas representan el prototipo academico observado y documentado. No representan una arquitectura productiva empresarial, active-active, SLA productivo, pagos reales ni failover automatico movil.
