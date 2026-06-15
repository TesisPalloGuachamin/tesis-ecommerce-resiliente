# Reporte de diagramas finales Capitulo 4

## 1. Proposito

Cerrar el bloque de diagramas finales del Capitulo 4 mediante auditoria, correccion y renderizado de las vistas arquitectonicas requeridas. Los diagramas representan el prototipo academico observado y sus limitaciones, no una arquitectura productiva empresarial.

## 2. Fuentes revisadas

Se revisaron las fuentes existentes en:

- `evidencias/arq-diag-01/`
- `evidencias/capitulo4-anexos-finales/`

Las fuentes finales corregidas se generaron en:

- `evidencias/capitulo4-anexos-finales/10_diagramas_finales/fuentes/`

## 3. Diagramas renderizados

| Codigo | Fuente final | PNG | SVG | Estado |
|---|---|---|---|---|
| C4-01 | `fuentes/C4-01-contexto-sistema.mmd` | `renderizados/C4-01-contexto-sistema.png` | `renderizados/C4-01-contexto-sistema.svg` | CERRADO |
| C4-02 | `fuentes/C4-02-contenedores.mmd` | `renderizados/C4-02-contenedores.png` | `renderizados/C4-02-contenedores.svg` | CERRADO |
| C4-03 | `fuentes/C4-03-componentes-core-api.mmd` | `renderizados/C4-03-componentes-core-api.png` | `renderizados/C4-03-componentes-core-api.svg` | CERRADO |
| C4-04 | `fuentes/C4-04-componentes-checkout-service.mmd` | `renderizados/C4-04-componentes-checkout-service.png` | `renderizados/C4-04-componentes-checkout-service.svg` | CERRADO |
| INT-01 | `fuentes/INT-01-mensajeria-rabbitmq.mmd` | `renderizados/INT-01-mensajeria-rabbitmq.png` | `renderizados/INT-01-mensajeria-rabbitmq.svg` | CERRADO |
| INT-02 | `fuentes/INT-02-pago-simulado.mmd` | `renderizados/INT-02-pago-simulado.png` | `renderizados/INT-02-pago-simulado.svg` | CERRADO |
| DEP-01 | `fuentes/DEP-01-despliegue-sandbox.mmd` | `renderizados/DEP-01-despliegue-sandbox.png` | `renderizados/DEP-01-despliegue-sandbox.svg` | CERRADO |
| DRP-ARCH-01 | `fuentes/DRP-ARCH-01-resiliencia-drp.mmd` | `renderizados/DRP-ARCH-01-resiliencia-drp.png` | `renderizados/DRP-ARCH-01-resiliencia-drp.svg` | CERRADO |

## 4. Correcciones realizadas

- C4-01: se corrigio el typo "incia checkout" por "inicia checkout".
- Se reemplazo lenguaje de "Produccion sandbox" por "AWS principal sandbox".
- DEP-01 refleja que Prometheus y Grafana fueron verificados como observabilidad operativa.
- DRP-ARCH-01 clasifica AWS principal como observado principal, AWS alterna como observado con limitaciones, Azure como alcance minimo con limitaciones, y GCP/on-premise como documental.
- Pago aparece como adaptador conceptual/simulado, no como integracion real con pasarela.
- RabbitMQ aparece como mecanismo de desacoplamiento asincrono, no como resiliencia automatica por si mismo.
- Mobile aparece como cliente configurado por `EXPO_PUBLIC_API_BASE_URL`, no como failover automatico.
- Se eliminaron host literales y no se incluyeron credenciales, tokens ni secretos.
- No se agregaron servicios no implementados como Kubernetes, EKS/AKS, API Gateway, Lambda, Cognito, DynamoDB, S3, CloudTrail o SNS.

## 5. Renderizado

Herramienta usada:

```bash
npx --yes @mermaid-js/mermaid-cli@11.4.2
```

Se renderizaron PNG y SVG para los ocho diagramas finales. No hubo diagramas fallidos.

## 6. Limitaciones

- Los diagramas son vistas academicas del prototipo observado.
- No representan SLA productivo.
- No representan active-active ni alta disponibilidad empresarial.
- No representan pagos reales ni integracion financiera externa.
- No representan failover automatico movil.
- GCP y on-premise se mantienen documentales.

## 7. Resultado final

Bloque de diagramas finales del Capitulo 4: `CERRADO`.
