# Prometheus Setup - Pasos para Habilitar Métricas

## Estado Actual
- ✓ Prometheus levantado y funcionando (self-monitoring activo)
- ✓ Core API y Checkout Service levantados
- ✗ Scraping de métricas de servicios Java **DESHABILITADO** (ver razón abajo)

## ¿Por Qué está Deshabilitado?
Los servicios Java exponen métricas en `/actuator/metrics` (formato JSON), pero Prometheus requiere formato Prometheus (texto plano). Sin la dependencia `micrometer-registry-prometheus`, no es posible scrapear métricas del backend.

## Para Habilitar Scraping de Métricas

### 1. Agregar Dependencia Maven
En **ambos** `pom.xml` (`apps/core-api/pom.xml` y `apps/checkout-service/pom.xml`), agregar:

```xml
<!-- Micrometer Prometheus Registry -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

Agregarlo después de `spring-boot-starter-actuator` (hacia línea 53-54).

### 2. Actualizar application.yml (opcional)
Si deseas exponer explícitamente el endpoint `/actuator/prometheus`, agrega a `application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

**NOTA**: Spring Boot Actuator ya expone automáticamente `/actuator/prometheus` si micrometer-prometheus está en el classpath. No es necesario configurar nada adicional en application.yml.

### 3. Recompilar Servicios
```bash
# Core API
cd apps/core-api
mvn clean package

# Checkout Service
cd apps/checkout-service
mvn clean package
```

### 4. Reiniciar Docker Compose
```bash
docker-compose -f deploy/compose/docker-compose.dev.yml down
docker-compose -f deploy/compose/docker-compose.dev.yml up -d
```

### 5. Verificar Scraping en Prometheus
- Acceder a `http://localhost:9090`
- Ir a **Status** → **Targets**
- Deberías ver:
  - `prometheus` → UP
  - `core-api` → UP
  - `checkout-service` → UP

### 6. Descomenta los Scrape Jobs
Una vez que confirmes que las métricas están disponibles, descomenta las líneas en `deploy/compose/prometheus/prometheus.yml`:

```yaml
  - job_name: 'core-api'
    metrics_path: '/actuator/prometheus'
    ...

  - job_name: 'checkout-service'
    metrics_path: '/actuator/prometheus'
    ...
```

Luego reinicia Prometheus (dentro del compose o manualmente).

## Endpoints Disponibles Después de Habilitar
- Core API: `http://localhost:8080/actuator/prometheus` (métricas en formato Prometheus)
- Checkout Service: `http://localhost:8082/actuator/prometheus` (métricas en formato Prometheus)
- Prometheus: `http://localhost:9090` (UI para consultar métricas)

## Referencia Rápida
- [Micrometer Documentation](https://micrometer.io/docs/registry/prometheus)
- [Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

