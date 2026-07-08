# Reporte pago simulado negativo E2E

| Campo | Valor |
|---|---|
| RUN_ID | `cap4-payment-negative-e2e-20260708T020358Z` |
| Fecha UTC | `2026-07-08T02:05:30+00:00` |
| Rama | `cap4-ajustes-tutor-final` |
| Commit | `90d0b4f549578eb56ae900c815912c50ca76dda3` |
| Ambiente | Docker Compose sandbox sobre EC2 AWS con `deploy/compose/docker-compose.dev.yml` |
| Dato de entrada | Producto con precio `29.99`, cantidad `1` |
| Condicion simulada | `PaymentPort` retorna `success=false` por bandera sandbox del adaptador simulado |
| HTTP registro | `201` |
| HTTP productos | `200` |
| HTTP carrito | `201` |
| HTTP checkout | `202` |
| Estado esperado | `FAILED` |
| Estado observado | `FAILED` |
| RequestId | `1bd8b411-6b6c-4802-80dc-8ccd536a2bad` |
| EventId | `0e0cb7e8-f383-4e63-a56f-37cb381213cd` |
| Criterio de aprobacion | Estado `FAILED` en API y bases, error `simulated decline`, sin estado final `COMPLETED` |
| Resultado | `PASS` |

## Archivos de evidencia

- `respuestas_http.json`
- `estado_checkout.json`
- `verificacion_core_db.txt`
- `verificacion_checkout_db.txt`
- `eventos_rabbitmq.json`
- `logs_core_api.txt`
- `logs_checkout_service.txt`
- `docker_images.txt`
- `resultado_prueba.txt`
- `exit_code.txt`
