# Referencia de commit — Bloque mobile-backend Capítulo 4

## Identificación

- RUN_ID: `cap4-mobile-backend-20260614-183132`
- Commit completo: `40bf682f6a732cbafbfd09325d251590f116cb56`
- Commit corto: `40bf682`
- Fecha/hora del commit: `2026-06-14T18:46:12-05:00`
- Rama: `cap4-cierre-tecnico`

## Alcance del commit

Este commit versiona exclusivamente el bloque de integración mobile-backend del Capítulo 4:

1. Inventario de endpoints consumidos desde la aplicación móvil.
2. Validaciones estáticas mobile.
3. Flujo HTTP equivalente.
4. Errores controlados.
5. Logs mobile-backend.
6. Reporte principal.
7. Checklist mobile-backend cerrado.

## Evidencia principal asociada

- `evidencias/capitulo4-anexos-finales/03_mobile_backend/`
- `evidencias/capitulo4-anexos-finales/03_mobile_backend/REPORTE_MOBILE_BACKEND_100.md`
- `evidencias/capitulo4-anexos-finales/03_mobile_backend/inventario_endpoints_mobile.md`
- `evidencias/capitulo4-anexos-finales/03_mobile_backend/mobile_backend_flow.json`
- `evidencias/capitulo4-anexos-finales/03_mobile_backend/mobile_backend_errors.json`
- `evidencias/capitulo4-anexos-finales/03_mobile_backend/mobile_static_checks.log`
- `evidencias/capitulo4-anexos-finales/11_reporte_final/CHECKLIST_MOBILE_BACKEND_CERRADA.md`

## Resultado observado

- Validación estática TypeScript: PASS.
- Expo Doctor: 16/18 checks PASS, con advertencias no bloqueantes.
- Login: HTTP 200.
- Productos: HTTP 200.
- Detalle producto: HTTP 200.
- Carrito: HTTP 200 / 201 según operación.
- Checkout: HTTP 202 y polling hasta `COMPLETED`.
- Listing: HTTP 201 y estado `ACTIVE`.
- Errores controlados: credenciales inválidas, endpoint protegido sin token, producto inexistente y checkout inexistente.
- Validación funcional desde cliente móvil/web: documentada.
- Capturas adicionales: no anexadas para esta ejecución.

## Exclusiones

Este commit no incluye:
- compra/venta/backend/RabbitMQ;
- carga/rendimiento;
- observabilidad;
- JaCoCo;
- DRP;
- XP;
- diagramas;
- ZIPs;
- `_diagnostico_raw`;
- `capitulo4-cierre-tecnico/`.

## Nota metodológica

El commit constituye una referencia de trazabilidad para el cierre técnico del bloque mobile-backend. La configuración `EXPO_PUBLIC_API_BASE_URL` se interpreta como mecanismo de configuración del endpoint backend, no como failover automático, alta disponibilidad o garantía de continuidad productiva.
