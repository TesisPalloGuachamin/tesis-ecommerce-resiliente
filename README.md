# E-commerce Resiliente - Monorepo Tesis

Prototipo académico de plataforma de e-commerce con arquitectura hexagonal, microservicios y patrones de resiliencia.

## Estructura Rápida

```
apps/              → Servicios backend (Spring Boot 21)
  core-api/        → API principal (usuarios, productos, carrito, checkout)
  checkout-service → Procesamiento asíncrono de pagos (RabbitMQ, idempotencia)

infra/docker/      → Dockerfiles y configuración de infraestructura
docs/              → Documentación (MONOREPO.md, EJECUCION.txt, REVISION_ARQUITECTURA.txt)
scripts/           → Scripts de utilidad (vacío, para futuro uso)

docker-compose.yml → Orquestación local (PostgreSQL 15, RabbitMQ 3.12)
.gitignore         → Configuración de Git
```

## Inicio Rápido

### Requisitos
- Docker & Docker Compose
- O: Java 21, Maven 3.8+, PostgreSQL 15, RabbitMQ 3.12

### Con Docker Compose (recomendado)
```bash
docker-compose up
```

Espera a que todos los servicios estén healthy:
- PostgreSQL: http://localhost:5432
- RabbitMQ: http://localhost:15672 (guest:guest)
- Core API: http://localhost:8080
- Checkout Service: http://localhost:8082

### Local (sin Docker)
```bash
# Terminal 1: Core API
cd apps/core-api
mvn spring-boot:run

# Terminal 2: Checkout Service
cd apps/checkout-service
mvn spring-boot:run
```

## Compilación Individual

```bash
# Core API
cd apps/core-api && mvn clean package

# Checkout Service
cd apps/checkout-service && mvn clean package
```

## Documentación Completa

Ver `docs/MONOREPO.md` para configuración avanzada, estructura interna y notas arquitectónicas.

**Roadmap & Scope:**
- `docs/ROADMAP.md` - Próximos pasos, fases de desarrollo, timeline, criterios de éxito
- `docs/SCOPE.md` - Qué está dentro/fuera del alcance actual, checkpoints por fase

**Documentación generada (auditoría anterior):**
- `docs/EJECUCION.txt` - Instrucciones de ejecución iniciales
- `docs/REVISION_ARQUITECTURA.txt` - Análisis arquitectónico completo

## Notas para Integración

- **JWT**: Firmado con HS512, stores userId como UUID
- **Eventos**: RabbitMQ topic exchange `ecommerce.checkout.exchange`
- **Idempotencia**: InboxEvent en checkout-service, OutboxEvent en core-api
- **BD**: `core_db` y `checkout_db` separadas en PostgreSQL
- **Versión Java**: 21 (OpenJDK)
- **Spring Boot**: 3.2.0

## Próximos Pasos (No en esta tarea)

- [ ] Implementar OutboxPollingScheduler en core-api
- [ ] Alinear payload de CheckoutCompletedEvent
- [ ] Crear script multi-BD para docker-compose
- [ ] Agregar tests e2e con Testcontainers
- [ ] DevOps: Infraestructura preparada para la siguiente fase de DevOps (Docker, CI/CD, IaC y observabilidad)


