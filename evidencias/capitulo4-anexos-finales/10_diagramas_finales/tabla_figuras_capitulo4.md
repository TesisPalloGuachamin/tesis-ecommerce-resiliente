# Tabla figura - proposito - evidencia Capitulo 4

| Figura sugerida | Diagrama | Proposito en la tesis | Evidencia relacionada | Seccion sugerida | Estado |
|---|---|---|---|---|---|
| Figura C4-01. Diagrama de contexto del sistema | `C4-01-contexto-sistema.png` / `.svg` | Presentar actores, sistema, AWS principal sandbox y pago simulado dentro del alcance academico | `03_mobile_backend/`; `01_funcionales_compra/`; `02_funcionales_venta/` | 4.1 Arquitectura general del prototipo | CERRADO |
| Figura C4-02. Diagrama de contenedores | `C4-02-contenedores.png` / `.svg` | Mostrar app movil, `core-api`, `checkout-service`, RabbitMQ, PostgreSQL, Prometheus y Grafana | `04_rabbitmq/`; `07_observabilidad/`; `03_mobile_backend/` | 4.1 Arquitectura de contenedores | CERRADO |
| Figura C4-03. Componentes de `core-api` | `C4-03-componentes-core-api.png` / `.svg` | Explicar arquitectura hexagonal de API publica, controladores, casos de uso, dominio, puertos y adapters | `05_pruebas_backend/`; `REPORTE_COBERTURA_JACOCO_BACKEND.md`; `01_funcionales_compra/`; `02_funcionales_venta/` | 4.2 Componentes backend `core-api` | CERRADO |
| Figura C4-04. Componentes de `checkout-service` | `C4-04-componentes-checkout-service.png` / `.svg` | Explicar procesamiento desacoplado, idempotencia, persistencia y pago simulado | `04_rabbitmq/`; `05_pruebas_backend/`; `INT-02` | 4.2 Componentes backend `checkout-service` | CERRADO |
| Figura INT-01. Mensajeria asincrona RabbitMQ | `INT-01-mensajeria-rabbitmq.png` / `.svg` | Describir publicacion, consumo, actualizacion de estado y polling hasta `COMPLETED` | `04_rabbitmq/reporte_rabbitmq_100.md`; `01_funcionales_compra/` | 4.3 Integracion asincrona | CERRADO |
| Figura INT-02. Adaptador conceptual de pago simulado | `INT-02-pago-simulado.png` / `.svg` | Aclarar que el pago es simulado, in-process y sin integracion real con pasarela | `04_rabbitmq/`; `05_pruebas_backend/`; `03_mobile_backend/` | 4.3 Integracion de pago simulado | CERRADO |
| Figura DEP-01. Despliegue final sandbox | `DEP-01-despliegue-sandbox.png` / `.svg` | Mostrar despliegue Docker Compose sobre AWS principal sandbox y observabilidad operativa | `07_observabilidad/`; `06_carga/`; `08_drp/` | 4.4 Despliegue y soporte operativo | CERRADO |
| Figura DRP-ARCH-01. Vista de resiliencia y clasificacion DRP | `DRP-ARCH-01-resiliencia-drp.png` / `.svg` | Diferenciar escenarios observados, limitados y documentales sin presentar SLA ni active-active | `08_drp/REPORTE_DRP_METRICAS_100.md`; `matriz_drp_final.md` | 4.5 Resiliencia y DRP | CERRADO |

## Uso recomendado

Usar los PNG para insercion directa en el documento y conservar los SVG como version escalable para ajustes editoriales. Las fuentes Mermaid finales quedan en `fuentes/` para trazabilidad.
