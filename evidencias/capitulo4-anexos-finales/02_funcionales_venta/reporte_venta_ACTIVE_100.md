# Reporte venta/listing ACTIVE 100%

RUN_ID: `cap4-core4-20260614-162647`

## Criterio de cierre

El bloque se considera cerrado porque el flujo nuevo de venta crea un listing con HTTP `201`, lo consulta con HTTP `200` y confirma estado `ACTIVE` con atributos minimos y registro persistido en `core_db`.

## Evidencia principal

| Evidencia | Archivo |
|---|---|
| Request/response API saneado | `venta_ACTIVE_100_flow.json` |
| Consulta de base de datos | `db_venta_ACTIVE_100.txt` |
| Logs relevantes | `logs_venta_ACTIVE_100.txt` |

## Creacion de listing

| Campo | Valor observado |
|---|---|
| Endpoint | `POST /api/v1/listings` |
| HTTP status | `201` |
| Request | `title`, `description`, `price=64.25`, `quantity=1` |
| listingId | `bf2dc1b6-99e8-4121-ad79-f8780aba1e7f` |
| Vendedor de prueba | `cap4-core4-20260614-162647-seller-1781456226@example.com` |
| Token | `***MASKED***` |

## Consulta de listing

| Campo | Valor observado |
|---|---|
| Endpoint | `GET /api/v1/listings/bf2dc1b6-99e8-4121-ad79-f8780aba1e7f` |
| HTTP status | `200` |
| id | `bf2dc1b6-99e8-4121-ad79-f8780aba1e7f` |
| title | `cap4-core4-20260614-162647 Listing ACTIVE` |
| description | `Evidencia limpia de listing ACTIVE para cierre tecnico Capitulo 4.` |
| price | `64.25` |
| quantity | `1` |
| status | `ACTIVE` |
| createdAt | `2026-06-14T16:57:06.875728` |
| updatedAt | `2026-06-14T16:57:06.875749` |

## Base de datos

| Base | Evidencia observada |
|---|---|
| `core_db.listings` | `id=bf2dc1b6-99e8-4121-ad79-f8780aba1e7f`, `status=ACTIVE`, `price=64.25`, `quantity=1` |

## Logs

| Servicio | Evidencia observada |
|---|---|
| `core-api` | Registra `POST /api/v1/listings` y `GET /api/v1/listings/bf2dc1b6-99e8-4121-ad79-f8780aba1e7f` sin HTTP 500 |

## Resultado

Estado: `CERRADO`.

Observacion: el listing pertenece a `core-api` porque corresponde al dominio de usuarios/catalogo/publicaciones y persiste en `core_db`.
