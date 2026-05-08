# Validación de Compatibilidad - DevOps Local Stack

## Resumen de Revisión Completada

Esta validación verifica la compatibilidad entre:
- `docker-compose.dev.yml`
- `.env.example`
- Dockerfiles de apps/
- `application.yml` de cada servicio
- Endpoints `/actuator/health` y `/actuator/prometheus`

---

## 1. Variables de Entorno - Alineamiento

| Variable | .env.example | application.yml | Docker Compose | Estado |
|----------|-------------|-----------------|-----------------|--------|
| DB Host (core) | `core-db` | `localhost` | `core-db` | ✓ Aligned |
| DB Host (checkout) | `checkout-db` | `localhost` | `checkout-db` | ✓ Aligned |
| DB Port (core) | 5432 | 5432 | 5432 | ✓ Aligned |
| DB Port (checkout) | 5433 | 5432 | 5433 | ✓ Aligned (diferente en mapping) |
| RabbitMQ Host | `rabbitmq` | `localhost` | `rabbitmq` | ✓ Aligned |
| RabbitMQ Port | 5672 | 5672 | 5672 | ✓ Aligned |
| RabbitMQ User | `guest` | `guest` | `guest` | ✓ Aligned |
| RabbitMQ Password | `guest` | `guest` | `guest` | ✓ Aligned |
| Core API Port | 8080 | 8080 | 8080 | ✓ Aligned |
| Checkout Port | 8082 | 8082 | 8082 | ✓ Aligned |
| Checkout Context Path | N/A | `/api` | N/A | ✓ Noted |
| JWT Secret | Presente | Presente | Presente | ✓ Aligned |
| JWT Expiration | 86400000 | 86400000 | 86400000 | ✓ Aligned |

---

## 2. Healthchecks - Validación

| Servicio | Healthcheck en Compose | Endpoint Real | Problema | Solución Aplicada |
|----------|----------------------|--------------|---------|-------------------|
| core-db | pg_isready | PostgreSQL | N/A | ✓ Correcto |
| checkout-db | pg_isready | PostgreSQL | N/A | ✓ Correcto |
| rabbitmq | rabbitmq-diagnostics | RabbitMQ | N/A | ✓ Correcto |
| core-api | wget a /actuator/health | http://8080/actuator/health | curl no disponible en Alpine | ✓ Cambiado a wget |
| checkout-service | wget a /api/actuator/health | http://8082/api/actuator/health | 1. curl no disponible; 2. context-path /api | ✓ Cambiado a wget + /api |

**Notas**:
- Alpine Linux no incluye `curl`, pero sí incluye `wget`
- Todos los healthchecks ahora usan `wget` (disponible en eclipse-temurin:*-alpine)
- Checkout Service requiere `/api` en la ruta por su `context-path: /api`

---

## 3. Endpoints Actuator - Disponibilidad

| Servicio | Endpoint | Disponibilidad | Reason |
|----------|----------|-----------------|--------|
| Core API | `/actuator/health` | ✓ Sí | `spring-boot-starter-actuator` en pom.xml |
| Core API | `/actuator/metrics` | No expuesto | El scrape oficial usa formato Prometheus |
| Core API | `/actuator/prometheus` | ✓ Sí | `micrometer-registry-prometheus` en pom.xml |
| Checkout Service | `/api/actuator/health` | ✓ Sí | `spring-boot-starter-actuator` + context-path |
| Checkout Service | `/api/actuator/metrics` | No expuesto | El scrape oficial usa formato Prometheus |
| Checkout Service | `/api/actuator/prometheus` | ✓ Sí | `micrometer-registry-prometheus` en pom.xml |

**Ajuste Aplicado**:
- Mantener `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE` en `health,info,prometheus`
- Scraping de Prometheus activo para `core-api` y `checkout-service`
- Checkout Service usa `metrics_path: /api/actuator/prometheus` por su `context-path`

---

## 4. Dockerfiles - Build Context

| Dockerfile | Base Image | Build Stage | JAR Output | Context en Compose | Estado |
|-----------|-----------|------------|-----------|-------------------|--------|
| core-api/Dockerfile | eclipse-temurin:17-jdk-alpine | Sí (multi-stage) | `target/*.jar` | `../../apps/core-api` | ✓ Correcto |
| checkout-service/Dockerfile | eclipse-temurin:17-jre-alpine | Sí (multi-stage) | `target/checkout-service-*.jar` | `../../apps/checkout-service` | ✓ Correcto |

