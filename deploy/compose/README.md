# DevOps Local Stack - Quick Start Guide

Este directorio contiene la base DevOps local mínima y versionable para desarrollo y observabilidad.

## Archivos Incluidos

| Archivo | Propósito |
|---------|-----------|
| `.env.example` | Plantilla de variables de entorno |
| `docker-compose.dev.yml` | Orquestación de 7 servicios locales |
| `prometheus/prometheus.yml` | Configuración de scraping de métricas |
| `grafana/provisioning/` | Auto-provisioning de datasources y dashboards |
| `validate.sh` | Script de validación pre-levantamiento |
| `PROMETHEUS_SETUP.md` | Pasos para habilitar scraping de métricas del backend |

## Servicios Incluidos

1. **core-db** (PostgreSQL 15) - Dominio Core API
2. **checkout-db** (PostgreSQL 15) - Dominio Checkout Service
3. **rabbitmq** - Message broker (exchange: `ecommerce.checkout.exchange`)
4. **core-api** - Spring Boot 3.2 (puerto 8080)
5. **checkout-service** - Spring Boot 3.2 (puerto 8082)
6. **prometheus** - Recopilador de métricas (puerto 9090)
7. **grafana** - Visualización de métricas (puerto 3000)

## Quick Start

### 1. Preparar Variables de Entorno
```bash
cp .env.example .env
```

No es necesario modificar `.env` para valores por defecto. Todos los defaults están configurados correctamente.

### 2. Validar Estructura
```bash
bash validate.sh
```

Esto verificará que todos los archivos y directorios necesarios existan.

### 3. Levantar Servicios
```bash
docker compose -f docker-compose.dev.yml up -d
```

### 4. Verificar Estado
```bash
docker compose -f docker-compose.dev.yml ps
```

Espera a que todos los servicios estén en estado `healthy` o `running`.

### 5. Probar Conectividad

**Core API Health:**
```bash
curl http://localhost:8080/actuator/health
```

**Checkout Service Health:**
```bash
curl http://localhost:8082/actuator/health
```

**RabbitMQ Management UI:**
```
http://localhost:15672 (usuario: guest, contraseña: guest)
```

**Prometheus:**
```
http://localhost:9090
```

**Grafana:**
```
http://localhost:3000 (usuario: admin, contraseña: admin123)
```

## Comandos Útiles

### Ver logs de un servicio
```bash
docker compose -f docker-compose.dev.yml logs -f core-api
docker compose -f docker-compose.dev.yml logs -f checkout-service
docker compose -f docker-compose.dev.yml logs -f rabbitmq
```

### Detener servicios
```bash
docker compose -f docker-compose.dev.yml down
```

### Limpiar volúmenes (CUIDADO: elimina datos)
```bash
docker compose -f docker-compose.dev.yml down -v
```

### Reconstruir imágenes
```bash
docker compose -f docker-compose.dev.yml build --no-cache
```

## Observabilidad

### Métricas (Prometheus)
**Estado Actual:** Deshabilitado (ver `PROMETHEUS_SETUP.md`)

Para habilitar scraping de métricas de Core API y Checkout Service:
1. Agregar dependencia `micrometer-registry-prometheus` a ambos `pom.xml`
2. Recompilar servicios: `mvn clean package`
3. Descomentar jobs en `prometheus/prometheus.yml`
4. Reiniciar Prometheus

### Dashboards (Grafana)
Grafana está pre-configurado con:
- Datasource Prometheus auto-provisioned
- Dashboard "Services Overview" con paneles básicos

Para agregar más dashboards:
1. Crear archivo JSON en `grafana/dashboards/`
2. Referenciarlo en `grafana/provisioning/dashboards/dashboards.yml`
3. Reiniciar Grafana

## Troubleshooting

### Healthcheck failed
Si los healthchecks fallan, revisa los logs:
```bash
docker compose -f docker-compose.dev.yml logs core-api
docker compose -f docker-compose.dev.yml logs checkout-service
```

### Puerto ya en uso
Si ves "Address already in use", puedes:
1. Cambiar puertos en `.env`
2. Asegurar que no haya otro compose levantado: `docker ps`

### Conexión a base de datos rechazada
Asegúrate que:
- Las bases de datos están healthy: `docker compose -f docker-compose.dev.yml ps`
- Las credenciales en `.env` coinciden con `application.yml`
- El host en la URL de conexión es el nombre del servicio (e.g., `core-db`, `checkout-db`)

### RabbitMQ no inicia
RabbitMQ requiere ~30s para iniciar. Usa:
```bash
docker compose -f docker-compose.dev.yml logs -f rabbitmq
```

## Variables de Entorno Clave

| Variable | Default | Descripción |
|----------|---------|-------------|
| `POSTGRES_CORE_PORT` | 5432 | Puerto exposición BD core-api |
| `POSTGRES_CHECKOUT_PORT` | 5433 | Puerto exposición BD checkout |
| `RABBITMQ_PORT` | 5672 | Puerto AMQP RabbitMQ |
| `CORE_API_PORT` | 8080 | Puerto Spring Boot Core API |
| `CHECKOUT_SERVICE_PORT` | 8082 | Puerto Spring Boot Checkout |
| `PROMETHEUS_PORT` | 9090 | Puerto Prometheus |
| `GRAFANA_PORT` | 3000 | Puerto Grafana |
| `GRAFANA_ADMIN_PASSWORD` | admin123 | Contraseña Grafana (cambiar en prod) |

## Notas Importantes

- Este compose **convive** con el `docker-compose.yml` previo del backend (raíz del monorepo)
- Usa red bridge local `tesis-network` para aislamiento
- Todos los datos persisten en volúmenes Docker (no se pierden entre reinicios)
- Las credenciales de desarrollo son inseguras. Para producción, usar secrets/variables inyectadas

## Próximos Pasos

1. **Terraform**: Infraestructura cloud (próxima microfase)
2. **GitHub Actions**: CI/CD y automatización (próxima microfase)
3. **Métricas**: Habilitar Prometheus scraping (ver `PROMETHEUS_SETUP.md`)
4. **Dashboards**: Expandir observabilidad en Grafana

## Documentación Relacionada

- [foundation-notes.md](../devops/foundation-notes.md) - Contexto general DevOps
- [PROMETHEUS_SETUP.md](./PROMETHEUS_SETUP.md) - Habilitar métricas Prometheus

