# Tabla metodologia - evidencia

| Elemento metodologico | Como se aplico | Evidencia | Resultado | Observacion |
|---|---|---|---|---|
| XP | Trabajo incremental por bloques tecnicos: core4, carga, observabilidad, mobile-backend, DRP y trazabilidad | Checklists en `11_reporte_final/`; commits tecnicos/documentales por bloque | Bloques cerrados con evidencia versionada | XP se evidencia como proceso incremental, no como herramienta automatica |
| Iteraciones/incrementos | Cada bloque se valido y luego se documento con referencia de commit | `REFERENCIA_COMMIT_*.md`; `indice_commits_capitulo4.md` | Trazabilidad entre evidencia y versionamiento | No sustituye gestion formal externa del proyecto |
| Pruebas funcionales | Compra `COMPLETED`, venta/listing `ACTIVE`, errores controlados mobile-backend | `01_funcionales_compra/`; `02_funcionales_venta/`; `03_mobile_backend/` | Flujos principales cerrados | Pago simulado, sin transacciones reales |
| Pruebas de integracion | RabbitMQ, checkout asincrono, mobile-backend y persistencia | `04_rabbitmq/`; `03_mobile_backend/mobile_backend_flow.json` | Publicacion/consumo y endpoints consumidos por la app validados | No implica failover automatico movil |
| Pruebas backend | Maven/Surefire y JaCoCo complementario | `05_pruebas_backend/`; `REPORTE_COBERTURA_JACOCO_BACKEND.md` | `core-api` 29 PASS; `checkout-service` 13 PASS con cobertura reforzada | No se imponen umbrales de cobertura en build |
| Pruebas de carga | k6 `shared-iterations` con 200, 500 y 1000 checkouts simulados | `06_carga/` | Tres escenarios PASS, 0.00% error HTTP y funcional | No es benchmark productivo |
| Observabilidad | Prometheus, Actuator, PromQL, Grafana health/API y datasource | `07_observabilidad/` | 3 targets UP; Grafana HTTP 200 | No es monitoreo empresarial ni SLA |
| DRP | Retest AWS principal, clasificacion de escenarios, formulas y fuentes | `08_drp/` | AWS principal CERRADO; AWS alterna/Azure con limitaciones; GCP/on-premise documental | No active-active, no alta disponibilidad productiva |
| Arquitectura hexagonal | Separacion de responsabilidades en backend y pruebas sobre mappers, dominio, casos de uso y puertos mockeados | `05_pruebas_backend/REPORTE_COBERTURA_JACOCO_BACKEND.md`; codigo versionado en commits de backend | Evidencia de capas testeadas sin dependencias externas | No se redisenio la arquitectura en esta etapa |
| DevOps/IaC | Uso de Docker, Docker Compose, GitHub Actions, Docker Hub, Terraform y despliegue sandbox como soporte del prototipo | Evidencias de despliegue/observabilidad/DRP y reportes de capitulo | Soporte operativo observado en AWS principal y escenarios limitados | No se presenta como plataforma empresarial productiva |
| Validacion mobile-backend | Revision de configuracion Expo, TypeScript, Expo Doctor, flujo HTTP equivalente y validacion manual cliente movil/web | `03_mobile_backend/REPORTE_MOBILE_BACKEND_100.md` | Integracion CERRADA con checkout `COMPLETED` y listing `ACTIVE` | `EXPO_PUBLIC_API_BASE_URL` es configuracion, no failover |

## Alcance

La metodologia se presenta como encadenamiento de evidencias verificables. Las tablas no agregan nuevas pruebas ni cambian los resultados tecnicos ya cerrados.
