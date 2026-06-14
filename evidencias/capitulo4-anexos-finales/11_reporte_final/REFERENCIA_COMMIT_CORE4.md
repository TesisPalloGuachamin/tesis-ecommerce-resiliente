# Referencia de commit — Primeros 4 bloques técnicos Capítulo 4

## Identificación

- RUN_ID: `cap4-core4-20260614-162647`
- Commit completo: `228f1604766cff30d15f8523fe9fa3986f8e1ddc`
- Commit corto: `228f160`
- Fecha/hora del commit: `2026-06-14T12:00:04-05:00`
- Rama: `cap4-cierre-tecnico`
- Autor/committer: `ChristopherPalloArias <christopherpallo2000@gmail.com>`

## Alcance del commit

Este commit versiona exclusivamente el primer bloque técnico de evidencias del Capítulo 4:

1. Compra hasta estado `COMPLETED`.
2. Venta/listing hasta estado `ACTIVE`.
3. Pruebas unitarias/backend.
4. RabbitMQ: publicación, consumo, cola, logs y base de datos.

## Evidencia principal asociada

- `evidencias/capitulo4-anexos-finales/11_reporte_final/CHECKLIST_PRIMEROS_4_CERRADOS.md`
- `evidencias/capitulo4-anexos-finales/01_funcionales_compra/`
- `evidencias/capitulo4-anexos-finales/02_funcionales_venta/`
- `evidencias/capitulo4-anexos-finales/04_rabbitmq/`
- `evidencias/capitulo4-anexos-finales/05_pruebas_backend/`

## Exclusiones

Este commit no incluye:
- carga/k6;
- observabilidad final;
- mobile-backend;
- DRP;
- XP;
- diagramas;
- ZIPs;
- `_diagnostico_raw`;
- `capitulo4-cierre-tecnico/`.

## Nota metodológica

El commit constituye una referencia de trazabilidad para el cierre técnico de los primeros cuatro bloques solicitados por el tutor. No representa el cierre total del Capítulo 4, sino únicamente el cierre versionado de compra, venta/publicación, pruebas backend y mensajería RabbitMQ.