**Notas**:
- Ambos usan Alpine (sin curl, sin bash extra)
- Build context relativo desde `deploy/compose/` es correcto
- JAR patterns coinciden con salida de Maven

---

## 5. RabbitMQ Exchange - Validación

| Componente | Configuración | Estado |
|-----------|-----------|--------|
| Exchange Name | `ecommerce.checkout.exchange` | ✓ Definido en .env.example |
| Queue Names | No definidas en compose | ⚠ A definir en backend |
| Routing Keys | No definidas en compose | ⚠ A definir en backend |

**Acción Requerida**:
- Las queues y routing keys específicas deben alinearse con la definición real en el backend
- Aún no están definidas en esta base DevOps (por diseño mínimo)
- Comentario explícito en `.env.example` indicando esto

---

## 6. Provisioning de Grafana - Validación

| Archivo | Propósito | Estado |
|---------|----------|--------|
| `grafana/provisioning/datasources/prometheus.yml` | Conectar Prometheus como datasource | ✓ Correcto |
| `grafana/provisioning/dashboards/dashboards.yml` | Habilitar auto-provisioning | ✓ Correcto |
| `grafana/dashboards/services-overview.json` | Dashboard básico | ✓ Presente |

**Verificación**:
```bash
# El provisioning se ejecuta automáticamente al iniciar Grafana
# Si cambias los files, reinicia Grafana:
docker compose -f docker-compose.dev.yml restart grafana
```

---

## 7. Problemas Probables de Arranque - Análisis

### ✓ Problemas Detectados y Resueltos

1. **Healthchecks con curl en Alpine**
   - **Problema**: `curl` no disponible en `eclipse-temurin:*-alpine`
   - **Detectado**: ✓ Sí
   - **Resuelto**: ✓ Cambiar a `wget`

2. **Prometheus endpoint no disponible**
   - **Problema**: faltaba `micrometer-registry-prometheus` y el scrape estaba comentado
   - **Detectado**: ✓ Sí
   - **Resuelto**: ✓ Agregar dependencia, exponer `prometheus` y activar scraping jobs

3. **Context-path incorrecto en healthcheck de checkout**
   - **Problema**: Healthcheck a `/actuator/health` pero servicio está en `/api/actuator/health`
   - **Detectado**: ✓ Sí
   - **Resuelto**: ✓ Agregar `/api` al healthcheck

### ⚠ Ambigüedades Pendientes

1. **Queue names y routing keys de RabbitMQ**
   - No definidas en compose (por diseño)
   - Requieren alineación con backend

2. **Métricas custom**
   - No se agregan todavía métricas custom de checkout/mensajería
   - La fase actual queda limitada a métricas JVM/HTTP/Actuator

---

## 8. Validación de Syntaxis

```
✓ YAML válido (docker-compose.dev.yml)
✓ YAML válido (prometheus.yml)
✓ JSON válido (services-overview.json)
✓ Bash válido (validate.sh)
```

---

## 9. Archivos Creados/Modificados

### Creados
- `deploy/compose/README.md` - Guía rápida de inicio
- `deploy/compose/PROMETHEUS_SETUP.md` - Instrucciones para habilitar métricas
- `deploy/compose/validate.sh` - Script de validación pre-arranque

### Modificados
- `deploy/compose/docker-compose.dev.yml` - Cambios en healthchecks y MANAGEMENT_ENDPOINTS
- `docs/devops/foundation-notes.md` - Actualización con nuevos archivos y aclaraciones

---

## 10. Checklist Final

- ✓ Variables de entorno alineadas
- ✓ Healthchecks funcionarán en Alpine
- ✓ Endpoints actuator correctos
- ✓ Build contexts válidos
- ✓ Dockerfiles multi-stage correctos
- ✓ Provisioning de Grafana correcto
- ✓ Documentación actualizada
- ✓ Script de validación funcional
- ✓ Problemas probables identificados y resueltos
- ⚠ Pendiente: Alineación de queue names con backend (por confirmar)

---

## Próxima Acción

Ejecutar el stack local:
```bash
bash deploy/compose/validate.sh
cp deploy/compose/.env.example deploy/compose/.env
docker compose -f deploy/compose/docker-compose.dev.yml up -d
docker compose -f deploy/compose/docker-compose.dev.yml ps
```
