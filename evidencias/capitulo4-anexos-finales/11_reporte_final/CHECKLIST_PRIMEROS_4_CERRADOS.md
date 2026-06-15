# Checklist primeros 4 bloques cerrados

RUN_ID: `cap4-core4-20260614-162647`

## Referencia de versionamiento

Las evidencias de este checklist quedaron versionadas en el commit:

- Commit completo: `228f1604766cff30d15f8523fe9fa3986f8e1ddc`
- Commit corto: `228f160`
- Fecha/hora: `2026-06-14T12:00:04-05:00`
- Rama: `cap4-cierre-tecnico`
- RUN_ID: `cap4-core4-20260614-162647`

## Evidencia complementaria posterior

Se agrego evidencia complementaria de cobertura JaCoCo para reforzar el bloque de pruebas backend, sin reabrir ni alterar el cierre original de los primeros cuatro bloques tecnicos. La evidencia queda ubicada en:

- `evidencias/capitulo4-anexos-finales/05_pruebas_backend/REPORTE_COBERTURA_JACOCO_BACKEND.md`
- `evidencias/capitulo4-anexos-finales/05_pruebas_backend/cobertura_jacoco/`

La cobertura se interpreta como soporte academico adicional sobre las pruebas automatizadas existentes. No reemplaza las evidencias funcionales, no aplica umbrales minimos y no representa certificacion productiva.

Posteriormente se reforzo JaCoCo con pruebas unitarias adicionales sin dependencias externas. Resultado final observado:

- `core-api`: 29 pruebas PASS; cobertura de lineas 50.76%; cobertura de instrucciones 46.67%.
- `checkout-service`: 13 pruebas PASS; cobertura de lineas 54.19%; cobertura de instrucciones 29.63%.

| Bloque | Criterio de cierre | Archivos de evidencia | Resultado observado | Estado | Observacion |
|---|---|---|---|---|---|
| 1. Compra hasta `COMPLETED` | Autenticacion, catalogo, carrito, checkout, DB y logs trazables por el mismo `requestId` | `01_funcionales_compra/reporte_compra_COMPLETED_100.md`; `01_funcionales_compra/compra_COMPLETED_100_flow.json`; `01_funcionales_compra/db_compra_COMPLETED_100.txt`; `01_funcionales_compra/logs_compra_COMPLETED_100.txt` | Checkout `bf08ff9a-ed24-4baf-8643-1d8c740107ee` queda `COMPLETED` | CERRADO | Token y password enmascarados; host normalizado como `AWS_PRINCIPAL_HOST` |
| 2. Venta/listing hasta `ACTIVE` | `POST /api/v1/listings` devuelve `201`, `GET /api/v1/listings/{id}` devuelve `200`, DB confirma `ACTIVE` | `02_funcionales_venta/reporte_venta_ACTIVE_100.md`; `02_funcionales_venta/venta_ACTIVE_100_flow.json`; `02_funcionales_venta/db_venta_ACTIVE_100.txt`; `02_funcionales_venta/logs_venta_ACTIVE_100.txt` | Listing `bf2dc1b6-99e8-4121-ad79-f8780aba1e7f` queda `ACTIVE` | CERRADO | Sin HTTP 500 observado en el flujo |
| 3. Pruebas unitarias/backend | `core-api` y `checkout-service` ejecutan `mvn test` con `BUILD SUCCESS` y Surefire disponible | `05_pruebas_backend/reporte_pruebas_backend_100.md`; `05_pruebas_backend/core-api-mvn-test-100.log`; `05_pruebas_backend/checkout-service-mvn-test-100.log`; `05_pruebas_backend/surefire-core-api-100/`; `05_pruebas_backend/surefire-checkout-service-100/` | `core-api`: 11 pruebas PASS; `checkout-service`: 4 pruebas PASS | CERRADO | En el cierre original no se agrego JaCoCo; la cobertura se incorporo despues como evidencia complementaria |
| 4. RabbitMQ publicacion/consumo/cola/logs/DB | Publicacion `checkout.requested`, consumo, pago simulado, `checkout.completed`, colas y persistencia trazables | `04_rabbitmq/reporte_rabbitmq_100.md`; `04_rabbitmq/db_trace_100_raw.txt`; `04_rabbitmq/logs_rabbitmq_100.txt`; `04_rabbitmq/rabbitmq_queues_100.json`; `04_rabbitmq/trace_ids_100.txt` | Cola critica queda sin backlog; DB confirma `COMPLETED` | CERRADO | Colas informativas sin consumidor dedicado conservan mensajes, sin afectar cierre del checkout |

## Resultado global

Los primeros cuatro bloques tecnicos solicitados para Capitulo 4 quedan en estado `CERRADO`.

No se trabajo en carga, DRP, mobile, XP, diagramas ni redaccion de tesis.
