# Reporte de cobertura JaCoCo backend

## 1. Proposito

Este reporte agrega evidencia complementaria de cobertura JaCoCo para los servicios backend. La evidencia no sustituye las pruebas funcionales, unitarias ni de integracion acotada ya cerradas para el Capitulo 4. Tampoco representa una certificacion productiva ni una garantia de calidad empresarial.

La configuracion se incorporo de forma minima para generar reportes de cobertura academicos, sin reglas de enforcement y sin umbrales que fallen el build por porcentaje.

## 2. Servicios evaluados

- `core-api`
- `checkout-service`

## 3. Configuracion Maven

JaCoCo no existia previamente en los `pom.xml` revisados. Se agrego `jacoco-maven-plugin` version `0.8.12` en:

- `apps/core-api/pom.xml`
- `apps/checkout-service/pom.xml`

Ejecuciones configuradas:

- `prepare-agent`
- `report`

No se agregaron reglas de cobertura minima.

## 4. Comandos ejecutados

```bash
cd apps/core-api && mvn clean test jacoco:report
cd apps/checkout-service && mvn clean test jacoco:report
```

## 5. Pruebas agregadas para mejora de cobertura

### `core-api`

- `CoreMapperTest`: mapeo de `Product`, `User`, `Cart`, `CartItem` y `CheckoutRequest`.
- `CoreDomainModelTest`: calculo de totales y estados iniciales de modelos de dominio.
- `CartAndCheckoutUseCaseTest`: carrito, checkout, errores controlados y publicacion mockeada de evento.
- `UserAuthUseCaseTest`: login, credenciales invalidas y consulta de usuario actual.

### `checkout-service`

- `CheckoutDomainModelTest`: modelos `CheckoutRequest`, `Order`, `OrderItem`, `PaymentAttempt` e `InboxEvent`.
- `CheckoutEventDtoTest`: DTOs/eventos de checkout y pago simulado.
- Nuevos casos en `ProcessCheckoutUseCaseTest`: pago fallido, evento sin items e inconsistencia de idempotencia.

## 6. Resultado anterior vs final

| Servicio | Pruebas antes | Pruebas final | Build final | Clases JaCoCo | Lineas antes | Lineas final | Instrucciones antes | Instrucciones final | Ramas final | Metodos final | Meta lineas | Estado meta |
|---|---:|---:|---|---:|---:|---:|---:|---:|---:|---:|---:|---|
| `core-api` | 11 | 29 | PASS | 89 | 20.77% | 50.76% | 20.48% | 46.67% | 40.12% | 46.00% | 35.00% | ALCANZADA |
| `checkout-service` | 4 | 13 | PASS | 31 | 37.74% | 54.19% | 19.33% | 29.63% | 3.06% | 51.40% | 50.00% | ALCANZADA |

## 7. Evidencia generada

Directorio base:

- `evidencias/capitulo4-anexos-finales/05_pruebas_backend/cobertura_jacoco/`

Archivos principales:

- `core-api-jacoco.log`
- `checkout-service-jacoco.log`
- `core-api-jacoco-after-improvement.log`
- `checkout-service-jacoco-after-improvement.log`
- `core-api-jacoco-summary.md`
- `checkout-service-jacoco-summary.md`
- `core-api-jacoco-summary-after-improvement.md`
- `checkout-service-jacoco-summary-after-improvement.md`
- `core-api/jacoco.csv`
- `core-api/jacoco.xml`
- `core-api/index.html`
- `checkout-service/jacoco.csv`
- `checkout-service/jacoco.xml`
- `checkout-service/index.html`

Reportes fuente generados por Maven:

- `apps/core-api/target/site/jacoco/index.html`
- `apps/core-api/target/site/jacoco/jacoco.csv`
- `apps/core-api/target/site/jacoco/jacoco.xml`
- `apps/checkout-service/target/site/jacoco/index.html`
- `apps/checkout-service/target/site/jacoco/jacoco.csv`
- `apps/checkout-service/target/site/jacoco/jacoco.xml`

## 8. Interpretacion tecnica

La ejecucion posterior confirma que las pruebas automatizadas siguen pasando con el agente JaCoCo activo y que la cobertura aumento en ambos servicios. La mejora se realizo agregando pruebas unitarias y de aplicacion con mocks sobre mappers, modelos de dominio, DTOs/eventos y casos de uso, sin depender de PostgreSQL local, RabbitMQ real, AWS, Docker ni red externa.

La cobertura observada es complementaria: permite visualizar que partes del codigo fueron ejercitadas por las pruebas existentes y nuevas, pero no debe leerse como cierre absoluto de calidad ni como medicion de los escenarios funcionales, de carga, observabilidad, DRP o mobile.

## 9. Limitaciones

- La cobertura mide solo las pruebas automatizadas existentes.
- No se impone umbral minimo de cobertura.
- No mide pruebas manuales ni evidencias funcionales ya cerradas.
- No mide carga/rendimiento, DRP, observabilidad, mobile ni Expo.
- La evidencia se usa como refuerzo academico del bloque de pruebas backend.

## 10. Resultado final

Evidencia complementaria JaCoCo backend: `GENERADA Y REFORZADA`.

Metas orientativas de lineas:

- `core-api`: 35% requerido como meta orientativa; resultado final 50.76%; estado `ALCANZADA`.
- `checkout-service`: 50% requerido como meta orientativa; resultado final 54.19%; estado `ALCANZADA`.
