# Tabla resumen de evidencias Capitulo 4

| Evidencia | Carpeta / archivo | Proposito | Resultado | Estado | Uso en tesis |
|---|---|---|---|---|---|
| Compra funcional | `evidencias/capitulo4-anexos-finales/01_funcionales_compra/` | Demostrar flujo de compra hasta `COMPLETED` con request, response, DB y logs | Checkout trazable finalizado `COMPLETED` | CERRADO | Soporte de validacion funcional del proceso de compra |
| Venta/listing | `evidencias/capitulo4-anexos-finales/02_funcionales_venta/` | Demostrar creacion y consulta de listing hasta `ACTIVE` | Listing creado con HTTP 201, consultado con HTTP 200 y estado `ACTIVE` | CERRADO | Soporte de validacion funcional del proceso de venta/publicacion |
| Mobile-backend | `evidencias/capitulo4-anexos-finales/03_mobile_backend/` | Documentar endpoints consumidos desde app, validacion estatica, flujo HTTP equivalente y cliente movil/web | Integracion cerrada con checkout `COMPLETED`, listing `ACTIVE` y errores controlados | CERRADO | Respuesta directa a observacion de integracion app-backend |
| RabbitMQ | `evidencias/capitulo4-anexos-finales/04_rabbitmq/` | Evidenciar publicacion, consumo, colas, logs y persistencia | Flujo asincrono trazable con cola critica sin backlog y checkout `COMPLETED` | CERRADO | Soporte de arquitectura asincrona desacoplada |
| Pruebas backend | `evidencias/capitulo4-anexos-finales/05_pruebas_backend/` | Evidenciar pruebas Maven, Surefire y cobertura JaCoCo complementaria | Cierre inicial 15 PASS; refuerzo JaCoCo con `core-api` 29 PASS y `checkout-service` 13 PASS | CERRADO | Soporte de calidad tecnica backend y evidencia academica de cobertura |
| Carga/rendimiento | `evidencias/capitulo4-anexos-finales/06_carga/` | Evaluar carga funcional ampliada en 200, 500 y 1000 checkouts simulados | Tres escenarios PASS con 0.00% error HTTP y 0.00% error funcional | CERRADO | Soporte de rendimiento observado en sandbox, sin SLA |
| Observabilidad | `evidencias/capitulo4-anexos-finales/07_observabilidad/` | Validar Prometheus/Grafana, targets, PromQL, Actuator y health/API | 3 targets UP; Actuator HTTP 200; Grafana health HTTP 200 | CERRADO | Soporte de monitoreo tecnico del prototipo |
| DRP/metricas | `evidencias/capitulo4-anexos-finales/08_drp/` | Consolidar DRP, clasificacion de escenarios, metricas y retest AWS principal | AWS principal observado; AWS alterna/Azure con limitaciones; GCP/on-premise documentales | CERRADO | Soporte de resiliencia y Plan de Recuperacion de Desastres |
| Reportes finales/checklists | `evidencias/capitulo4-anexos-finales/11_reporte_final/` | Concentrar checklists y referencias de commit por bloque | Bloques cerrados con trazabilidad documental y commits de respaldo | CERRADO | Indice de control para anexos y cierre de observaciones del tutor |

## Nota

Las evidencias operativas se usan para validar el prototipo observado. Las evidencias documentales se usan para delimitar alcance y evitar extrapolaciones no soportadas.
