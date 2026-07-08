# Trazabilidad de iteraciones, commits y versiones

| Campo | Valor |
|---|---|
| Fecha de revision | 2026-07-08 |
| Rama de trabajo | `cap4-ajustes-tutor-final` |
| Commit actual de referencia | `acf46725bb4dfd49081eb1a18c1a7084e34f520e` |
| Cierre tecnico anterior | `a5947c69399a4355fab06514def75cd27a64c5fb` |
| Tag de cierre anterior | `v1.0-cap4-final-evidence` |
| Evidencia unitaria complementaria historica | `a3a44494adb149e6567b01c2e2bcb5572aa98102` |

## Iteraciones reconstruidas desde Git y evidencias

| Iteracion | Fecha o rango | Commit o commits | Incremento funcional o tecnico | Evidencia asociada | Resultado | Version desplegada | Fuente usada |
|---|---|---|---|---|---|---|---|
| I01 Base DevOps e IaC inicial | 2026-03-21 a 2026-03-22 | `a5b2b1a`, `d2c3431`, `472e6c7`, `7337354` | Fundacion Terraform EC2 y workflows GitHub Actions. | `.github/workflows/docker-build-push.yml`; `.github/workflows/deploy-ec2.yml`; `infra/environments/demo/` | Evidencia historica de configuracion | No verificable con la evidencia disponible | `git log`; archivos IaC/CI |
| I02 Checkout asincrono y RabbitMQ | 2026-04-03 a 2026-04-04 | `dc08a21`, `3b8e624`, `6bb8a17`, `a0e2e91`, `5f54a7e` | Checkout por `requestId`, eventos con RabbitMQ, outbox y configuracion de colas. | `apps/core-api/src`; `apps/checkout-service/src`; `evidencias/capitulo4-anexos-finales/04_rabbitmq/reporte_rabbitmq_100.md` | Flujo asincrono documentado | No verificable con la evidencia disponible | `git log`; reporte RabbitMQ |
| I03 Mobile, venta y evidencias QA iniciales | 2026-05-06 | `37cbf45`, `fb5c5f2`, `59a0989`, `503a4b2` | App movil Expo, integracion API, venta/listing y evidencias QA. | `evidencias/capitulo4-anexos-finales/02_funcionales_venta/`; `03_mobile_backend/` | Evidencia funcional integrada | No verificable con la evidencia disponible | `git log`; evidencias Capitulo 4 |
| I04 Observabilidad y DRP | 2026-05-08 a 2026-05-18 | `909c349`, `ea66cc1`, `ee5d71e`, `973a863`, `bebf8d5` | Actuator Prometheus, dashboard Grafana, protocolos DRP AWS/Azure y evidencias de recuperacion. | `evidencias/capitulo4-anexos-finales/07_observabilidad/`; `08_drp/`; `evidencias/drp-aws-alt-01/`; `evidencias/drp-azure-01/` | Evidencias DRP/observabilidad generadas | No verificable con la evidencia disponible | `git log`; evidencias DRP |
| I05 Cierre tecnico Capitulo 4 | 2026-06-14 | `9d60920`, `453feab`, `80d102c`, `1b318de`, `09bf0f9`, `64f14ea`, `1ccde37`, `59bd928`, `d95375b`, merge `a5947c6` | Consolidacion de evidencias funcionales, carga, observabilidad, DRP, trazabilidad y diagramas. | `evidencias/capitulo4-anexos-finales/` | Cierre tecnico historico | `v1.0-cap4-final-evidence` apunta a `a5947c6` | `git log`; `git describe --tags` |
| I06 Pago simulado negativo unitario complementario | 2026-06-29 | `a3a4449` | Evidencia complementaria de prueba unitaria de pago rechazado. | `evidencias/capitulo4-anexos-finales/05_pruebas_backend/pago_simulado_negativo/` | PASS unitario historico | No verificable con la evidencia disponible | `git log`; evidencia C4-EV-009 |
| I07 Correccion final: pago negativo E2E y fallos RabbitMQ | 2026-07-07 a 2026-07-08 | `44f3726`, `90d0b4f`, `c229e18`, `acf4672` | Rechazo controlado de pago en sandbox, prueba E2E AWS, pruebas RabbitMQ MQ-01 a MQ-07, requeue/DLQ controlados. | `05_pruebas_backend/pago_simulado_negativo_e2e/`; `04_rabbitmq/fallos_asincronos/`; `08_drp/aws_cap4_ajustes_final/` | PASS en AWS EC2 para pago E2E y MQ-01..MQ-07 | Imagen local Docker Compose en EC2; digest no verificable con la evidencia disponible | Evidencias nuevas y `ambiente_versiones.txt` |

## Nota de consistencia

`a5947c6` permanece como cierre tecnico anterior y el tag `v1.0-cap4-final-evidence` no fue movido. La evidencia C4-EV-009 corresponde a una prueba unitaria complementaria generada en `a3a4449`, posterior al cierre `a5947c6`. Las nuevas evidencias end-to-end y de fallos asincronos pertenecen a la rama de ajustes finales y a commits posteriores; se recomienda crear un nuevo tag anotado despues de integrar el PR.

