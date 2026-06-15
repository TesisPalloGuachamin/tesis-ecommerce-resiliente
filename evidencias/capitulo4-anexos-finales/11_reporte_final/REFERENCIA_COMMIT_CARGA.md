# Referencia de commit — Bloque de carga/rendimiento Capítulo 4

## Identificación

- RUN_ID: `cap4-load-20260614-124116`
- Commit completo: `04665551ca2f91bb47df98f2d2ee1eae8d7a98d1`
- Commit corto: `0466555`
- Fecha/hora del commit: `2026-06-14T14:24:50-05:00`
- Rama: `cap4-cierre-tecnico`
- Autor/committer: `ChristopherPalloArias <christopherpallo2000@gmail.com>`

## Alcance del commit

Este commit versiona exclusivamente el bloque de carga/rendimiento del Capítulo 4:

1. CARGA-200.
2. CARGA-500.
3. CARGA-1000.
4. Script k6 de carga escalonada.
5. Reporte comparativo de carga.
6. Resumen para tesis.
7. Checklist de carga cerrada.

## Evidencia principal asociada

- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-200/`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-500/`
- `evidencias/capitulo4-anexos-finales/06_carga/CARGA-1000/`
- `evidencias/capitulo4-anexos-finales/06_carga/scripts/cap4_k6_load_200_500_1000.js`
- `evidencias/capitulo4-anexos-finales/06_carga/REPORTE_CARGA_ESCALONADA_200_500_1000.md`
- `evidencias/capitulo4-anexos-finales/06_carga/RESUMEN_TESIS_CARGA_RENDIMIENTO.md`
- `evidencias/capitulo4-anexos-finales/11_reporte_final/CHECKLIST_CARGA_CERRADA.md`

## Resultados observados

| Escenario | VUs | Objetivo | COMPLETED | Error HTTP | Error funcional | p95 HTTP | Estado |
|---|---:|---:|---:|---:|---:|---:|---|
| CARGA-200 | 10 | 200 | 200 | 0.00% | 0.00% | 184.61 ms | PASS |
| CARGA-500 | 25 | 500 | 500 | 0.00% | 0.00% | 168.22 ms | PASS |
| CARGA-1000 | 50 | 1000 | 1000 | 0.00% | 0.00% | 164.46 ms | PASS |

## Exclusiones

Este commit no incluye:
- observabilidad como bloque cerrado;
- mobile-backend;
- DRP;
- XP;
- diagramas;
- ZIPs;
- `_diagnostico_raw`;
- `capitulo4-cierre-tecnico/`.

## Nota metodológica

El commit constituye una referencia de trazabilidad para el cierre técnico del bloque de carga/rendimiento. La prueba se interpreta como carga funcional ampliada en entorno sandbox, no como benchmark productivo, certificación de SLA ni garantía de disponibilidad empresarial.
